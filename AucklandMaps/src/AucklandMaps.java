

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.Dimension;
import java.util.*;
import java.io.*;
import javax.swing.JTextField;

public class AucklandMaps extends GUI {
	
	private Map<Integer, Road> roadIDMap;
	private Map<Integer, Intersection> intersectionIDMap;
	//private Set<Segment> segmentSet;
	private Set<Polygon> polygonSet;
	private RoadTrieBox roadTrieStructure;
	private List<Road> roadsSelected;
	
	/**
	 * 
	 */
	private double scale;
	private Location origin;
	private Intersection selectedNode = null;
	
	private int mouseX;
	private int mouseY;
	
	
	public AucklandMaps(){
		roadIDMap = new HashMap<>();
		intersectionIDMap = new HashMap<>();
		//segmentSet = new HashSet<>();
		polygonSet = new HashSet<>();
		roadTrieStructure = new RoadTrieBox(getTextOutputArea());
		roadsSelected = new ArrayList<>();
		scale = 50;
		origin = Location.newFromLatLon(-36.847622, 174.763444);
		origin = origin.moveBy(-8, 1);
		
	}

	@Override
	protected void redraw(Graphics g) {
		draw(g);
	}

	@Override
	protected void onClick(MouseEvent e) {
		int clickX = e.getX();
		int clickY = e.getY();
		Set<Integer> interID = intersectionIDMap.keySet();
		for(Integer i : interID){
			Intersection element = intersectionIDMap.get(i);
			if(element.isOn(clickX, clickY, origin, scale)){
				element.printDetails(origin, scale, roadIDMap, getTextOutputArea());
				selectedNode = element;
				break;
			}
		}
	}
	
