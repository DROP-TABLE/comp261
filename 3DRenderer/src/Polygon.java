import java.util.Map;



public class Polygon {
	
	public final Vector3D[] vertices;
	public final int[] colourRef;
	//private boolean visible = true;
	public final boolean visible;
	
	private int minY = 0;
	private float[][] edgeList;
	
	public Polygon(Vector3D v1, Vector3D v2, Vector3D v3, int red, int grn, int blue, boolean vis){
		Vector3D[] vert = {v1, v2, v3};
		vertices = vert;
		int[] colour = {red, grn, blue};
		colourRef = colour;
		visible = vis;
	}
	
	public Vector3D getNormal(){
		Vector3D e21 = vertices[1].minus(vertices[0]);
		Vector3D e32 = vertices[2].minus(vertices[1]);
		
		Vector3D norm = e21.crossProduct(e32);
		return norm.unitVector();
	}
	
	/*public void setVisibility(boolean vis){
		visible = vis;
	}
	
	public boolean getVisible(){
		return visible;
	}*/
	
	private void computeEdgeLists(){
		int maxY = Math.round(Math.max(Math.max(vertices[0].y, vertices[1].y), vertices[2].y));
		minY = Math.round(Math.min(Math.min(vertices[0].y, vertices[1].y), vertices[2].y));
		edgeList = new float[maxY-minY][4];
		for(int i=0;i<edgeList.length;i++){ //Initialize with +inf, +inf, -inf, +inf.
			edgeList[i][0] = Float.POSITIVE_INFINITY;
			edgeList[i][1] = Float.POSITIVE_INFINITY;
			edgeList[i][2] = Float.NEGATIVE_INFINITY;
			edgeList[i][3] = Float.POSITIVE_INFINITY;
		}
		
		for(int j=0;j<3;j++){
			Vector3D a = vertices[j];
			Vector3D b = vertices[(j+1)%3];
			if(a.y > b.y){
				a = b;
				b = vertices[j];
			}
			float mx = (b.x - a.x)/(b.y - a.y);
			float mz = (b.z - a.z)/(b.y - a.y);
			float x = a.x;
			float z = a.z;
			int maxi = Math.round(b.y) - Math.round(a.y);
			int start= Math.round(a.y) - minY;
			for(int i=start;i<(start+maxi);i++){
				if(x < edgeList[i][0]){
					edgeList[i][0] = x;
					edgeList[i][1] = z;
				}
				if(x > edgeList[i][2]){
					edgeList[i][2] = x;
					edgeList[i][3] = z;
				}
				
				x = x + mx;
				z = z + mz;
			}
		}
	}
	
	public float[][] getEdgeList(){
		return edgeList;
	}
	
	public void genGouraudEdgeList(Map<Vector3D, float[]> vertexLight){
		int maxY = Math.round(Math.max(Math.max(vertices[0].y, vertices[1].y), vertices[2].y));
		minY = Math.round(Math.min(Math.min(vertices[0].y, vertices[1].y), vertices[2].y));
		edgeList = new float[maxY-minY][10];
		for(int i=0;i<edgeList.length;i++){ //Initialize with +inf, +inf, -inf, +inf.
			edgeList[i][0] = Float.POSITIVE_INFINITY;
			edgeList[i][1] = Float.POSITIVE_INFINITY;
			edgeList[i][5] = Float.NEGATIVE_INFINITY;
			edgeList[i][6] = Float.POSITIVE_INFINITY;
		}
		for(int j=0;j<3;j++){
			Vector3D a = vertices[j];
			Vector3D b = vertices[(j+1)%3];
			if(a.y > b.y){
				a = b;
				b = vertices[j];
			}
			float mx = (b.x - a.x)/(b.y - a.y);
			float mz = (b.z - a.z)/(b.y - a.y);
			float mred = (vertexLight.get(b)[0] - vertexLight.get(a)[0])/(b.y - a.y);
			float mgrn = (vertexLight.get(b)[1] - vertexLight.get(a)[1])/(b.y - a.y);
			float mblue = (vertexLight.get(b)[2] - vertexLight.get(a)[2])/(b.y - a.y);
			
			float x = a.x;
			float z = a.z;
			float red = vertexLight.get(a)[0];
			float grn = vertexLight.get(a)[1];
			float blue = vertexLight.get(a)[2];
			
			int maxi = Math.round(b.y) - Math.round(a.y);
			int start= Math.round(a.y) - minY;
			for(int i=start;i<(start+maxi);i++){
				if(x < edgeList[i][0]){
					edgeList[i][0] = x;
					edgeList[i][1] = z;
					edgeList[i][2] = red;
					edgeList[i][3] = grn;
					edgeList[i][4] = blue;
					
				}
				if(x > edgeList[i][5]){
					edgeList[i][5] = x;
					edgeList[i][6] = z;
					edgeList[i][7] = red;
					edgeList[i][8] = grn;
					edgeList[i][9] = blue;
				}
				
				x = x + mx;
				z = z + mz;
				red = red + mred;
				grn = grn + mgrn;
				blue = blue + mblue;
			}
		}
	}
	
	public boolean isOn(int x, int y){
		if(edgeList == null) computeEdgeLists();
		if(y < minY || y >= minY + edgeList.length) return false;
		if(edgeList[0].length == 4){
			if(x >= edgeList[y-minY][0] && x <= edgeList[y-minY][2]){
				return true;
			}
		}
		else{
			if(x >= edgeList[y-minY][0] && x <= edgeList[y-minY][5]){
				return true;
			}
		}
		return false;
	}
	
	public float[] getEdgeRow(int y){ //is only called after isOn, so edgelist must exist.
		return edgeList[y-minY];
	}
	
	@Override
	public String toString(){
		StringBuilder ans = new StringBuilder("Poly:");
		ans.append('(').append(vertices[0].toString()).append(',').append(vertices[1].toString()).append(',').append(vertices[2].toString()).append(')');
		return ans.toString();
	}
	
}
