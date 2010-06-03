package aimax.osm.routing;

import java.util.ArrayList;
import java.util.List;

import aima.core.agent.Action;
import aimax.osm.data.Position;
import aimax.osm.data.entities.MapNode;
import aimax.osm.data.entities.MapWay;

public class OsmMoveAction implements Action {
	private MapWay way;
	private int fromIndex;
	private int toIndex;
	
	public OsmMoveAction(MapWay way, MapNode fromNode, MapNode toNode) {
		this.way = way;
		fromIndex = way.getNodes().indexOf(fromNode);
		toIndex = way.getNodes().indexOf(toNode);
	}
	
	public MapWay getWay() {
		return way;
	}
	
	public MapNode getFrom() {
		return way.getNodes().get(fromIndex);
	}
	
	public MapNode getTo() {
		return way.getNodes().get(toIndex);
	}
	
	public List<MapNode> getNodes() {
		int size = Math.abs(toIndex-fromIndex)+1;
		List<MapNode> result = new ArrayList<MapNode>(size);
		for (int i = 0; i < size; i++)
			result.add(way.getNodes().get
					(fromIndex < toIndex ? fromIndex+i : fromIndex-i));
		return result;
	}
	
	public float getTravelDistance() {
		float result = 0f;
		int size = Math.abs(toIndex-fromIndex)+1;
		List<MapNode> nodes = way.getNodes();
		Position pos = new Position(nodes.get(fromIndex));
		for (int i = 1; i < size; i++) {
			MapNode next =
				nodes.get(fromIndex < toIndex ? fromIndex+i : fromIndex-i);
			result+= pos.getDistKM(next);
			pos = new Position(next);
		}
		return result;
	}
	
	@Override
	public boolean isNoOp() {
		return false;
	}
	public String toString() {
		return "OsmMove[to=" + getTo().getId() + "]";
	}
}
