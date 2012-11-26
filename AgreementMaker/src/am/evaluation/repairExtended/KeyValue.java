package am.evaluation.repairExtended;

public class KeyValue<K,V> {

	private K Key;
	private V Value;
	//private Integer Flag;
	
	public KeyValue(K key, V value){
		Key = key;
		Value = value;
		//Flag = 0;
	}
	
	/*public KeyValue(K key, V value, Integer flag){
		Key = key;
		Value = value;
		Flag = flag;
	}*/
	
	public void setKey (K key)
    {
    	Key = key;           
    }
    public K getKey()
    {
        return Key;
    }

    public void setValue (V value)
    {
    	Value = value;           
    }
    public V getValue()
    {
        return Value;
    }	
    
    /*public void setFlag (Integer flag)
    {
    	Flag = flag;           
    }
    public Integer getFlag()
    {
        return Flag;
    }*/
}
