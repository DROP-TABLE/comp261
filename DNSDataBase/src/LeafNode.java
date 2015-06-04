import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class LeafNode<K extends Comparable<? super K>, V> implements Node<K, V> {

	private int maxSize;
	private List<Pair<K,V>> values;

	private LeafNode<K, V> nextLeaf;

	public LeafNode(int size){
		maxSize = size;
		values = new ArrayList<>();
		nextLeaf = null;
	}

	private LeafNode(int maxSize, List<Pair<K, V>> values) {
		super();
		this.maxSize = maxSize;
		this.values = values;
		nextLeaf = null;
	}



	protected void setNext(LeafNode<K, V> ln){ nextLeaf = ln; }
	protected LeafNode<K, V> getNext(){ return nextLeaf; }

	public V find(K key){
		int index = Collections.binarySearch(values, key);
		if(index < 0) return null;
		else return values.get(index).value;
	}

	public KeyNodePair<K, V> put(K key, V val){
		int index = Collections.binarySearch(values, key);
		assert index < 0;
		index = -(index + 1);
		values.add(index, new Pair<>(key, val));
		if(values.size() < maxSize){
			return null;
		}
		else{
			return splitLeaf(key, val);
		}
	}

	public KeyNodePair<K, V> splitLeaf(K key, V val){
		//new element already added in prev method.
		int mid = (values.size()+1)/2;

		List<Pair<K, V>> siblingpairs = new ArrayList<>(values.subList(mid, values.size()));
		K firstSibKey = values.get(mid).key;
		values = new ArrayList<>(values.subList(0, mid));

		LeafNode<K, V> sibling = new LeafNode<>(maxSize, siblingpairs);

		sibling.setNext(nextLeaf);
		nextLeaf = sibling;

		return new KeyNodePair<K, V>(firstSibKey, sibling);
	}

	public void iterate(){
		for(int i=0;i<values.size();i++){
			System.out.println(DNSDB.IPToString((int)values.get(i).key) + ", " + values.get(i).value + i);
		}
	}

	private static class Pair<K extends Comparable<? super K>,V> implements Comparable<K> {

		private final K key;
		private final V value;

		private Pair(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		@Override
		public int compareTo(K o) {
			return key.compareTo(o);
		}

	}

}
