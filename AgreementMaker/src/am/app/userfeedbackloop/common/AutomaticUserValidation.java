package am.app.userfeedbackloop.common;

import am.app.mappingEngine.Mapping;
import am.app.userfeedbackloop.UFLExperiment;
import am.app.userfeedbackloop.UserFeedback;

public class AutomaticUserValidation extends UserFeedback {

	Validation userValidation;
	Mapping candidateMapping;

	@Override public Validation getUserFeedback() { return userValidation; }
	@Override public Mapping getCandidateMapping() { return candidateMapping; }

	@Override
	public void validate(UFLExperiment experiment) {
		
		candidateMapping = experiment.candidateSelection.getCandidateMapping();
		
		if( experiment.getReferenceAlignment().contains(candidateMapping))
			userValidation = Validation.CORRECT;
		else
			userValidation = Validation.INCORRECT;
		
		done();
	}


}
