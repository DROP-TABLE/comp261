

import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

import javax.swing.JTextArea;

public class Intersection {
	
	private final Location loc;
	private final int ID;  //may not choose to use
	private Set<Segment> connections;
	
	public Intersection(Location loc, int ID){
		this.loc = loc;
		
		this.ID = ID;
		connections = new HashSet<>();
	}
	
	void drawIntersection(Graphics g, Location origin, double scale){
		Point pnt = loc.asPoint(origin, scale);
		g.fillRect(pnt.x-1, pnt.y-1, 2, 2);
		//System.out.println("point drawn at: " + pnt.x + ", " + pnt.y);
	}
	
	void addSegment(Segment seg){
		connections.add(seg);
	}
	
	boolean isOn(int x, int y, Location origin, double scale){
		Point pnt = loc.asPoint(origin, scale);
		if(pnt.getX() >= x-4 && pnt.getX() <= x+4 && pnt.getY() >= y-4 && pnt.getY() <= y+4) return true;
		return false;
	}
	
	void printDetails(Location origin, double scale, Map<Integer, Road> roadMap, JTextArea jta){
		String outText = "Intersection (ID: " + ID + ") selected.\nCoordinates: x = " + loc.asPoint(origin, scale).getX() + ", y = " + loc.asPoint(origin, scale).getY() + "\nConnected Roads: ";
		//System.out.println("Intersection (ID: " + ID + ") selected.\nCoordinates: x = " + loc.asPoint(origin, scale).getX() + ", y = " + loc.asPoint(origin, scale).getY() + "\nConnected Roads: ");
		Set<String> displayed = new HashSet<>();
		for(Segment seg : connections){
			Road curRoad = roadMap.get(seg.getRoadID());
			if(displayed.contains(curRoad.getName() + ", " + curRoad.getRegion())) continue;
			displayed.add(curRoad.getName() + ", " + curRoad.getRegion());
			outText += curRoad.getName() + ", " + curRoad.getRegion();
			//System.out.println(curRoad.getName() + ", " + curRoad.getRegion());
		}
		jta.setText(null);
		jta.setText(outText);
	}
	
}
