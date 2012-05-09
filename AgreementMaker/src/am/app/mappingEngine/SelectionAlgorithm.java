package am.app.mappingEngine;

/**
 * Represents a mapping selection algorithm.
 * 
 * @author joe
 *
 */
public interface SelectionAlgorithm {

	/**
	 * @param matcherResult Results from running a matching algorithm.
	 * @return The input matcher result with recomputed alignments.
	 */
	public void select(MatcherResult matcherResult);
	
}
