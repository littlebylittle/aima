package aimax.osm.routing.mapagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import aima.core.environment.map.Map;
import aima.core.util.Util;
import aima.core.util.datastructure.Point2D;
import aimax.osm.data.MapDataStore;
import aimax.osm.data.MapWayFilter;
import aimax.osm.data.Position;
import aimax.osm.data.entities.MapNode;
import aimax.osm.data.entities.MapWay;
import aimax.osm.data.entities.MapNode.WayRef;

/**
 * Adapter class which provides an aima-core <code>Map</code> interface for
 * OSM data. This enables map environments to access real maps which are
 * generated from OSM data. Note that location strings are dynamically
 * generated from map node ids (long values), so always use the equal
 * method for comparison.
 * @author R. Lunde
 */
public class OsmMapAdapter implements Map {

	/** A map which is generated from OSM data. */
	MapDataStore mapData;
	/**
	 * A filter, which hides some of the ways
	 * (e.g. foot ways are irrelevant when traveling by car.).
	 */
	MapWayFilter filter;
	/**
	 * Controls whether a way which is marked as one-way can be traveled
	 * in both directions.
	 */
	boolean ignoreOneways;
	
	public OsmMapAdapter(MapDataStore mapData) {
		this.mapData = mapData;
	}
	
	public void ignoreOneways(boolean state) {
		ignoreOneways = state;
	}
	
	public void setMapWayFilter(MapWayFilter filter) {
		this.filter = filter;
	}
	
	public MapDataStore getMapData() {
		return mapData;
	}
	
	@Override
	public Double getDistance(String fromLocation, String toLocation) {
		MapNode node1 = getWayNode(fromLocation);
		MapNode node2 = getWayNode(toLocation);
		if (node1 != null && node2 != null &&
				getLocationsLinkedTo(fromLocation).contains(toLocation))
			return new Position(node1).getDistKM(node2);
		else
			return null;
	}

	@Override
	public List<String> getLocations() {
		List<String> result = new ArrayList<String>();
		for (MapNode node : mapData.getWayNodes()) {
			boolean relevant = false;
			for (WayRef wref : node.getWays()) {
				if (filter == null || filter.isAccepted(wref.wayId)) {
					relevant = true;
					break;
				}
			}
			if (relevant) {
				result.add(Long.toString(node.getId()));
			}
		}
		return result;
	}

	@Override
	public List<String> getLocationsLinkedTo(String fromLocation) {
		List<String> result = new ArrayList<String>();
		MapNode node = getWayNode(fromLocation);
		if (node != null) {
			for (WayRef wref : node.getWays()) {
				if (filter == null || filter.isAccepted(wref.wayId)) {
					MapWay way = mapData.getWay(wref.wayId);
					int nodeIdx = wref.nodeIdx;
					List<MapNode> wayNodes = way.getNodes();
					MapNode next;
					if (wayNodes.size() > nodeIdx+1) {
						next = wayNodes.get(nodeIdx+1);
						result.add(Long.toString(next.getId()));
					}
					if (nodeIdx > 0 && (!way.isOneway() || ignoreOneways)) {
						next = wayNodes.get(nodeIdx-1);
						result.add(Long.toString(next.getId()));
					}
				}
			}
		}
		return result;
	}

	/** Returns a <code>PointLatLon</code> instance. */
	@Override
	public Point2D getPosition(String loc) {
		MapNode node = getWayNode(loc);
		if (node != null)
			return new PointLatLon(node.getLat(), node.getLon());
		else
			return null;
	}

	@Override
	public String randomlyGenerateDestination() {
		return Util.selectRandomlyFromList(getLocations());
	}

	/**
	 * Returns the ID of the way node in the underlying OSM map
	 * which is nearest with respect to the specified coordinates
	 * and additionally passes the filter.
	 */
	public String getNearestLocation(Point2D pt) {
		Position pos = new Position((float) pt.getY(), (float) pt.getX());
		Collection<MapNode> rNodes = mapData.getWayNodes();
		MapNode node = pos.selectNearest(rNodes, filter);
		return (node != null) ? Long.toString(node.getId()) : null;
	}
	
	/** Returns the OSM way node corresponding to the given location string. */ 
	public MapNode getWayNode(String id) {
		MapNode result = null;
		try {
			result = mapData.getWayNode(Long.parseLong(id));
		} catch (NumberFormatException e) {
			// node not found, indicated by return value null.
		}
		return result;
	}
}
