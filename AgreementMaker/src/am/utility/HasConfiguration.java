package am.utility;

/**
 * An interface that allows for objects to maintain configuration settings. The
 * configuration allows for arbitrary objects to be stored, as well as just
 * Strings.
 * 
 * @author Cosmin Stroe
 * 
 */
public interface HasConfiguration {

	public void setConfigParam(String key, String value);
	
	public String getConfigParam(String key);

	public void setConfigObject(String key, Object value);
	
	public Object getConfigObject(String key);
	
}
