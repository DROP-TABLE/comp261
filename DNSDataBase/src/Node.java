import java.util.Comparator;


public interface Node <K extends Comparable<? super K>, V> {


	public V find(K key);
	public KeyNodePair<K, V> put(K key, V val);
}
