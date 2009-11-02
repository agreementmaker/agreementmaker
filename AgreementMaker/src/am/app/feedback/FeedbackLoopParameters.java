package am.app.feedback;

import am.app.mappingEngine.AbstractParameters;

public class FeedbackLoopParameters extends AbstractParameters {

	
	
	public int K = 4;
	public int M = 2;
	
	public double highThreshold = 0.7d;
	public double lowThreshold = 0.0d;
	
	public String configuration = "Default";
	
	public String cardinality = "1-1";
	
	public int sourceNumMappings = 1;
	public int targetNumMappings = 1;
	
}
