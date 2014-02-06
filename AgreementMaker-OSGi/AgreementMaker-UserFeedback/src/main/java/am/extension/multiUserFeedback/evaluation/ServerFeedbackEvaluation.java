package am.extension.multiUserFeedback.evaluation;

import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.evaluation.PropagationEvaluation;

public class ServerFeedbackEvaluation extends PropagationEvaluation<MUExperiment>{
	
	@Override
	public void evaluate(MUExperiment exp) {
		
		// compute the delta from reference
		DeltaFromReference deltaFromReference = new DeltaFromReference(exp.getReferenceAlignment());
		int delta = deltaFromReference.getDelta(exp.getFinalAlignment());
		
		// alignment metrics: precision, recall, fmeasure.
		AlignmentMetrics metrics = new AlignmentMetrics(exp.getReferenceAlignment(), exp.getFinalAlignment());
		
		// save all the values
		int currentIteration = exp.getIterationNumber();
		exp.feedbackEvaluationData.precisionArray[currentIteration] = metrics.getPrecision();
		exp.feedbackEvaluationData.recallArray   [currentIteration] = metrics.getRecall();
		exp.feedbackEvaluationData.fmeasureArray [currentIteration] = metrics.getFMeasure();
		exp.feedbackEvaluationData.deltaArray    [currentIteration] = delta;
		
		exp.info("Iteration: " + (exp.getIterationNumber()) + 
				", Delta from reference: " + delta + 
				", Precision: " + metrics.getPrecisionPercent() + 
				", Recall: " + metrics.getRecallPercent() + 
				", FMeasure: " + metrics.getFMeasurePercent());
		exp.info("");
		
		done();
	}
}
