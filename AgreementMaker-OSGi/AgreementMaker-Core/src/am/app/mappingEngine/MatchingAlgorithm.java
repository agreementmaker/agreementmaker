package am.app.mappingEngine;

import java.io.Serializable;

/**
 * This interface is meant to be implemented by any AgreementMaker matcher.
 * 
 * @author joe
 *
 */
public interface MatchingAlgorithm extends Serializable {
	
	public void setParameters(DefaultMatcherParameters param);
	
	public void match() throws Exception;
	
	/**
	 * @return A human readable name for this matcher.
	 */
	public String getName();
	
	/**
	 * @return The result from this matcher after matching has finished.
	 */
	public MatcherResult getResult();
	
	/**
	 * @param result
	 *            Set the result of this matcher. Allows to use a previously
	 *            computed result.
	 */
	public void setResult(MatcherResult result);
}
