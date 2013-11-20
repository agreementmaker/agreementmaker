package am.app.ontology.instance.queryExpansion;

public interface Cache<K, V> {
	/**
	 * Loads the cache from disk, or initializes the database
	 */
	public void load();
	
	/**
	 * Saves the cache to disk
	 */
	public void persist();
	
	/**
	 * 
	 */
	public void put(K key, V value);
	
	/**
	 * 
	 */
	public V get(K key);
}
