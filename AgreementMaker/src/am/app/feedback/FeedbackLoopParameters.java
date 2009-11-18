package am.app.feedback;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;

public class FeedbackLoopParameters extends AbstractParameters {

	
	
	public int K = 4;
	public int M = 2;
	
	public double highThreshold = 0.7d;
	public double lowThreshold = 0.0d;
	
	public String configuration = FeedbackLoop.MANUAL;
	
	public String cardinality = "1-1";
	
	public int sourceNumMappings = 1;
	public int targetNumMappings = 1;
	public int iterations = 15;
	
	public AbstractMatcher matcher;
	
	public String getParameterString(){
		String result = "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=";
		result += "\nFeedback Loop parameters:";
		result += "\n\tMatcher:       \t"+ matcher.getName().getMatcherName();
		result += "\n\tConfiguration: \t"+ configuration ;
		result += "\n\tIterations:    \t"+ iterations ;
		result += "\n\tHigh Threshold:\t" + Double.toString(highThreshold) ;
		result += "\n\tLow Threshold: \t" + Double.toString(lowThreshold) ;
		result += "\n\tK:             \t" + Integer.toString(K) ;
		result += "\n\tM:             \t" + Integer.toString(M) ;
		result += "\n\tCardinality:   \t" + cardinality ;
		result += "\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=";
		return result;
	}
	
	public void print() {
		System.out.println( "\n"+getParameterString()+"\n");
	}
	
}
