

import java.util.*;
import java.awt.Graphics;

public class Road{
	
	private final int ID;
	private final String name;
	private final String region;
	
	private final boolean oneway;
	private final int speed;
	private final int roadClass;
	
	private final boolean notForCar;
	private final boolean notForPede;
	private final boolean notForBicy;
	
	private Set<Segment> roadSegments;
	
	public Road(String[] data){
		ID = Integer.parseInt(data[0]);
		name = data[2];
		region = data[3];
		oneway = (data[4].equals("1")) ? true : false;
		speed = Integer.parseInt(data[5]);
		roadClass = Integer.parseInt(data[6]);
		notForCar = (data[7].equals("1")) ? true : false;
		notForPede = (data[8].equals("1")) ? true : false;
		notForBicy = (data[9].equals("1")) ? true : false;
		
		roadSegments = new HashSet<>();
	}
	
	void addSegment(Segment seg){
		roadSegments.add(seg);
	}
	
	void drawRoad(Graphics g, Location origin, double scale){
		for(Segment s : roadSegments){
			s.drawSegment(g, origin, scale);
		}
	}
	
	String getName(){
		return name;
	}
	
	String getRegion(){
		return region;
	}
	
	
}
