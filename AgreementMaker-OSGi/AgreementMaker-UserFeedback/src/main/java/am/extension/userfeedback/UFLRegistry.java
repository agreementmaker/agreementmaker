package am.extension.userfeedback;

/**
 * This class is just a wrapper class for several registry enums.
 * 
 * @author cosmin
 *
 */
public class UFLRegistry {

	/* Different experimental setups (Ontologies + Reference alignment) */
	public enum ExperimentRegistry {
		MachineLearning ( am.extension.userfeedback.MLFeedback.MLFExperiment.class),
		Manual ( am.extension.userfeedback.common.ManualExperimentSetup.class );
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends UFLExperiment> clazz;

		ExperimentRegistry( Class<? extends UFLExperiment> cs ) { clazz = cs; }
		public Class<? extends UFLExperiment> getEntryClass() { return clazz; }
	}
	
	public enum InitialMatcherRegistry {
		OrthoCombination ( am.extension.userfeedback.clustering.disagreement.OrthoCombinationMatcher.class );
			
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends ExecutionSemantics> clazz;
		
		InitialMatcherRegistry( Class<? extends ExecutionSemantics> cs ) { clazz = cs; }
		public Class<? extends ExecutionSemantics> getEntryClass() { return clazz; }
	}
	
	public enum CandidateSelectionRegistry {
		
		MaxInformationRanking (am.extension.userfeedback.clustering.disagreement.MaxInformationRanking.class   ),
		DisagreementRank ( am.extension.userfeedback.clustering.disagreement.DisagreementRanking.class );
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends CandidateSelection> clazz;
		
		CandidateSelectionRegistry( Class<? extends CandidateSelection> cs ) { clazz = cs; }
		public Class<? extends CandidateSelection> getEntryClass() { return clazz; }
	}
	
	public enum CSEvaluationRegistry {
		PrecisionRecallEval ( am.extension.userfeedback.common.PrecisionRecallPlot.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends CandidateSelectionEvaluation> clazz;
		
		CSEvaluationRegistry( Class<? extends CandidateSelectionEvaluation> cs ) { clazz = cs; }
		public Class<? extends CandidateSelectionEvaluation> getEntryClass() { return clazz; }
	}
	
	public enum UserValidationRegistry {
		AutomaticReference ( am.extension.userfeedback.common.AutomaticUserValidation.class ),
		Manual ( am.extension.userfeedback.common.ManualUserValidation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends UserFeedback> clazz;
		
		UserValidationRegistry( Class<? extends UserFeedback> cs ) { clazz = cs; }
		public Class<? extends UserFeedback> getEntryClass() { return clazz; }
	}
	
	public enum FeedbackPropagationRegistry {
		MLFeedbackPropagationByLearningLinearWeights(am.extension.userfeedback.MLFeedback.MLFeedbackPropagationByLearningLinearWeights.class),
		MLFeedbackPropagation (am.extension.userfeedback.MLFeedback.MLFeedbackPropagation.class),
		ClusterBoost ( am.extension.userfeedback.clustering.disagreement.ClusterBoostPropagation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends FeedbackPropagation> clazz;
		
		FeedbackPropagationRegistry( Class<? extends FeedbackPropagation> cs ) { clazz = cs; }
		public Class<? extends FeedbackPropagation> getEntryClass() { return clazz; }
	}
	
	public enum PropagationEvaluationRegistry {
		SMatrixDeltaEvaluetion (am.extension.userfeedback.clustering.disagreement.SMatrixDeltaEvaluetion.class),
		DeltaFromRef ( am.extension.userfeedback.clustering.disagreement.DeltaFromReferenceEvaluation.class ),
		ClusterBoost ( am.extension.userfeedback.clustering.disagreement.ClusterBoostEvaluation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends PropagationEvaluation> clazz;
		
		PropagationEvaluationRegistry( Class<? extends PropagationEvaluation> cs ) { clazz = cs; }
		public Class<? extends PropagationEvaluation> getEntryClass() { return clazz; }
	}
	
	public enum SaveFeedbackRegistry {
		MultiUserSaveFeedback (am.extension.userfeedback.MultiUserSaveFeedback.class);
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends SaveFeedback> clazz;
		
		SaveFeedbackRegistry( Class<? extends SaveFeedback> cs ) { clazz = cs; }
		public Class<? extends SaveFeedback> getEntryClass() { return clazz; }
	}
}
