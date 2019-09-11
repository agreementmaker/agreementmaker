package am.app.mappingEngine;


public class DefaultSelectionParameters {

	public double threshold = 0.6;
	public int maxSourceAlign = 1;
	public int maxTargetAlign = 1;
	
	public boolean alignClasses = true;
	public boolean alignProperties = true;
	
	public MatchingTask matchingTask;
	
	public MatcherResult inputResult;
}
