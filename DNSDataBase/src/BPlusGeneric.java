

public class BPlusGeneric <K extends Comparable<? super K>, V> {

	private Node<K, V> root;
	private LeafNode<K, V> startLeaf;

	private final int leafSize;
	private final int interSize;

	private final int HEADER_SIZE = 5;
	private final int POINTER_SIZE = 4;


	public BPlusGeneric(int blockSize, int keySize, int valSize){
		root = null;
		startLeaf = null;
		interSize = calcInterSize(blockSize, keySize);
		leafSize = calcLeafSize(blockSize, keySize, valSize);
	}

	 /**
     * Returns the value associated with the given key,
     * or null if the key is not in the B+ tree.
     */
    public V find(K key){
    	if(root == null) return null;
    	else return root.find(key);
    }

    /**
     * Stores the value associated with the key in the B+ tree.
     * If the key is already present, replaces the associated value.
     * If the key is not present, adds the key with the associated value
     * @param key
     * @param value
     * @return whether pair was successfully added.
     */
    public boolean put(K key, V val){
    	if(root == null){
    		root = new LeafNode<K, V>(leafSize);
    		root.put(key, val);
    		startLeaf = (LeafNode<K, V>)root;
    	}
    	else{
    		KeyNodePair<K, V> rightChild = root.put(key, val);

    		if(rightChild != null){
    			InternalNode<K, V> newRoot = new InternalNode<K, V>(interSize);
    			newRoot.moveChild(root);
    			newRoot.moveKey(rightChild.key);
    			newRoot.moveChild(rightChild.node);
    			root = newRoot;
    		}



    	}


    	return true;
    }

    public void iterateAll(){
    	LeafNode<K, V> leaf = startLeaf;
    	if(root != null){
    		int count = 0;
    		while(leaf != null){
    			leaf.iterate();
    			leaf = leaf.getNext();
    			count++;
    		}
    		System.out.println("number of leaves iterated: " + count);
    	}
    }

    public int calcInterSize(int blockSize, int keySize){
    	//return 4;
    	return (int)(((double)(blockSize - HEADER_SIZE - POINTER_SIZE))/((double)(keySize + POINTER_SIZE)));
    }

    public int calcLeafSize(int blockSize, int keySize, int valSize){
    	//return 4;
    	return (int)(((double)(blockSize - HEADER_SIZE - POINTER_SIZE))/((double)(keySize + valSize)));
    }

}
