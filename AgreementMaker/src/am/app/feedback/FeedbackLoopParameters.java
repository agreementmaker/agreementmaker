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
	
	
	public void print() {
		System.out.println( "\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		System.out.println( "Feedback Loop PARAMETERS: ");
		System.out.println( "\tMatcher: \t"+ matcher.getName().getMatcherName());
		System.out.println( "\tConfiguration: \t"+ configuration );
		System.out.println( "\tIterations:    \t"+ iterations );
		System.out.println( "\tHigh Threshold:\t" + Double.toString(highThreshold) );
		System.out.println( "\tLow Threshold: \t" + Double.toString(lowThreshold) );
		System.out.println( "\tK:             \t" + Integer.toString(K) );
		System.out.println( "\tM:             \t" + Integer.toString(M) );
		System.out.println( "\tCardinality:   \t" + cardinality );
		System.out.println( "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");
	}
	
}
