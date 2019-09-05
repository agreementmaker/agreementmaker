package am.utility.parameters;

/**
 * Used to denote that a certain object can accept parameters.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 */
public interface HasParameters {

	/**
	 * Set a single parameter.
	 */
	public void setParameter( AMParameter param );
	
	/**
	 * Set multiple parameters.
	 */
	public void setParameters( AMParameterSet params );
	
}
