package am.app.mappingEngine;

/**
 * Represents a mapping selection algorithm.
 * 
 * @author Cosmin Stroe
 * @author Joe Lozar
 */
public interface SelectionAlgorithm {

	/**
	 * The MatcherResult is part of the SelectionParameters.
	 */
	public void setParameters(DefaultSelectionParameters param);
	
	public void select();
	
	public SelectionResult getResult();
	
	public String getName();
}
