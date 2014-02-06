package am.extension.userfeedback.common;

import java.io.Serializable;
import java.util.Arrays;

public class ServerFeedbackEvaluationData implements Serializable {
		
	private static final long serialVersionUID = -6384728913529143938L;
	
	public double[] precisionArray; // the precision for each iteration
	public double[] recallArray;    // the recall for each iteration
	public double[] fmeasureArray;  // the fmeasure for each iteration
	public int[] deltaArray;        // the delta from reference for each iteration
	
	public ServerFeedbackEvaluationData(int numIterations) {
		// +1 because we will store the initial matchers data also.
		precisionArray = new double[numIterations+1]; 
		recallArray    = new double[numIterations+1];
		fmeasureArray  = new double[numIterations+1];
		deltaArray     = new int[numIterations+1];
	}
	
	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof ServerFeedbackEvaluationData) ) return false;
		
		ServerFeedbackEvaluationData data = (ServerFeedbackEvaluationData) obj;
		
		return Arrays.equals(data.precisionArray, precisionArray) &&
			   Arrays.equals(data.recallArray, recallArray) &&
			   Arrays.equals(data.fmeasureArray, data.fmeasureArray) &&
			   Arrays.equals(data.deltaArray, data.deltaArray);
	}
}
