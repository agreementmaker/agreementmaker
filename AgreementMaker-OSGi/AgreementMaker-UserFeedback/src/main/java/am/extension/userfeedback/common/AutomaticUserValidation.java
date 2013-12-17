package am.extension.userfeedback.common;

import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UserFeedback;

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
			if(experiment.getIterationNumber() == 5 || experiment.getIterationNumber() == 10 || experiment.getIterationNumber() == 15 || experiment.getIterationNumber() == 20) {
				userValidation = Validation.INCORRECT;
				log.info("Automatic Evaluation: Incorrect mapping, " + candidateMapping.toString());
			}
			else {
				userValidation = Validation.CORRECT;
				log.info("Automatic Evaluation: Correct mapping, " + candidateMapping.toString() );

			}
		}
		else {
			if(experiment.getIterationNumber() == 5 || experiment.getIterationNumber() == 10 || experiment.getIterationNumber() == 15 || experiment.getIterationNumber() == 20) {
				userValidation = Validation.CORRECT;
				log.info("Automatic Evaluation: Correct mapping, " + candidateMapping.toString());
			}
			else {
				userValidation = Validation.INCORRECT;
				log.info("Automatic Evaluation: Incorrect mapping, " + candidateMapping.toString() );
			}
		}
		
		log.info("");
		
		done();
	}

	@Override
	public void setUserFeedback(Validation feedback) { /* not implemented */ }

}