	@Override
	protected void onPress(MouseEvent e){
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	protected void onDrag(MouseEvent e){
		origin = origin.moveBy((mouseX-e.getX())/scale, (e.getY()-mouseY)/scale);
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	protected void onMouseScroll(MouseWheelEvent e){
		int scrollAmount = e.getWheelRotation();
		if(scrollAmount > 0){
			for(int i=0;i<scrollAmount;i++){
				zoomOut();
			}
		}
		else if(scrollAmount < 0){
			for(int i=0;i>scrollAmount;i--){
				zoomIn();
			}
		}
	}

	@Override
	protected void onSearch() {
		JTextField searchBox = getSearchBox();
		String text = searchBox.getText();
		
		roadsSelected = roadTrieStructure.getRoads(text);
	}
	

	@Override
	protected void onMove(Move m) {
		switch(m){
		case EAST:
			origin = origin.moveBy(100/scale, 0);
		break;
		
		case WEST:
			origin = origin.moveBy(-100/scale, 0);
		break;
		
		case NORTH:
			origin = origin.moveBy(0, 100/scale);
		break;
		
		case SOUTH:
			origin = origin.moveBy(0, -100/scale);
		break;
		
		case ZOOM_IN:
			zoomIn();
		break;
		
		case ZOOM_OUT:
			zoomOut();
		break;
		
		default:
		break;
		}
	}
	
	private void zoomIn(){
		Dimension canvasSize = getDrawingAreaDimension();
		scale = scale*2;
		origin = origin.moveBy(canvasSize.getWidth()/(2*scale), -canvasSize.getHeight()/(2*scale));
	}
	
	private void zoomOut(){
		Dimension canvasSize = getDrawingAreaDimension();
		origin = origin.moveBy(-canvasSize.getWidth()/(2*scale), canvasSize.getHeight()/(2*scale));
		scale = scale/2;
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		loadRoads(roads);
		loadIntersections(nodes);
		loadSegments(segments);
		loadPolygons(polygons);
	}
	
	private void draw(Graphics g){
		g.setColor(Color.green);
		for(Polygon poly : polygonSet){
			poly.drawPolygon(g, origin, scale);
		}
		
		g.setColor(Color.blue);
		for(Integer i : roadIDMap.keySet()){
			roadIDMap.get(i).drawRoad(g, origin, scale);
		}
		
		g.setColor(Color.red);
		for(Road r : roadsSelected){
			r.drawRoad(g, origin, scale);
		}
		
		g.setColor(Color.BLACK);
		for(Intersection inter : intersectionIDMap.values()){
			inter.drawIntersection(g, origin, scale);
		}
		
		g.setColor(Color.red);
		if(selectedNode != null){
			selectedNode.drawIntersection(g, origin, scale);
		}
	}
	
	
	private void loadRoads(File roads){
		try{
			BufferedReader br = new BufferedReader(new FileReader(roads));
			br.readLine();
			String line = br.readLine();
			while(line != null){
				String[] columns = new String[10]; 
				columns = line.split("\t");
				Road curRoad = new Road(columns);
				int roadID = Integer.parseInt(columns[0]);
				roadIDMap.put(roadID, curRoad);
				roadTrieStructure.addToTrie(curRoad);
				line = br.readLine();
			}
			br.close();
		}catch(IOException e){System.out.println("Error loading Roads file. " + e);}
	}

	private void loadIntersections(File nodes){
		try{
			BufferedReader br = new BufferedReader(new FileReader(nodes));
			
			String line = br.readLine();
			while(line != null){
				String[] columns = new String[3]; 
				columns = line.split("\t");
				Location curLoc = Location.newFromLatLon(Double.parseDouble(columns[1]), Double.parseDouble(columns[2]));
				Integer nID = Integer.parseInt(columns[0]);
				Intersection curInter = new Intersection(curLoc, nID);
				intersectionIDMap.put(nID, curInter);
				line = br.readLine();
			}
			br.close();
		}catch(IOException e){System.out.println("Error loading Nodes file. " + e);}
	}

	private void loadSegments(File segments){
		try{
			BufferedReader br = new BufferedReader(new FileReader(segments));
			br.readLine();
			String line = br.readLine();
			while(line != null){
				Queue<String> lineQueue = new ArrayDeque<>(Arrays.asList(line.split("\t")));
				Integer parentRoadID = Integer.parseInt(lineQueue.poll());
				double length = Double.parseDouble(lineQueue.poll());
				Integer node1ID = Integer.parseInt(lineQueue.poll());
				Integer node2ID = Integer.parseInt(lineQueue.poll());
				Segment curSeg = new Segment(parentRoadID, length, node1ID, node2ID);
				while(!lineQueue.isEmpty()){
					Location curLoc = Location.newFromLatLon(Double.parseDouble(lineQueue.poll()), Double.parseDouble(lineQueue.poll()));
					curSeg.addLocation(curLoc);
				}
			
			if(roadIDMap.containsKey(parentRoadID)){
				roadIDMap.get(parentRoadID).addSegment(curSeg);
			}
			
			if( intersectionIDMap.containsKey(node1ID) && intersectionIDMap.containsKey(node2ID)){
				intersectionIDMap.get(node1ID).addSegment(curSeg);
				intersectionIDMap.get(node2ID).addSegment(curSeg);
			}
			line = br.readLine();
			}
			br.close();
		}catch(IOException e){ System.out.println("Error loading Segments file. " + e); }
	}
	
	private void loadPolygons(File polygons){
		if(polygons == null) return;
		try{
			BufferedReader br = new BufferedReader(new FileReader(polygons));
			String line = br.readLine();
			
			while(line != null){
				line = br.readLine();
				int index = line.indexOf("=");
				String type = "";
				if(line.substring(0, index).equals("Type")){
					type = line.substring(index+1);
					line = br.readLine();
					index = line.indexOf("=");
				}
				String label = "";
				if(line.substring(0, index).equals("Label")){
					label = line.substring(index+1);
					line = br.readLine();
					index = line.indexOf("=");
				}
				int endLevel = 0;
				if(line.substring(0, index).equals("EndLevel")){
					endLevel = Integer.parseInt(line.substring(index+1));
					line = br.readLine();
					index = line.indexOf("=");
				}
				int cityID = 0;
				if(line.substring(0, index).equals("CityIdx")){
					cityID = Integer.parseInt(line.substring(index+1));
					line = br.readLine();
					index = line.indexOf("=");
				}
				Polygon curPoly = new Polygon(type, label, endLevel, cityID);
				polygonSet.add(curPoly);
				
				line = line.substring(index+1);
				line = line.substring(1, line.length()-1);
				String[] vertices = line.split("\\),\\(");
				for(int i=0;i<vertices.length;i++){
					String[] coordPair = vertices[i].split(",");
					Location loc = Location.newFromLatLon(Double.parseDouble(coordPair[0]), Double.parseDouble(coordPair[1]));
					curPoly.addLocation(loc);
				}
				while(!"[END]".equals(line)){
					line = br.readLine();
					if(line == null) break;
				}
				while(!"[POLYGON]".equals(line)){
					line = br.readLine();
					if(line == null) break;
				}
			}
			
			br.close();
		}catch(IOException e){ System.out.println("Error loading Polygons file. " + e); }
		
	}
		
	
	public static void main(String[] args){
        new AucklandMaps();
    }
}

