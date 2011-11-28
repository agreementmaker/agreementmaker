package am.app.userfeedback;

/**
 * This class is just a wrapper class for several registry enums.
 * 
 * @author cosmin
 *
 */
public class UFLRegistry {

	/* Different experimental setups (Ontologies + Reference alignment) */
	public enum ExperimentRegistry {
		Manual ( am.app.userfeedback.common.ManualExperimentSetup.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends UFLExperiment> clazz;

		ExperimentRegistry( Class<? extends UFLExperiment> cs ) { clazz = cs; }
		public Class<? extends UFLExperiment> getEntryClass() { return clazz; }
	}
	
	public enum InitialMatcherRegistry {
		OrthoCombination ( am.app.userfeedback.disagreementclustering.OrthoCombinationMatcher.class );
			
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends ExecutionSemantics> clazz;
		
		InitialMatcherRegistry( Class<? extends ExecutionSemantics> cs ) { clazz = cs; }
		public Class<? extends ExecutionSemantics> getEntryClass() { return clazz; }
	}
	
	public enum CandidateSelectionRegistry {
		
		DisagreementRank ( am.app.userfeedback.disagreementclustering.DisagreementRanking.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends CandidateSelection> clazz;
		
		CandidateSelectionRegistry( Class<? extends CandidateSelection> cs ) { clazz = cs; }
		public Class<? extends CandidateSelection> getEntryClass() { return clazz; }
	}
	
	public enum CSEvaluationRegistry {
		PrecisionRecallEval ( am.app.userfeedback.common.PrecisionRecallPlot.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends CandidateSelectionEvaluation> clazz;
		
		CSEvaluationRegistry( Class<? extends CandidateSelectionEvaluation> cs ) { clazz = cs; }
		public Class<? extends CandidateSelectionEvaluation> getEntryClass() { return clazz; }
	}
	
	public enum UserValidationRegistry {
		AutomaticReference ( am.app.userfeedback.common.AutomaticUserValidation.class ),
		Manual ( am.app.userfeedback.common.ManualUserValidation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends UserFeedback> clazz;
		
		UserValidationRegistry( Class<? extends UserFeedback> cs ) { clazz = cs; }
		public Class<? extends UserFeedback> getEntryClass() { return clazz; }
	}
	
	public enum FeedbackPropagationRegistry {
		ClusterBoost ( am.app.userfeedback.disagreementclustering.ClusterBoostPropagation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends FeedbackPropagation> clazz;
		
		FeedbackPropagationRegistry( Class<? extends FeedbackPropagation> cs ) { clazz = cs; }
		public Class<? extends FeedbackPropagation> getEntryClass() { return clazz; }
	}
	
	public enum PropagationEvaluationRegistry {
		DeltaFromRef ( am.app.userfeedback.disagreementclustering.DeltaFromReferenceEvaluation.class ),
		ClusterBoost ( am.app.userfeedback.disagreementclustering.ClusterBoostEvaluation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends PropagationEvaluation> clazz;
		
		PropagationEvaluationRegistry( Class<? extends PropagationEvaluation> cs ) { clazz = cs; }
		public Class<? extends PropagationEvaluation> getEntryClass() { return clazz; }
	}
}
