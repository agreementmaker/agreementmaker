package am.app.mappingEngine;

import java.io.Serializable;

/**
 * This interface is meant to be implemented by any AgreementMaker matcher.
 * 
 * @author joe
 *
 */
public interface MatchingAlgorithm extends Serializable {
	
	public void match() throws Exception;
	
	public MatcherResult getResult();

}
