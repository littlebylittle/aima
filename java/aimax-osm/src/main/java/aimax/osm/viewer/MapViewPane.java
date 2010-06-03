package aimax.osm.viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import aimax.osm.data.BoundingBox;
import aimax.osm.data.MapDataEvent;
import aimax.osm.data.MapDataEventListener;
import aimax.osm.data.MapDataStore;
import aimax.osm.data.Position;
import aimax.osm.data.entities.MapEntity;

/**
 * Provides a panel which shows map data. As model, a
 * {@link aimax.osm.data.MapDataStore} is used.
 * <p>Hint for using the viewer: Try Mouse-Left, Mouse-Right, Mouse-Drag,
 * Ctrl-Mouse-Left, Plus, Minus, Ctrl-Plus, Ctrl-Minus, arrow buttons,
 * and also the Mouse-Wheel for navigation, mark setting, and track
 * definition.
 * @author R. Lunde
 */
public class MapViewPane extends JComponent implements MapDataEventListener {
	private Logger LOG = Logger.getLogger("aimax.osm");
	/** Maintains a reference to the model which provides the data to be displayed. */
	protected MapDataStore mapData;
	protected CoordTransformer transformer;
	private AbstractEntityRenderer renderer;
	private ArrayList<MapViewEventListener> eventListeners;
	protected boolean isAdjusted;
	protected JPopupMenu popup;
	
	public MapViewPane() {
		transformer = new CoordTransformer();
		renderer = new DefaultEntityRenderer();
		eventListeners = new ArrayList<MapViewEventListener>();
		isAdjusted = false;
		popup = createPopup();
		MyMouseListener mouseListener = new MyMouseListener();
		addMouseListener(mouseListener);
		addMouseWheelListener(mouseListener);
		addKeyListener(new MyKeyListener());
		this.setFocusable(true);
	}
	
	/** Responsible for creating the popup menu. */
	protected JPopupMenu createPopup() {
	    return new MapViewPopup();
	}
	
	public MapDataStore getModel() {
		return mapData;
	}
	
	/** Stores the model and initiates painting. */
	public void setModel(MapDataStore mapData) {
		if (this.mapData != null)
			this.mapData.removeMapDataEventListener(this);
		this.mapData = mapData;
		if (mapData != null) {
			mapData.addMapDataEventListener(this);
			isAdjusted = false;
		}
		fireMapViewEvent(new MapViewEvent(this, MapViewEvent.Type.MAP_NEW));
	}
	
	public AbstractEntityRenderer getRenderer() {
		return renderer;
	}
	
	/** Allows to replace the renderer. */
	public void setRenderer(AbstractEntityRenderer renderer) {
		this.renderer = renderer;
		repaint();
	}
	
	/** Returns the component responsible for coordinate transformation. */
	public CoordTransformer getTransformer() {
		return transformer;
	}
	
	/**
	 * Multiples the current scale with the specified factor and
	 * adjusts the view so that the objects shown at the
	 * specified view focus keep at their position.
	 */
	public void zoom(float factor, int focusX, int focusY) {
		transformer.zoom(factor, focusX, focusY);
		repaint();
		fireMapViewEvent(new MapViewEvent(this, MapViewEvent.Type.ZOOM));
	}
	
	/**
	 * Adjusts the view.
	 * @param dx Number of pixels for horizontal shift.
	 * @param dy Number of pixels for vertical shift.
	 */
	public void adjust(int dx, int dy) {
		transformer.adjust(dx, dy);
		repaint();
		fireMapViewEvent(new MapViewEvent(this, MapViewEvent.Type.ADJUST));
	}
	
	/**
	 * Initiates a reset of all coordinate transformation parameters.
	 */
	public void adjustToFit() {
		isAdjusted = false;
		repaint();
		fireMapViewEvent(new MapViewEvent(this, MapViewEvent.Type.ADJUST));
	}
	
	/**
	 * Puts a position given in world coordinates into the center of the view.
	 * @param lat Latitude
	 * @param lon Longitude
	 */
	public void adjustToCenter(double lat, double lon) {
		int dx = getWidth() / 2 - transformer.x(lon);
		int dy = getHeight() / 2 - transformer.y(lat);
		adjust(dx, dy);
	}
	
	/**
	 * Returns the world coordinates, which are currently shown in the center.
	 */
	public Position getCenterPosition() {
		float lat = transformer.lat(getHeight() / 2);
		float lon = transformer.lon(getWidth() / 2);
		return new Position(lat, lon);
	}
	
