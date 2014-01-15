package am.extension.multiUserFeedback.evaluation;

import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.evaluation.PropagationEvaluation;

public class ServerFeedbackEvaluation extends PropagationEvaluation<MUExperiment>{

	@Override
	public void evaluate(MUExperiment exp) {

		DeltaFromReference deltaFromReference = new DeltaFromReference(exp.getReferenceAlignment());
		int delta = deltaFromReference.getDelta(exp.getFinalAlignment());
		
		AlignmentMetrics metrics = new AlignmentMetrics(exp.getReferenceAlignment(), exp.getFinalAlignment());
		
		exp.info("Iteration: " + exp.getIterationNumber() + ", Delta from reference: " + delta + 
				", Precision: " + metrics.getPrecisionPercent() + ", Recall: " + metrics.getRecallPercent() + ", FMeasure: " + metrics.getFMeasurePercent());
		exp.info("");
		
		done();
	}
	
}
