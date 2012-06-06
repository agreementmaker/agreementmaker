package am.app.mappingEngine;

import java.io.Serializable;


/**
 * Replaced by {@link MatchingAlgorithm}. -- Cosmin.
 */
@Deprecated
public interface Matcher extends Serializable{
	
	public void match() throws Exception;
	
	public void select();
	
	public MatcherResult getResult();

}
