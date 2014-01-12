package am.extension.multiUserFeedback.evaluation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.SUExperiment;
import am.extension.userfeedback.experiments.UFLExperiment;

public class ServerFeedbackEvaluation extends PropagationEvaluation<MUExperiment>{
	MUExperiment experiment;
	List<AbstractMatcher> inputMatchers;
	Object[][] trainingSet;
	
	private DeltaFromReference deltaFromReference;
	


	@Override
	public void evaluate(MUExperiment exp) {

		deltaFromReference = new DeltaFromReference(exp.getReferenceAlignment());
		
		int delta = deltaFromReference.getDelta(exp.getMLAlignment());//exp.getFinalAlignment());
		
		AlignmentMetrics metrics = new AlignmentMetrics(exp.getReferenceAlignment(), exp.getMLAlignment()); //exp.getFinalAlignment());
		
		//Logger log = Logger.getLogger(this.getClass());
		MUExperiment log = exp;
		log.info("Iteration: " + exp.getIterationNumber() + ", Delta from reference: " + delta + 
				", Precision: " + metrics.getPrecisionPercent() + ", Recall: " + metrics.getRecallPercent() + ", FMeasure: " + metrics.getFMeasurePercent());
		log.info("");
		
		
		done();
	}



	
}
