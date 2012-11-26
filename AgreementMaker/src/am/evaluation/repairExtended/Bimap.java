package am.evaluation.repairExtended;

import java.util.ArrayList;

public class Bimap<K,V> {
	
	private ArrayList<KeyValue<K,V>> Bimap = new ArrayList<KeyValue<K,V>>();
	
	public void add(K key, V value){
	
		KeyValue<K,V> pair = new KeyValue<K, V>(key,value);
		
		if(!Bimap.contains(pair))
			Bimap.add(pair);
	}
	
	public ArrayList<V> getValuesByKey(K key){
		
		ArrayList<V> values = new ArrayList<V>();
		
		for(KeyValue<K,V> pair : Bimap){
			if(pair.getKey() == key)
				values.add(pair.getValue());
		}
		
		return values;		
	}
	
	public ArrayList<K> getKeysByValue(V value){
		
		ArrayList<K> keys = new ArrayList<K>();
		
		for(KeyValue<K,V> pair : Bimap){
			if(pair.getValue() == value)
				keys.add(pair.getKey());
		}
		
		return keys;		
	}
	
	public K getKeyByValue(V value){		
				
		K key = null;
		
		for(KeyValue<K,V> pair : Bimap){
			
			if(pair.getValue() == value){
				key = pair.getKey();
				break;
			}
		}
		
		return key;		
	}
		
	public void remove(K key, V value){
		
		for(KeyValue<K,V> pair : Bimap){
			if(pair.getValue() == value && pair.getKey() == key)
				Bimap.remove(pair);
		}
		
	}
	
	public ArrayList<K> getBottomKeys(){
		
		ArrayList<K> bottomKeys = new ArrayList<K>();
		
		for(KeyValue<K,V> pair : Bimap){
			if(pair.getValue() == null)
				bottomKeys.add(pair.getKey());
		}
		
		return bottomKeys;
		
	}

	//get set
	public ArrayList<KeyValue<K,V>> getBimap(){
		return Bimap;
	}
	public void setBimap(ArrayList<KeyValue<K,V>> bimap){
		Bimap = bimap;
	}
	
	
	
	

}

