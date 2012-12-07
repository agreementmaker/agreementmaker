package am.evaluation.repairExtended;

import java.util.ArrayList;
/**
 * @author Pavan
 *
 *	Bimap class is a generic key-value map, similar to bimap in guava lib but of course with a few additional specific functionalities.
 */
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
		
	//TODO : very specific for this implementation(assumes type K == V). 
	//Turns out I will be using this only when K == V, should fix it to be more generic if I have time
	@SuppressWarnings("unchecked")
	public ArrayList<V> getBottomKeys(){
		
		ArrayList<V> bottomKeys = new ArrayList<V>();
		
		for(V value : getValueArrayList()){
						
			if(!getKeyArrayList().contains((K)value))			
				bottomKeys.add(value);
		}
		
		return bottomKeys;
	}

	//getter setter
	public ArrayList<KeyValue<K,V>> getBimap(){
		return Bimap;
	}
	public void setBimap(ArrayList<KeyValue<K,V>> bimap){
		Bimap = bimap;
	}
	
	public ArrayList<K> getKeyArrayList(){
		
		ArrayList<K> keys = new ArrayList<K>();
		
		for(KeyValue<K,V> pair : Bimap){
			keys.add(pair.getKey());
		}
		
		return keys;		
	}
	
	public ArrayList<V> getValueArrayList(){
		
		ArrayList<V> values = new ArrayList<V>();
		
		for(KeyValue<K,V> pair : Bimap){
			values.add(pair.getValue());
		}
		
		return values;		
	}

}

