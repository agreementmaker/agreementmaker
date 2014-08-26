package am.extension.multiUserFeedback.evaluation;

import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.evaluation.PropagationEvaluation;

public class ServerFeedbackEvaluation extends PropagationEvaluation<MUExperiment>{
	
	@Override
	public void evaluate(MUExperiment exp) {
		done();
	}
}
