

import java.util.*;

public class RoadTrie {
	
	private Map<String, RoadTrie> children;
	private Set<Road> itemSet;
	
	public RoadTrie(){
		children = new HashMap<>();
		itemSet = new HashSet<>();
	}
	
	void addItem(Road item){
		itemSet.add(item);
	}
	
	boolean isEnd(){
		return itemSet.isEmpty() ? false : true;
	}
	
	Set<Road> getValue(){
		return itemSet;
	}
	
	void addLetter(String letter, RoadTrie next){
		children.put(letter, next);
	}
	
	void removeLetter(String letter){
		children.remove(letter);
	}
	
	Set<RoadTrie> getChildren(){
		Set<RoadTrie> values = new HashSet<>();
		for(String s : children.keySet()){
			values.add(children.get(s));
		}
		return values;
	}
	
	Set<String> getKeySet(){
		return children.keySet();
	}
	
	RoadTrie getChild(String letter){
		return children.get(letter);
	}
	
	boolean hasLetter(String letter){
		return children.containsKey(letter);
	}
}
