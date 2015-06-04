
public class KeyNodePair <K extends Comparable<? super K>, V> {

	public K key;
	public Node<K, V> node;

	public KeyNodePair(K key, Node<K, V> node){
		this.key = key;
		this.node = node;
	}
}
