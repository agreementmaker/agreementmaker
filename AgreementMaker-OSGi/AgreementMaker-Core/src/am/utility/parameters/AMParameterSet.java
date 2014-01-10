package am.utility.parameters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import am.app.mappingEngine.Mapping;
import am.utility.parameters.AMParameter.Type;

/**
 * Part of the generic parameters framework.
 * 
 * This class manages a collection of AMParameters.
 * 
 * @author Cosmin Stroe
 *
 */
public class AMParameterSet extends HashMap<String,AMParameter> {

	private static final long serialVersionUID = 6798638512408573380L;
	
	public AMParameterSet() {
		super();
	}
	
	public AMParameterSet( Collection<AMParameter> paramList ) {
		super();
		
		for( AMParameter p : paramList ) {
			put( p.getKey() , p );
		}
	}
	
	public void put( AMParameter p ) {
		put( p.getKey() , p );
	}
	
	public void putAll( AMParameterSet ps ) {
		for( Map.Entry<String, AMParameter> entry : ps.entrySet() ) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	public int 	   getInt( String key ) throws Exception { return get(key).getInt(); }
	public double  getDec( String key ) throws Exception { return get(key).getDec(); }
	public String  getStr( String key ) throws Exception { return get(key).getStr(); }
	public boolean getBit( String key ) throws Exception { return get(key).getBit(); }
	public Type getType( String key ) throws Exception { return get(key).getType(); }
	
	
}
