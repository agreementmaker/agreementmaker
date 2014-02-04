package am.extension.multiUserFeedback.evaluation;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;

public class ServerFeedbackEvaluation extends PropagationEvaluation<MUExperiment>{
	
	private ServerFeedbackEvaluationData data;
	
	@Override
	public void evaluate(MUExperiment exp) {
		
		// initialization
		if( data == null) {
			int numIterations = exp.setup.parameters.getIntParameter(Parameter.NUM_ITERATIONS);
			data = new ServerFeedbackEvaluationData(numIterations-1);
		}
		
		// compute the delta from reference
		DeltaFromReference deltaFromReference = new DeltaFromReference(exp.getReferenceAlignment());
		int delta = deltaFromReference.getDelta(exp.getFinalAlignment());
		
		// alignment metrics: precision, recall, fmeasure.
		AlignmentMetrics metrics = new AlignmentMetrics(exp.getReferenceAlignment(), exp.getFinalAlignment());
		
		// save all the values
		int currentIteration = exp.getIterationNumber();
		data.precisionArray[currentIteration] = metrics.getPrecision();
		data.recallArray   [currentIteration] = metrics.getRecall();
		data.fmeasureArray [currentIteration] = metrics.getFMeasure();
		data.deltaArray    [currentIteration] = delta;
		
		exp.info("Iteration: " + exp.getIterationNumber() + 
				", Delta from reference: " + delta + 
				", Precision: " + metrics.getPrecisionPercent() + 
				", Recall: " + metrics.getRecallPercent() + 
				", FMeasure: " + metrics.getFMeasurePercent());
		exp.info("");
		
		done();
	}
	
	public ServerFeedbackEvaluationData getData() {
		return data;
	}
	
	// class to store all the data we gather
	public class ServerFeedbackEvaluationData implements Serializable {
		
		private static final long serialVersionUID = -6384728913529143938L;
		
		public double[] precisionArray; // the precision for each iteration
		public double[] recallArray;    // the recall for each iteration
		public double[] fmeasureArray;  // the fmeasure for each iteration
		public int[] deltaArray;        // the delta from reference for each iteration
		
		public ServerFeedbackEvaluationData(int numIterations) {
			precisionArray = new double[numIterations]; 
			recallArray    = new double[numIterations];
			fmeasureArray  = new double[numIterations];
			deltaArray     = new int[numIterations];
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
}
