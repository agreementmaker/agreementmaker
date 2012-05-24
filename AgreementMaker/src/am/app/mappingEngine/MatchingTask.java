package am.app.mappingEngine;

/**
 * A matching task contains:
 * 	- A matching algorithm.
 * 	- Parameters for the matching algorithm.
 * 	- A selection algorithm.
 * 	- Parameters for the selection algorithm.
 *  - The MatcherResult after the matcher has executed.
 *  - The SelectionResult after the selection algorithm has executed.
 * 
 * @author Cosmin Stroe
 *
 */
public class MatchingTask {
	public AbstractMatcher 				matchingAlgorithm;
	public DefaultMatcherParameters 	matcherParameters;
	public AbstractSelectionAlgorithm 	selectionAlgorithm;
	public DefaultSelectionParameters 	selectionParameters;
	public MatcherResult				matcherResult;
	public SelectionResult				selectionResult;
}
