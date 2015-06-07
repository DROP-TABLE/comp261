import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;;


public class Renderer extends GUI {
	
	private static final double ZOOM_FACTOR = 1.5;
	private static final float LIGHT_SOURCE_INTENSITY = 1.0f;
	private Set<Vector3D> lightSources;
	private Set<Vector3D> rotatedSources;
	private Set<Polygon> polygons;
	
	private Color[][] bitmap;
	private float[][] zbuffer;
	
	private float xtheta;
	private float yphi;
	private float zoom = 1.0f;
	private float xAdjust = 0.0f;
	private float yAdjust = 0.0f;
	
	private double mouseX = 0.0;
	private double mouseY = 0.0;
	private int mouseButton = 0;
	
	private boolean enableGouraud = true;
	
	public Renderer(){
		lightSources = new HashSet<>();
		polygons = new HashSet<>();
		xtheta = 0.0f;
		yphi = 0.0f;
	}

	@Override
	protected void onLoad(File file) {
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			line = br.readLine();
			String[] tokens = line.split(" ");
			Vector3D light = new Vector3D(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
			lightSources = new HashSet<>();
			lightSources.add(light);
			Set<Polygon> newPolygons = new HashSet<>();
			while((line = br.readLine()) != null){
				tokens = line.split(" ");
				if(tokens.length != 12) break;
				Vector3D v1 = new Vector3D(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
				Vector3D v2 = new Vector3D(Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]), Float.parseFloat(tokens[5]));
				Vector3D v3 = new Vector3D(Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]), Float.parseFloat(tokens[8]));
				newPolygons.add(new Polygon(v1, v2, v3, Integer.parseInt(tokens[9]), Integer.parseInt(tokens[10]), Integer.parseInt(tokens[11]), false));
			}
			polygons = newPolygons;
			br.close();
		} catch(IOException e){System.out.println("IO exception loading file " + e);}
	}
	
	@Override
	protected void onPress(MouseEvent e){
		mouseX = e.getX();
		mouseY = e.getY();
		mouseButton = e.getButton();
	}
	
	@Override
	protected void onDrag(MouseEvent e){
		if(mouseButton == MouseEvent.BUTTON1){
			if(xtheta-Math.PI/2 > 0){
				if((Math.abs((int)((xtheta-Math.PI/2)/Math.PI))%2) == 1){
					yphi += (Math.PI/900)*(mouseX - e.getX());
				}
				else yphi -= (Math.PI/900)*(mouseX - e.getX());
			}
			else{
				if((Math.abs((int)((xtheta-Math.PI/2)/Math.PI))%2) == 1){
					yphi -= (Math.PI/900)*(mouseX - e.getX());
				}
				else yphi += (Math.PI/900)*(mouseX - e.getX());
			}
			xtheta -= (Math.PI/900)*(mouseY - e.getY());
		}
		else if(mouseButton == MouseEvent.BUTTON3){
			xAdjust += mouseX - e.getX();
			yAdjust += mouseY - e.getY();
		}
		mouseX = e.getX();
		mouseY = e.getY();
		//System.out.println("xtheta: " + xtheta + " , yphi: " + yphi);
	}
	
	@Override
	protected void onMouseScroll(MouseWheelEvent e){
		int scrollAmount = e.getWheelRotation();
		if(scrollAmount > 0){
			for(int i=0;i<scrollAmount;i++){
				zoom *= ZOOM_FACTOR;
			}
		}
		else if(scrollAmount < 0){
			for(int i=0;i>scrollAmount;i--){
				zoom /= ZOOM_FACTOR;
			}
		}
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		switch(ev.getKeyCode()){
			case 87: // key = w
				xtheta += Math.PI/12;
			break;
			
			case 65: // key = a
				//yphi += Math.PI/12;
				if(xtheta-Math.PI/2 > 0){
					if((Math.abs((int)((xtheta-Math.PI/2)/Math.PI))%2) == 1){
						yphi -= Math.PI/12;
					}
					else yphi += Math.PI/12;
				}
				else{
					if((Math.abs((int)((xtheta-Math.PI/2)/Math.PI))%2) == 1){
						yphi += Math.PI/12;
					}
					else yphi -= Math.PI/12;
				}
			break;
			
			case 83: // key = s
				xtheta -= Math.PI/12;
			break;
			
			case 68: // key = d
				//yphi -= Math.PI/12;
				if(xtheta-Math.PI/2 > 0){
					if((Math.abs((int)((xtheta-Math.PI/2)/Math.PI))%2) == 1){
						yphi += Math.PI/12;
					}
					else yphi -= Math.PI/12;
				}
				else{
					if((Math.abs((int)((xtheta-Math.PI/2)/Math.PI))%2) == 1){
						yphi -= Math.PI/12;
					}
					else yphi += Math.PI/12;
				}
			break;
			
			case 81: // key = q
				zoom *= ZOOM_FACTOR;
			break;
			
			case 69: // key = e
				zoom /= ZOOM_FACTOR;
			break;
			
			case 38: // key = up arrow
				yAdjust -= 50/zoom;
			break;
			
			case 37: // key = left arrow
				xAdjust -= 50/zoom;
			break;
			
			case 40: // key = down arrow
				yAdjust += 50/zoom;
			break;
			
			case 39: // key = right arrow
				xAdjust += 50/zoom;
			break;
		}
			
	}
	
	@Override
	protected void reset(){
		zoom = 1.0f;
		xtheta = 0.0f;
		yphi = 0.0f;
		redraw();
	}
	
	@Override
	protected void addLightSource(){
		Vector3D zVector = new Vector3D(0.0f, 0.0f, 1.0f);
		Transform xRot = Transform.newXRotation(-xtheta); // perform the rotations in reverse, as must add light source so that it will be rotated 
		Transform yRot = Transform.newYRotation(-yphi); // to the current camera angle
		Transform combRot = yRot.compose(xRot);
		
		Vector3D sourceLoc = combRot.multiply(zVector);
		lightSources.add(sourceLoc);
		System.out.println("Light Source added at: " + sourceLoc.toString());
		redraw();
	}
	
	@Override
	protected void toggleGouraud(){
		if(enableGouraud) enableGouraud = false;
		else enableGouraud = true;
		redraw();
	}

	@Override
	protected BufferedImage render() {
		Set<Polygon> modPolys = rotatePolygons(polygons);
		modPolys = translateMinToZero(modPolys);
		modPolys = rescaleToFit(modPolys);
		modPolys = applyPosition(modPolys);
		modPolys = applyZoom(modPolys);
		modPolys = checkVisibility(modPolys);
		if(enableGouraud){
			computeGourandShading(modPolys);
			constructGouraudBitmap(modPolys);
		}
		else{
			modPolys = computeShading(modPolys);
			constructBitmap(modPolys);
		}
		
		return convertBitmapToImage();
	}
	
	private Set<Polygon> rotatePolygons(Set<Polygon> modPolys){
		Set<Polygon> newPolygons = new HashSet<>();
		Transform xRot = Transform.newXRotation(xtheta);
		Transform yRot = Transform.newYRotation(yphi);
		Transform combRot = xRot.compose(yRot);
		for(Polygon poly : modPolys){
			Vector3D modv1 = combRot.multiply(poly.vertices[0]);
			Vector3D modv2 = combRot.multiply(poly.vertices[1]);
			Vector3D modv3 = combRot.multiply(poly.vertices[2]);
			
			newPolygons.add(new Polygon(modv1, modv2, modv3, poly.colourRef[0], poly.colourRef[1], poly.colourRef[2], poly.visible));
		}
		rotatedSources = new HashSet<>();
		for(Vector3D vec : lightSources){
			rotatedSources.add(combRot.multiply(vec));
		}
		return newPolygons;
	}
	
	private Set<Polygon> translateMinToZero(Set<Polygon> modPolys){
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		for(Polygon poly : modPolys){
			for(int i=0;i<3;i++){
				if(poly.vertices[i].x < minX){
					minX = poly.vertices[i].x;
				}
				if(poly.vertices[i].y < minY){
					minY = poly.vertices[i].y;
				}
			}
		}
		Transform translate = Transform.newTranslation(-minX, -minY, 0.0f);
		Set<Polygon> newPolygons = new HashSet<>();
		for(Polygon poly : modPolys){
			Vector3D modv1 = translate.multiply(poly.vertices[0]);
			Vector3D modv2 = translate.multiply(poly.vertices[1]);
			Vector3D modv3 = translate.multiply(poly.vertices[2]);
			newPolygons.add(new Polygon(modv1, modv2, modv3, poly.colourRef[0], poly.colourRef[1], poly.colourRef[2], poly.visible));
		}
		return newPolygons;
	}
	
	private Set<Polygon> rescaleToFit(Set<Polygon> modPolys){
		
		float maxDiff = Float.NEGATIVE_INFINITY;
		for(Polygon poly : modPolys){
			for(int i=0;i<3;i++){
				if(poly.vertices[i].x - CANVAS_WIDTH > maxDiff){
					maxDiff = poly.vertices[i].x - CANVAS_WIDTH;
				}
				if(poly.vertices[i].y - CANVAS_HEIGHT > maxDiff){
					maxDiff = poly.vertices[i].y - CANVAS_HEIGHT;
				}
			}
		}
		float factor = ((float)CANVAS_HEIGHT)/((float)CANVAS_HEIGHT + maxDiff);
		Transform scaler = Transform.newScale(factor, factor, factor);
		Set<Polygon> newPolygons = new HashSet<>();
		for(Polygon poly : modPolys){
			Vector3D modv1 = scaler.multiply(poly.vertices[0]);
			Vector3D modv2 = scaler.multiply(poly.vertices[1]);
			Vector3D modv3 = scaler.multiply(poly.vertices[2]);
			newPolygons.add(new Polygon(modv1, modv2, modv3, poly.colourRef[0], poly.colourRef[1], poly.colourRef[2], poly.visible));
		}
		return newPolygons;
	}
	
	private Set<Polygon> applyPosition(Set<Polygon> modPolys){
		Transform translate = Transform.newTranslation(-xAdjust, -yAdjust, 0.0f);
		Set<Polygon> newPolygons = new HashSet<>();
		for(Polygon poly : modPolys){
			Vector3D modv1 = translate.multiply(poly.vertices[0]);
			Vector3D modv2 = translate.multiply(poly.vertices[1]);
			Vector3D modv3 = translate.multiply(poly.vertices[2]);
			newPolygons.add(new Polygon(modv1, modv2, modv3, poly.colourRef[0], poly.colourRef[1], poly.colourRef[2], poly.visible));
		}
		return newPolygons;
	}
	
	private Set<Polygon> applyZoom(Set<Polygon> modPolys){
		Transform scaler = Transform.newScale(zoom, zoom, zoom);
		float transAmount = (CANVAS_HEIGHT-(CANVAS_HEIGHT/zoom))/2;
		Transform translate = Transform.newTranslation(-transAmount, -transAmount, 0.0f);
		Transform combined = scaler.compose(translate);
		Set<Polygon> newPolygons = new HashSet<>();
		for(Polygon poly : modPolys){
			Vector3D modv1 = combined.multiply(poly.vertices[0]);
			Vector3D modv2 = combined.multiply(poly.vertices[1]);
			Vector3D modv3 = combined.multiply(poly.vertices[2]);
			newPolygons.add(new Polygon(modv1, modv2, modv3, poly.colourRef[0], poly.colourRef[1], poly.colourRef[2], poly.visible));
		}
		return newPolygons;
		
	}
	
	private Set<Polygon> checkVisibility(Set<Polygon> modPolys){
		Set<Polygon> newPolygons = new HashSet<>();
		for(Polygon poly : modPolys){
			boolean vis = true;
			if(poly.getNormal().z > 0){
				vis = false;
			}
			newPolygons.add(new Polygon(poly.vertices[0], poly.vertices[1], poly.vertices[2], poly.colourRef[0], poly.colourRef[1], poly.colourRef[2], vis));
		}
		return newPolygons;
	}
	
	
	private Set<Polygon> computeShading(Set<Polygon> modPolys){
		int[] ambLight = getAmbientLight();
		Set<Polygon> newPolygons = new HashSet<>();
		for(Polygon poly : modPolys){
			int[] adjColour = new int[3];
			for(int i=0;i<3;i++){
				float sourceLighting = 0.0f;
				for(Vector3D source : rotatedSources){
					float cos = -poly.getNormal().cosTheta(source);
					if(cos < 0) cos = 0.0f;
					sourceLighting += LIGHT_SOURCE_INTENSITY*(cos);
				}
				adjColour[i] = (int) (((float)ambLight[i]/255 + sourceLighting)*poly.colourRef[i]);
				if(adjColour[i] > 255) adjColour[i] = 255;
				if(adjColour[i] < 0) adjColour[i] = 0;
			}
			newPolygons.add(new Polygon(poly.vertices[0], poly.vertices[1], poly.vertices[2], adjColour[0], adjColour[1], adjColour[2], poly.visible));
		}
		return newPolygons;
	}
	
	private Map<Vector3D, float[]> computeGourandShading(Set<Polygon> modPolys){ // needs to be called before edgelist generation so the result can be used to determine the extended edgelist parts.
		int[] ambLight = getAmbientLight();
		Map<Vector3D, float[]> vertexLight = new HashMap<>();
		
		for(Polygon poly : modPolys){
			for(int i=0;i<3;i++){
				Set<Polygon> adj = new HashSet<>();
				if(!vertexLight.keySet().contains(poly.vertices[i])){
					for(Polygon pol2 : modPolys){
						for(int j=0;j<3;j++){
							if(!pol2.equals(poly) && poly.vertices[i].equals(pol2.vertices[j])){
								adj.add(pol2);
							}
						}
					}
					Vector3D avgNorm = poly.getNormal();
					for(Polygon pol3 : adj){
						avgNorm = avgNorm.plus(pol3.getNormal()); // all vectors returned through getNormal() are unit vectors already.
					}
					avgNorm = avgNorm.unitVector();
					Vector3D vec = poly.vertices[i];
					
					float[] adjColour = new float[3];
					for(int j=0;j<3;j++){ // scroll through colours
						float sourceLighting = 0.0f;
						for(Vector3D source : rotatedSources){
							float cos = -avgNorm.cosTheta(source);
							if(cos < 0) cos = 0.0f;
							sourceLighting += LIGHT_SOURCE_INTENSITY*(cos);
						}
						float avgReflect = poly.colourRef[j];
						for(Polygon pol4 : adj){
							avgReflect += pol4.colourRef[j];
						}
						avgReflect /= (adj.size() + 1); // total number of polygons at point is adj set size + the starting polygon
						adjColour[j] = (((float)ambLight[j]/255 + sourceLighting)*avgReflect);
					}
					
					vertexLight.put(vec, adjColour);
				}
			}
		}
		for(Polygon poly : modPolys){
			poly.genGouraudEdgeList(vertexLight);
		}
		return vertexLight;
	}
	
	private void constructBitmap(Set<Polygon> modPolys){
		bitmap = new Color[600][600];
		zbuffer = new float[600][600];
		for(int i=0;i<600;i++){
			for(int j=0;j<600;j++){
				bitmap[i][j] = Color.lightGray;
				zbuffer[i][j] = Float.MAX_VALUE;
			}
		}
		for(Polygon poly : modPolys){
			if(poly.visible){
				for(int i=0;i<600;i++){
					for(int j=0;j<600;j++){
						if(poly.isOn(i, j)){
							float[] edgeRow = poly.getEdgeRow(j); 
							float grad = (edgeRow[1] - edgeRow[3])/(edgeRow[0] - edgeRow[2]);
							float zval = edgeRow[1] + grad * (i - edgeRow[0]);
							if(zval < zbuffer[i][j]){
								zbuffer[i][j] = zval;
								bitmap[i][j] = new Color(poly.colourRef[0], poly.colourRef[1], poly.colourRef[2]);
							}
						}
					}
				}
			}
		}
	}
	
	private void constructGouraudBitmap(Set<Polygon> modPolys){
		bitmap = new Color[600][600];
		zbuffer = new float[600][600];
		for(int i=0;i<600;i++){
			for(int j=0;j<600;j++){
				bitmap[i][j] = Color.lightGray;
				zbuffer[i][j] = Float.MAX_VALUE;
			}
		}
		
		for(Polygon poly : modPolys){
			if(poly.visible){
				for(int i=0;i<600;i++){
					for(int j=0;j<600;j++){
						if(poly.isOn(i, j)){
							float[] edgeRow = poly.getEdgeRow(j); 
							float zgrad = (edgeRow[1] - edgeRow[6])/(edgeRow[0] - edgeRow[5]);
							float zval = edgeRow[1] + zgrad * (i - edgeRow[0]);
							
							float redgrad = (edgeRow[2] - edgeRow[7])/(edgeRow[0] - edgeRow[5]);
							int redval = (int) (edgeRow[2] + redgrad * (i - edgeRow[0]));
							if(redval > 255) redval = 255;
							if(redval < 0) redval = 0;
							
							float grngrad = (edgeRow[3] - edgeRow[8])/(edgeRow[0] - edgeRow[5]);
							int grnval = (int) (edgeRow[3] + grngrad * (i - edgeRow[0]));
							if(grnval > 255) grnval = 255;
							if(grnval < 0) grnval = 0;
							
							float bluegrad = (edgeRow[4] - edgeRow[9])/(edgeRow[0] - edgeRow[5]);
							int blueval = (int)(edgeRow[4] + bluegrad * (i - edgeRow[0]));
							if(blueval > 255) blueval = 255;
							if(blueval < 0) blueval = 0;
							
							if(zval < zbuffer[i][j]){
								zbuffer[i][j] = zval;
								bitmap[i][j] = new Color(redval, grnval, blueval);
							}
						}
					}
				}
			}
		}
	}
	
	private BufferedImage convertBitmapToImage(){
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}
	
	public static void main(String[] args) {
		new Renderer();
	}
	
	
}
