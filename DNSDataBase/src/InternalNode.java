import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class InternalNode <K extends Comparable<? super K>, V> implements Node<K, V> {

	private int maxSize;
	private List<K> keys;
	private List<Node<K, V>> children;

	public InternalNode(int size){
		maxSize = size;
		keys = new ArrayList<>();
		children = new ArrayList<>();;
	}

	public InternalNode(int maxSize, List<K> keys, List<Node<K, V>> children) {
		super();
		this.maxSize = maxSize;
		this.keys = keys;
		this.children = children;
	}

	public V find(K key){
		for(int i=0;i<keys.size();i++){
			if(keys.get(i).compareTo(key) > 0){
				return children.get(i).find(key);
			}
		}
		return children.get(children.size()-1).find(key);
	}

	public KeyNodePair<K, V> put(K key, V val){
		for(int i=0;i<keys.size();i++){
			if(keys.get(i).compareTo(key) > 0){
				KeyNodePair<K, V> child = children.get(i).put(key, val);
				if(child == null) return null;
				else return dealWithPromote(child);
			}
		}
		KeyNodePair<K, V> child = children.get(children.size()-1).put(key, val);
		if(child == null) return null;
		else return dealWithPromote(child);
	}

	public KeyNodePair<K, V> dealWithPromote(KeyNodePair<K, V> rightChild){
		if(rightChild == null) return null;

		K key = rightChild.key;

		int index = Collections.binarySearch(keys, key);
		assert index < 0;
		index = -(index + 1);
		keys.add(index, key);
		children.add(index + 1, rightChild.node);


		if(keys.size() <= maxSize) return null;

		int mid = keys.size()/2 + 1;


		List<K> siblingkeys = keys.subList(mid, keys.size());
		keys = new ArrayList<>(keys.subList(0, mid));

		List<Node<K, V>> siblingChild = new ArrayList<>(children.subList(mid, children.size()));
		children = new ArrayList<>(children.subList(0, mid));

		InternalNode<K, V> sibling = new InternalNode<>(maxSize, siblingkeys, siblingChild);

		K promoteKey = keys.get(mid-1);
		keys.remove(mid-1);

		return new KeyNodePair<K, V>(promoteKey, sibling);
	}

	protected void moveKey(K key){
		keys.add(key);
	}

	protected void moveChild(Node<K, V> child){
		children.add(child);
	}

	@Override
	public String toString() {
		return "InternalNode [maxSize=" + maxSize + ", keys=" + keys
				+ ", children=" + children + "]";
	}

}
