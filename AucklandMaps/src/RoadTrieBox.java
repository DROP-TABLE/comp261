import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.JTextArea;

public class RoadTrieBox {
	
	private RoadTrie root;
	private JTextArea textOutput;
	
	public RoadTrieBox(JTextArea jta){
		root = new RoadTrie();
		textOutput = jta;
		
	}
	
	
	List<Road> getRoads(String text){
		
		RoadTrie trieEntry = root;
		for(int i=0;i<text.length();i++){
			if(trieEntry.hasLetter(text.substring(i, i+1))){
				trieEntry = trieEntry.getChild(text.substring(i, i+1));
			}
			else{
				textOutput.setText("No roads match search terms");
				//System.out.println("No roads match search terms");
				return new ArrayList<>();
			}
		}
		if(trieEntry.isEnd()){
			List<Road> exactRoad = new ArrayList<>();
			exactRoad.addAll(trieEntry.getValue()); 
			Road example = trieEntry.getValue().iterator().next();
			textOutput.setText("Showing road exactly matching search term, " + example.getName() + ", " + example.getRegion());
			//System.out.println("Showing road exactly matching search term, " + example.getName() + ", " + example.getRegion());
			return exactRoad;
		}
		else{
			return findCompletions(trieEntry);
		}
	}
	
	private List<Road> findCompletions(RoadTrie trieEntry){
		List<Road> results = new ArrayList<>();
		Stack<RoadTrie> parents = new Stack<>();
		parents.push(trieEntry);
		while(!parents.isEmpty()){
			trieEntry = parents.pop();
			
			if(trieEntry.isEnd()){   //&& !results.contains(trieEntry.getValue())
				results.addAll(trieEntry.getValue());
			}
			//process
			
			for(RoadTrie rt : trieEntry.getChildren()){
				parents.push(rt);
			}
			
		}
		List<String> names = new ArrayList<>();
		for(Road r : results){
			String roadName = r.getName() + ", " + r.getRegion();
			if(!names.contains(roadName)) names.add(roadName);
		}
		names.sort(null);
		String outText = "Roads beginning with search term: ";
		//System.out.println("Roads beginning with search term: ");
		for(int i=0;i<Math.min(10, names.size());i++) outText += names.get(i);//System.out.println(names.get(i));
		textOutput.setText(outText);
		return results;
	}
	
	void addToTrie(Road road){
		String roadName = road.getName() + ", " + road.getRegion();
		RoadTrie curPlace = root;
		for(int i=0;i<roadName.length();i++){
			boolean lastChar = false;
			if(i == roadName.length()-1){ lastChar = true;}
			String letter = null;
			if(lastChar){letter = roadName.substring(i);}
			else{letter = roadName.substring(i, i+1);}
			if(!curPlace.hasLetter(letter)){
				RoadTrie next = new RoadTrie();
				curPlace.addLetter(letter, next);
			}
			curPlace = curPlace.getChild(letter);
			if(lastChar) curPlace.addItem(road);
		}
	}
	
	
}