	/**
	 * Shows a graphical representation of the provided map data.
	 */
	public void paint(Graphics g) {
	    Graphics2D g2 = (java.awt.Graphics2D) g;
	    g2.setBackground(renderer.getBackgroundColor());
	    g2.clearRect(0, 0, getWidth(), getHeight());
	    if (getWidth() > 0 && mapData != null) {
	    	if (!isAdjusted) { 
	    		transformer.adjustTransformation(mapData.getBoundingBox(), getWidth(), getHeight());
	    		isAdjusted = true;
	    	}
	    	float latMin = transformer.lat(getHeight());
	    	float lonMin = transformer.lon(0);
	    	float latMax = transformer.lat(0);
	    	float lonMax = transformer.lon(getWidth());
	    	float scale = transformer.getScale();
	    	BoundingBox vbox = new BoundingBox(latMin, lonMin, latMax, lonMax);
	    	float viewScale = scale / renderer.getDisplayFactor();
	    	renderer.initForRendering(g2, transformer);
    		if (mapData.getEntityTree() != null)
    			mapData.getEntityTree().visitEntities
    			(renderer, vbox, viewScale);
    		for (MapEntity entity : mapData.getVisibleMarksAndTracks(viewScale))
    			entity.accept(renderer);
    		renderer.printBufferedObjects();
    		if (renderer.isDebugModeEnabled()) {
    			List<double[]> splits = mapData.getEntityTree().getSplitCoords();
    			g2.setColor(Color.LIGHT_GRAY);
    			g2.setStroke(new BasicStroke(1f));
    			for (double[] split : splits)
    				g2.drawLine(
    						renderer.transformer.x(split[1]),
    						renderer.transformer.y(split[0]),
    						renderer.transformer.x(split[3]),
    						renderer.transformer.y(split[2]));
    		}
	    }
	}
	
	public void addMapViewEventListener(MapViewEventListener listener) {
		eventListeners.add(listener);
	}
	
	/**
	 * Informs all interested listener about view events such as
	 * mouse events and data changes.
	 */
	public void fireMapViewEvent(MapViewEvent e) {
		for (MapViewEventListener listener : eventListeners)
			listener.eventHappened(e);
	}
	
	/**
	 * Defines, how to react on model events (new, modifications).
	 */
	@Override
	public void eventHappened(MapDataEvent event) {
		if (event.getType() == MapDataEvent.Type.MAP_NEW) {
			adjustToFit();
			fireMapViewEvent(new MapViewEvent(this, MapViewEvent.Type.MAP_NEW));
		} else {
			repaint();
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////
	// some inner classes...

	/**
	 * Defines reactions on mouse events including navigation, mark setting
	 * and track creation.
	 * @author R. Lunde
	 *
	 */
	private class MyMouseListener implements MouseListener, MouseWheelListener {
		int xp;
		int yp;
		@Override
		public void mouseClicked(MouseEvent arg0) {}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			MapViewPane.this.requestFocusInWindow();
		}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			xp = e.getX();
			yp = e.getY();
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				int xr = e.getX();
				int yr = e.getY();
				if (xr != xp || yr != yp) {
					// mouse drag -> adjust view
					adjust(xr-xp, yr-yp);
				} else {
					float lat = transformer.lat(yr);
					float lon = transformer.lon(xr);
					if ((e.getModifiers() & MouseEvent.CTRL_MASK) == 0) {
						// mouse left button -> add mark
						mapData.addMark(lat, lon);
						fireMapViewEvent
						(new MapViewEvent(MapViewPane.this, MapViewEvent.Type.MARK_ADDED));
					} else {
						// mouse left button + shift -> add track point
						mapData.addToTrack("Mouse Track", new Position(lat, lon));
						fireMapViewEvent
						(new MapViewEvent(MapViewPane.this, MapViewEvent.Type.TRK_PT_ADDED));
					}
				}
			} else if (popup != null) {
				popup.show(MapViewPane.this, e.getX(), e.getY());
			} else {
				// mouse right button -> clear marks and tracks
				mapData.clearMarksAndTracks();
				fireMapViewEvent(new MapViewEvent
						(MapViewPane.this, MapViewEvent.Type.TMP_NODES_REMOVED));
			}
				
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int rot = e.getWheelRotation();
			int x = e.getX();
			int y = e.getY();
			if (rot == -1)
				zoom(1.5f, x, y);
			else if (rot == 1)
				zoom(1 / 1.5f , x, y);
		}
	}
	
	/**
	 * Enables map navigation (zooming, adjustment) with the keyboard. 
	 * @author R. Lunde
	 */
	private class MyKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_PLUS:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					renderer.setDisplayFactor(renderer.getDisplayFactor()*1.5f);
					repaint();
				} else
					zoom(1.5f, getWidth()/2, getHeight()/2); break;
			case KeyEvent.VK_MINUS:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					renderer.setDisplayFactor(renderer.getDisplayFactor()/1.5f);
					repaint();
				} else
					zoom(1/1.5f, getWidth()/2, getHeight()/2); break;
			case KeyEvent.VK_SPACE:
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) == 0)
					zoom(1.5f, getWidth()/2, getHeight()/2);
				else
					zoom(1/1.5f, getWidth()/2, getHeight()/2); break;
			case KeyEvent.VK_LEFT:
				adjust((int) (0.3 * getWidth()), 0); break;
			case KeyEvent.VK_RIGHT:
				adjust((int) (-0.3 * getWidth()), 0); break;
			case KeyEvent.VK_UP:
				adjust(0, (int) (0.3 * getHeight())); break;
			case KeyEvent.VK_DOWN:
				adjust(0, (int) (-0.3 * getHeight())); break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyTyped(KeyEvent e) {}
	}
}
