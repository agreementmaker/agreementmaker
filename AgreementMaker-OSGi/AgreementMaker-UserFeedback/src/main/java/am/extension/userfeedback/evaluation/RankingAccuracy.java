package am.extension.userfeedback.evaluation;

import java.io.Serializable;
import java.util.Arrays;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;

public class RankingAccuracy extends CandidateSelectionEvaluation {
	private int falsePositive=0;
	private int falseNegative=0;
	private int count=0;
	private UFLExperiment experiment;
	
	private ServerCSEvaluationData data;
	
	@Override
	public void evaluate(UFLExperiment exp) {

		Alignment<Mapping> referenceAlignment = exp.getReferenceAlignment();
		
		Mapping candidateMapping = exp.candidateSelection.getSelectedMapping();
		
		this.experiment = exp;

		Alignment<Mapping> computedAlignment = exp.getFinalAlignment();
		if ((computedAlignment.contains(candidateMapping)) && (!referenceAlignment.contains(candidateMapping))) {
			falsePositive++;
		}
		if ((!computedAlignment.contains(candidateMapping)) && (referenceAlignment.contains(candidateMapping))) {
			falseNegative++;
		}
		count++;
		
		double accuracy = ((double)(falseNegative+falsePositive)) / (double)count;
		
		if (candidateMapping != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("\tRanking Accuracy: ")
			  .append(accuracy).append(" ")
			  .append(falsePositive).append(" ")
			  .append(falseNegative).append(" ")
			  .append(count).append(" ")
			  .append(candidateMapping);
			experiment.info(sb.toString());
		}
		
		if( data == null) {
			int numIterations = exp.setup.parameters.getIntParameter(Parameter.NUM_ITERATIONS);
			data = new ServerCSEvaluationData(numIterations);
		}
	
		// save all the values
		int currentIndex = exp.getIterationNumber()-1;
		data.accuracy[currentIndex]      = accuracy;
		data.falsePositive[currentIndex] = falsePositive;
		data.falseNegative[currentIndex] = falseNegative;
		data.totalFeedback[currentIndex] = count;
		
		
		done();
		
	}
	
	public ServerCSEvaluationData getData() {
		return data;
	}
	
public class ServerCSEvaluationData implements Serializable {
		

		
	private static final long serialVersionUID = 2530851739600524219L;
	
		public double[] accuracy; // the precision for each iteration
		public int[] falsePositive;    // the recall for each iteration
		public int[] falseNegative;  // the fmeasure for each iteration
		public int[] totalFeedback;        // the delta from reference for each iteration
		
		public ServerCSEvaluationData(int numIterations) {
			// +1 because we will store the initial matchers data also.
			falsePositive = new int[numIterations+1]; 
			falseNegative    = new int[numIterations+1];
			accuracy  = new double[numIterations+1];
			totalFeedback     = new int[numIterations+1];
		}
		
		@Override
		public boolean equals(Object obj) {
			if( !(obj instanceof ServerCSEvaluationData) ) return false;
			
			ServerCSEvaluationData data = (ServerCSEvaluationData) obj;
			
			return Arrays.equals(data.accuracy, accuracy) &&
				   Arrays.equals(data.falsePositive, falsePositive) &&
				   Arrays.equals(data.falseNegative, data.falseNegative) &&
				   Arrays.equals(data.totalFeedback, data.totalFeedback);
		}
	}

}
