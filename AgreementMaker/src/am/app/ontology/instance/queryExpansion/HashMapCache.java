package am.app.ontology.instance.queryExpansion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 *
 * 
 * @author Federico Caimi
 *
 */
public class HashMapCache<K, V> implements Cache<K, V>{
	private HashMap<K, V> cache;
	private Logger log = Logger.getLogger(HashMapCache.class); 
	private String cacheFileName;
	
	public HashMapCache(){
		
	}
	
	public HashMapCache(String cacheFileName){
		this.cacheFileName = cacheFileName;
		load();
	}
	
	public void load(){
		FileInputStream fis = null;
		ObjectInputStream in;
		Object input = null;
						
		try {	fis = new FileInputStream(cacheFileName); }
		catch (FileNotFoundException e1) {
			log.error("The cache file doesn't exist, the cache will be empty");
			cache = new HashMap<K, V>();
			return;
		}
		try {
			in = new ObjectInputStream(fis);
			input  = in.readObject();
			cache = (HashMap<K, V>) input;
		} catch (Exception e1) {
			log.error("The cache will be empty");
			cache = new HashMap<K, V>();
			return;
		}
		log.info("Done");
	}

	@Override
	public void persist(){
		log.info("Writing cache to file... [" + cacheFileName + "]");
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(cacheFileName));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			out.writeObject(cache);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 log.info("Done");	
	}

	@Override
	public void put(K key, V value) {
		cache.put(key, value);		
	}

	@Override
	public V get(K key) {
		return cache.get(key);
	}
}
