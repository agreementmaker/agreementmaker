package am.extension.multiUserFeedback.validation;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.UserFeedback;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;

public class ProbabilisticErrorAutomaticValidation extends UserFeedback {
	
	//double errorThreshold=0.3;
	Validation userValidation;
	Mapping candidateMapping;

	/**
	 * The number of errors allowed for a single mapping. For example, if
	 * maxErrorCount = 1 then a mapping can be incorrectly validated only once
	 * (only one erroneous validation is allowed for that mapping).
	 */
	private final int maxErrorCount = 1;
	
	/**
	 * If false, the user validation will ignore the relation type of the mapping.
	 * TODO: Make this be a parameter that can be changed programmatically.
	 */
	private boolean considerRelationType = false;
		
	@Override public Validation getUserFeedback() { return userValidation; }
	
	@Override public Mapping getCandidateMapping() 
	{ 
		return candidateMapping; 
	}

	@Override
	public void validate(UFLExperiment experiment) {
		
		UFLExperiment log = experiment;
		
		candidateMapping = experiment.candidateSelection.getSelectedMapping();
		
		// end of the experiment?
		final int numIterations = experiment.setup.parameters.getIntParameter(Parameter.NUM_ITERATIONS);
		if( candidateMapping == null || experiment.getIterationNumber() > numIterations ) {
			userValidation = Validation.END_EXPERIMENT;
			log.info("\tAutomatic Evaluation: End of experiment.");
			log.info("");
			done();
			return;
		}

		final Alignment<Mapping> ref = experiment.getReferenceAlignment();
		if( ( considerRelationType && ref.contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation()) ) ||
			(!considerRelationType && ref.contains(candidateMapping.getEntity1(), candidateMapping.getEntity2()) != null ) ) 
		{
			userValidation = Validation.CORRECT;
			log.info("\tAutomatic Evaluation: Correct mapping, " + candidateMapping.toString() );
		}
		else {
			userValidation = Validation.INCORRECT;
			log.info("\tAutomatic Evaluation: Incorrect mapping, " + candidateMapping.toString() );
		}

		// can we generate an error for this mapping?
		if ( experiment.incorrectFeedbackCount.containsKey(candidateMapping) && 
				experiment.incorrectFeedbackCount.get(candidateMapping) >= maxErrorCount ) {
			// we cannot generate an error for this mapping because we have already 
			// reached the limit (maxErrorCount)
			done(); // end validation
			return;
		}
		
		// randomly generate an error		
		double errorRate = experiment.setup.parameters.getDoubleParameter(Parameter.ERROR_RATE);
		double errorProb = Math.random();

		if( errorProb < errorRate )
		{
			// increment the error count
			incrementErrorCount(experiment, candidateMapping);
			
			if( userValidation == Validation.CORRECT ) {
				userValidation = Validation.INCORRECT;
				log.info("\tGENERATED ERROR at iteration "+ experiment.getIterationNumber() + ": This mapping should be CORRECT: " + candidateMapping.toString() );
			}
			else {
				userValidation = Validation.CORRECT;
				log.info("\tGENERATED ERROR at iteration "+ experiment.getIterationNumber() + ": This mapping should be INCORRECT: " + candidateMapping.toString() );
			}
		}
		
		log.info("");
		
		done();
	}

	/**
	 * Increment the feedback counter for that mapping.
	 */
	private void incrementErrorCount(UFLExperiment experiment, Mapping candidateMapping) {
		if (experiment.incorrectFeedbackCount.containsKey(candidateMapping))
			experiment.incorrectFeedbackCount.put(candidateMapping, experiment.incorrectFeedbackCount.get(candidateMapping)+1);
		else
			experiment.incorrectFeedbackCount.put(candidateMapping, 1);
	}

	@Override
	public void setUserFeedback(Validation feedback) {
		throw new RuntimeException("Not implemented.");
	}

}
