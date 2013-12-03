package am.utility.parameters;

/**
 * Part of the generic parameters framework.
 * 
 * This class represents a single parameter value.
 * The parameter can be either an integer (int), string (String),
 * decimal (double), or bit (boolean).
 * 
 * Use the set method to set the value, and then use the correct
 * get method to retrieve it.
 * 
 * Every AMParameter has a key associated with it, and is used to
 * identify that parameter.  If a null string value is passed to the
 * constructor, the key is assumed to be blank (not null).
 * 
 * @author Cosmin Stroe
 *
 */

public class AMParameter {

	public enum Type {
		integer,
		string,
		decimal,
		bit;
	}
	
	private String key;
	private Type type;
	
	private int 	intVal;
	private double 	decVal;
	private String 	strVal;
	private boolean bitVal;
	
	public AMParameter(String key) {
		if( key == null ) { key = ""; }
		else this.key = key;
	}
	
	public AMParameter(String key, int val) {
		if( key == null ) { key = ""; }
		else this.key = key;
		set(val);
	}
	
	public AMParameter(String key, double val) {
		if( key == null ) { key = ""; }
		else this.key = key;
		set(val);
	}
	
	public AMParameter(String key, String val) {
		if( key == null ) { key = ""; }
		else this.key = key;
		set(val);
	}
	
	public AMParameter(String key, boolean val) {
		if( key == null ) { key = ""; }
		else this.key = key;
		set(val);
	}
	
	public void set( int val ) {
		// clear string value
		if( type == Type.string ) strVal = null;
		
		// set new value
		type = Type.integer;
		intVal = val;
	}
	
	public void set( double val ) {
		// clear string value
		if( type == Type.string ) strVal = null;
		
		// set new value
		type = Type.decimal;
		decVal = val;
	}
	
	public void set( String val ) {
		// clear string value
		if( type == Type.string ) strVal = null;
		
		// set new value
		type = Type.string;
		strVal = new String(val);
	}
	
	public void set( boolean val ) {
		// clear string value
		if( type == Type.string ) strVal = null;
		
		// set new value
		type = Type.bit;
		bitVal = val;
	}
	
	
	public int getInt() { return intVal; }
	public boolean getBit() { return bitVal; }
	public String getStr() { return strVal; }
	public double getDec() { return decVal; }
	public Type getType() { return type; }
	public String getKey() { return key; }
}
