package am.app.mappingEngine;

import javax.swing.SwingWorker;

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
public class MatchingTask extends SwingWorker<Void,Void> {
	public AbstractMatcher 				matchingAlgorithm;
	public DefaultMatcherParameters 	matcherParameters;
	public AbstractSelectionAlgorithm 	selectionAlgorithm;
	public DefaultSelectionParameters 	selectionParameters;
	public MatcherResult				matcherResult;
	public SelectionResult				selectionResult;
	public int ID;
	
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
			matchingAlgorithm.setParam(matcherParameters);
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
			selectionAlgorithm.select(matcherResult);
			selectionResult = selectionAlgorithm.getResult();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		match();
		select();
		return null;
	}
}
