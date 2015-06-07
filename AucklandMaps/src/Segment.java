

import java.util.*;
import java.awt.Graphics;
import java.awt.Point;

public class Segment {
	
	private final int roadID;
	
	private final double length;
	
	private final int nodeID1;
	private final int nodeID2;
	private List<Location> locations;
	
	public Segment(Integer roadID, double length, Integer nodeID1, Integer nodeID2){
		this.roadID = roadID;
		this.length = length;
		this.nodeID1 = nodeID1;
		this.nodeID2 = nodeID2;
		this.locations = new ArrayList<>();
	}
	
	void addLocation(Location loc){
		locations.add(loc);
		
	}
	
	int getRoadID(){
		return roadID;
	}
	
	void drawSegment(Graphics g, Location origin, double scale){
		Point lastPnt = locations.get(0).asPoint(origin, scale);
		for(Location loc : locations){
			Point pnt = loc.asPoint(origin, scale);
			if(!pnt.equals(lastPnt)){
				g.drawLine(lastPnt.x, lastPnt.y, pnt.x, pnt.y);
				lastPnt = pnt;
			}
		}
	}
	
	
}
