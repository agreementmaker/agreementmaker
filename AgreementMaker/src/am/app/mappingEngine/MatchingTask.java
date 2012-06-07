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
 * TODO: Make the property change support work. - Cosmin
 */
public class MatchingTask {
	public AbstractMatcher 				matchingAlgorithm;
	public DefaultMatcherParameters 	matcherParameters;
	public AbstractSelectionAlgorithm 	selectionAlgorithm;
	public DefaultSelectionParameters 	selectionParameters;
	public MatcherResult				matcherResult;
	public SelectionResult				selectionResult;
	public int ID;
	public String label;
	
	public MatchingTask(AbstractMatcher matcher, DefaultMatcherParameters matcherParams,
						AbstractSelectionAlgorithm selectionAlgorithm, DefaultSelectionParameters selectionParams) {
		super();
		
		this.matchingAlgorithm = matcher;
		this.matcherParameters = matcherParams;
		this.selectionAlgorithm = selectionAlgorithm;
		this.selectionParameters = selectionParams;
	}

	/**
	 * Run the matching algorithm.
	 */
	public void match() {
		try {
			matchingAlgorithm.setParameters(matcherParameters);
			matchingAlgorithm.match();
			matcherResult = matchingAlgorithm.getResult();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * After the matching algorithm runs, run the selection algorithm.
	 */
	public void select() {
		try {
			selectionAlgorithm.setParameters(selectionParameters);
			selectionAlgorithm.select();
			selectionResult = selectionAlgorithm.getResult();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public int getID() {
		return ID;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
}
