package am.app.ontology.instance;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class Instance {
	protected String uri;
	protected String type;
	
	protected Hashtable<String,List<String>> properties;
	
	public Instance(String uri, String type) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public Enumeration<String>  listProperties() {
		return properties.keys();
	}
	
	public List<String> getProperty( String key ) {
		return properties.get(key);
	}
	
	/** Passing a null value will remove the key from the properties table. */
	public void setProperty( String key, List<String> value ) {
		if( value == null ) {
			properties.remove(key);
		} else {
			properties.put(key,value);
		}
	}

	public String getType() {
		return type;
	}
	
}

