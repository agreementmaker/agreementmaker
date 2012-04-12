package am.app.ontology.instance.queryExpansion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class HashMapCache<K,V> implements Cache<K, V>{
	private HashMap<K, V> cache;
	private Logger log = Logger.getLogger(HashMapCache.class); 
	private String cacheFile;
	
	
	
	public void load(){
		FileInputStream fis = null;
		ObjectInputStream in;
		Object input = null;
						
		try {	fis = new FileInputStream(cacheFile); }
		catch (FileNotFoundException e1) {
			log.error("The cache file doesn't exist");
			//cache = new HashMap<String, String>();
			return;
		}
		
		try {
			in = new ObjectInputStream(fis);
			input  = in.readObject();
			cache = (HashMap<K, V>) input;
		} catch (Exception e1) {
			log.error("The cache will be empty");
			//cache = new HashMap<String, String>();
			return;
		}
		
		log.info("Done");
	}

	public void save(){
		
	}

	@Override
	public void persist() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(K key, V value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public V get(K key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
