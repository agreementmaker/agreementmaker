package am.extension.multiUserFeedback.validation;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.UserFeedback;
import am.extension.userfeedback.experiments.UFLExperiment;

public class BMAutomaticValidation extends UserFeedback {
	
	double errorThreshold=0.3;
	Validation userValidation;
	Mapping candidateMapping;

	/**
	 * If false, the user validation will ignore the relation type of the mapping.
	 * TODO: Make this be a parameter that can be changed programatically.
	 */
	private boolean considerRelationType = false;
		
	@Override public Validation getUserFeedback() { return userValidation; }
	@Override public Mapping getCandidateMapping() 
	{ 
		return candidateMapping; 
	}

	@Override
	public void validate(UFLExperiment experiment) {
		
		//Logger log = Logger.getLogger(this.getClass());
		UFLExperiment log = experiment;
		candidateMapping = experiment.candidateSelection.getSelectedMapping();

		if( candidateMapping == null || experiment.getIterationNumber() > 100 ) {
			userValidation = Validation.END_EXPERIMENT;
			done();
			return;
		}

		final Alignment<Mapping> ref = experiment.getReferenceAlignment();
		if( ( considerRelationType && ref.contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation()) ) ||
			(!considerRelationType && ref.contains(candidateMapping.getEntity1(), candidateMapping.getEntity2()) != null ) ) 
		{
			userValidation = Validation.CORRECT;
			log.info("Automatic Evaluation: Correct mapping, " + candidateMapping.toString() );
		}
		else {
			userValidation = Validation.INCORRECT;
			log.info("Automatic Evaluation: Incorrect mapping, " + candidateMapping.toString() );
		}
		
		double errorProb=Math.random();
		if (errorProb<errorThreshold)
		{
			if (userValidation==Validation.CORRECT)
				userValidation=Validation.INCORRECT;
			else
				userValidation=Validation.CORRECT;
		}
		
		log.info("");
		
		done();
	}

	@Override
	public void setUserFeedback(Validation feedback) { /* not implemented */ }

}
