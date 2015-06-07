import java.util.*;
import java.awt.Graphics;
import java.awt.Point;

public class Polygon {
	
	private final String type;
	private final String label;
	private final int endLevel;
	private final int cityID;
	private final List<Location> locations;
	
	public Polygon(String type, String label, int endLevel, int cityID){
		this.type = type;
		this.label = label;
		this.endLevel = endLevel;
		this.cityID = cityID;
		
		locations = new ArrayList<>();
	}
	
	void addLocation(Location loc){
		locations.add(loc);
	}
	
	void drawPolygon(Graphics g, Location origin, double scale){
		//System.out.println("poly drawn");
		int[] xPnts = new int[locations.size()];
		int[] yPnts = new int[locations.size()];
		for(int i=0;i<locations.size();i++){
			xPnts[i] = locations.get(i).asPoint(origin, scale).x;
			yPnts[i] = locations.get(i).asPoint(origin, scale).y;
		}
		g.fillPolygon(xPnts, yPnts, locations.size());
	}
	
	void drawRoads(Graphics g, Location origin, double scale){
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