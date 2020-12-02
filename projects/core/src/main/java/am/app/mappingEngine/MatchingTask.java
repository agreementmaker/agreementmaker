package am.app.mappingEngine;

import java.util.ArrayList;
import java.util.List;


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
	public SelectionAlgorithm 			selectionAlgorithm;
	public DefaultSelectionParameters 	selectionParameters;
	public MatcherResult				matcherResult;
	public SelectionResult				selectionResult;
	public int ID;
	
	public List<MatchingTask>			inputMatchingTasks;
	
	/**
	 * A short label for this MatchingTask.
	 */
	public String shortLabel;
	
	/**
	 * A longer description for this matching task.
	 */
	public String description;
	
	public MatchingTask(AbstractMatcher matcher, DefaultMatcherParameters matcherParams,
						 SelectionAlgorithm selectionAlgorithm, DefaultSelectionParameters selectionParams) {
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
			selectionParameters.inputResult = matcherResult;
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
	
	public String getShortLabel() {
		if( shortLabel != null ) return shortLabel;
		if (matchingAlgorithm != null) {
			return matchingAlgorithm.getName();
		}
		return "Task " + Integer.toString(ID);
	}
	
	public void setLabel(String label) {
		this.shortLabel = label;
	}
	
	public void addManualAlignments(ArrayList<Mapping> alignments) throws Exception {
		throw new RuntimeException ("FIX ME!!!!! (Implement me)");
	}

	public String getMatchingReport() {
		StringBuilder report = new StringBuilder();
		report.append("Matching Process Complete Succesfully!\n\n");
		if (selectionResult != null) {
			if (selectionResult.classesAlignment != null) {
				report.append("Classes alignments found: ").append(selectionResult.classesAlignment.size()).append("\n");
			} else {
				report.append("Classes alignments found: 0").append("\n");
			}

			if (selectionResult.propertiesAlignment != null) {
				report.append("Properties alignments found: ").append(selectionResult.propertiesAlignment.size()).append("\n");
			} else {
				report.append("Properties alignments found: 0").append("\n");
			}

		}
		
		return report.toString();
	}
}
