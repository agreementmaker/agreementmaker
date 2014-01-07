package am.extension.multiUserFeedback.evaluation;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.evaluation.PropagationEvaluation;

public class ServerFeedbackEvaluation extends PropagationEvaluation<MUExperiment>{
	MUExperiment experiment;
	List<AbstractMatcher> inputMatchers;
	Object[][] trainingSet;
	
	
	private DeltaFromReference deltaFromReference;
	


	@Override
	public void evaluate(MUExperiment exp) {
		experiment=exp;
		deltaFromReference = new DeltaFromReference(exp.getReferenceAlignment());
		
		int delta = deltaFromReference.getDelta(exp.getMLAlignment());
		AlignmentMetrics metrics = new AlignmentMetrics(exp.getReferenceAlignment(), exp.getMLAlignment());
		
		MUExperiment log = exp;
		log.info("Iteration: " + exp.getIterationNumber() + ", Delta from reference: " + delta + 
				", Precision: " + metrics.getPrecisionPercent() + ", Recall: " + metrics.getRecallPercent() + ", FMeasure: " + metrics.getFMeasurePercent());
		log.info("");
		

		
		done();
	}


	
}
