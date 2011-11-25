package am.app.userfeedbackloop.common;

import org.apache.log4j.Logger;

import am.app.mappingEngine.Mapping;
import am.app.userfeedback.UFLExperiment;
import am.app.userfeedback.UserFeedback;

public class AutomaticUserValidation extends UserFeedback {

	Validation userValidation;
	Mapping candidateMapping;

	@Override public Validation getUserFeedback() { return userValidation; }
	@Override public Mapping getCandidateMapping() { return candidateMapping; }

	@Override
	public void validate(UFLExperiment experiment) {
		
		//Logger log = Logger.getLogger(this.getClass());
		UFLExperiment log = experiment;
		candidateMapping = experiment.candidateSelection.getCandidateMapping();
		
		if( candidateMapping == null || experiment.getIterationNumber() > 100 ) {
			userValidation = Validation.END_EXPERIMENT;
			return;
		}
		
		if( experiment.getReferenceAlignment().contains(candidateMapping.getEntity1(),candidateMapping.getEntity2(),candidateMapping.getRelation())) {
			userValidation = Validation.CORRECT;
			log.info("Automatic Evaluation: Correct mapping, " + candidateMapping.toString() );
		}
		else {
			userValidation = Validation.INCORRECT;
			log.info("Automatic Evaluation: Incorrect mapping, " + candidateMapping.toString() );
		}
		
		log.info("");
		
		done();
	}

	@Override
	public void setUserFeedback(Validation feedback) { /* not implemented */ }

}
