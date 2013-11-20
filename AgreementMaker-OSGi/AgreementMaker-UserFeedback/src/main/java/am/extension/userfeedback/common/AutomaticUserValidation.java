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
		//we don't look at the relations
		if( experiment.getReferenceAlignment().contains(candidateMapping.getEntity1(),candidateMapping.getEntity2()) != null){//,candidateMapping.getRelation())) {
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
