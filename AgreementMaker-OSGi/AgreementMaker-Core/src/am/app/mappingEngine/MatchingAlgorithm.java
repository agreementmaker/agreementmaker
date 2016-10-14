package am.app.mappingEngine;

import java.io.Serializable;

/**
 * This interface is meant to be implemented by any AgreementMaker matcher.
 * 
 * @author joe
 *
 */
public interface MatchingAlgorithm extends Serializable {

	void setParameters(DefaultMatcherParameters param);

	void match() throws Exception;
	
	/** @return A human readable name for this matcher. */
	String getName();
	
	/** @return The result from this matcher after matching has finished. */
	MatcherResult getResult();
	
	/**
	 * @param result
	 *            Set the result of this matcher. Allows to use a previously
	 *            computed result.
	 */
	void setResult(MatcherResult result);
}
