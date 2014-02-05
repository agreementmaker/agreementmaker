package am.extension.userfeedback;

import am.extension.multiUserFeedback.storage.FeedbackAgregation;
import am.extension.userfeedback.evaluation.CandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.inizialization.FeedbackLoopInizialization;
import am.extension.userfeedback.propagation.FeedbackPropagation;
import am.extension.userfeedback.selection.CandidateSelection;


/**
 * This class is just a wrapper class for several registry enums.
 * 
 * @author cosmin
 *
 */
public class UFLRegistry {

	/* Different experimental setups (Ontologies + Reference alignment) */
	public enum ExperimentRegistry {
		ServerExperiment ( am.extension.multiUserFeedback.experiment.MUExperiment.class ),
		ClientExperiment ( am.extension.userfeedback.experiments.MLFExperiment.class),
		SingleUser( am.extension.userfeedback.experiments.SUExperiment.class),
		Manual ( am.extension.userfeedback.common.ManualExperimentSetup.class );
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends UFLExperiment> clazz;

		ExperimentRegistry( Class<? extends UFLExperiment> cs ) { clazz = cs; }
		public Class<? extends UFLExperiment> getEntryClass() { return clazz; }
	}
	
	public enum InitialMatcherRegistry {
		SemanticStructuralCombination( am.extension.userfeedback.clustering.disagreement.SestCombinationMatchers.class ),
		LargeOrthoCombination ( am.extension.userfeedback.clustering.disagreement.LargeOntologyOrthoMatchers.class ),
		OrthoCombination ( am.extension.userfeedback.clustering.disagreement.OrthoCombinationMatchers.class );
			
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends ExecutionSemantics> clazz;
		
		InitialMatcherRegistry( Class<? extends ExecutionSemantics> cs ) { clazz = cs; }
		public Class<? extends ExecutionSemantics> getEntryClass() { return clazz; }
	}
	
	public enum LoopInizializationRegistry {
		ClientDataInizialization (am.extension.userfeedback.inizialization.RestfulDataInizialization.class),
		ServerDataInizialization (am.extension.multiUserFeedback.initialization.MUDataInitialization.class),
		DataInizialization ( am.extension.userfeedback.inizialization.DataInizialization.class);
			
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends FeedbackLoopInizialization> clazz;
		
		LoopInizializationRegistry( Class<? extends FeedbackLoopInizialization> fli ) { clazz = fli; }
		public Class<? extends FeedbackLoopInizialization> getEntryClass() { return clazz; }
	}
	
//	public enum MultiUserCandidateSelectionRegistry {
//		ServerCandidateSelection (am.extension.multiUserFeedback.ServerCandidateSelection.class);
//		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
//		Class<? extends MUCandidateSelection> clazz;
//		
//		void MUCandidateSelectionRegistry( Class<? extends MUCandidateSelection> mucs ) { clazz = mucs; }
//		public Class<? extends MUCandidateSelection> getEntryClass() { return clazz; }
//	}
	
	
	public enum CandidateSelectionRegistry {
		ServerMultiStrategy (am.extension.multiUserFeedback.selection.MultiStrategyCandidateSelection.class),
		ClientCandidateSelection (am.extension.multiUserFeedback.selection.ClientCandidateSelection.class),
		ServerCandidateSelection (am.extension.multiUserFeedback.selection.ServerCandidateSelection.class),
		MultiStrategyRanking (am.extension.userfeedback.clustering.disagreement.MultiStrategyRanking.class),
		MaxInformationRanking (am.extension.userfeedback.clustering.disagreement.MaxInformationRanking.class   ),
		DisagreementRank ( am.extension.userfeedback.clustering.disagreement.DisagreementRanking.class );
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends CandidateSelection> clazz;
		
		CandidateSelectionRegistry( Class<? extends CandidateSelection> cs ) { clazz = cs; }
		public Class<? extends CandidateSelection> getEntryClass() { return clazz; }
	}
	
	public enum CSEvaluationRegistry {
		MultiplexCSE (am.extension.userfeedback.evaluation.MultiplexCandidateSelectionEvaluation.class),
		RankingAccuracy (am.extension.userfeedback.evaluation.RankingAccuracy.class),
		PrecisionRecallEval ( am.extension.userfeedback.common.CandidateMappingEvaluation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends CandidateSelectionEvaluation> clazz;
		
		CSEvaluationRegistry( Class<? extends CandidateSelectionEvaluation> cs ) { clazz = cs; }
		public Class<? extends CandidateSelectionEvaluation> getEntryClass() { return clazz; }
	}
	
	public enum UserValidationRegistry {
		PESimulatedClient (am.extension.multiUserFeedback.validation.ProbabilisticErrorAutomaticValidation.class),
		ClientFeedbackValidation (am.extension.multiUserFeedback.validation.ClientFeedbackValidation.class),
		AutomaticReference ( am.extension.userfeedback.common.AutomaticValidation.class ),
		Manual ( am.extension.userfeedback.common.ManualUserValidation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends UserFeedback> clazz;
		
		UserValidationRegistry( Class<? extends UserFeedback> cs ) { clazz = cs; }
		public Class<? extends UserFeedback> getEntryClass() { return clazz; }
	}
	
	public enum FeedbackAggregationRegistry {
		ServerFeedbackAggregation (am.extension.multiUserFeedback.storage.ServerFeedbackAggregation.class);
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends FeedbackAgregation> clazz;
		
		FeedbackAggregationRegistry( Class<? extends FeedbackAgregation> fa ) { clazz = fa; }
		public Class<? extends FeedbackAgregation> getEntryClass() { return clazz; }
	}
	
	
	public enum FeedbackPropagationRegistry {
		ServerFeedbackPropagation (am.extension.multiUserFeedback.propagation.ServerFeedbackPropagation.class),
		ClientFeedbackPropagation (am.extension.userfeedback.propagation.MLFeedbackPropagation.class),
		SUFeedbackPropagation (am.extension.userfeedback.propagation.SUFeedbcackPropagation.class),
		ClusterBoost ( am.extension.userfeedback.clustering.disagreement.ClusterBoostPropagation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends FeedbackPropagation> clazz;
		
		FeedbackPropagationRegistry( Class<? extends FeedbackPropagation> cs ) { clazz = cs; }
		public Class<? extends FeedbackPropagation> getEntryClass() { return clazz; }
	}
	
	public enum PropagationEvaluationRegistry {
		ServerPropagationEvaluation (am.extension.multiUserFeedback.evaluation.ServerFeedbackEvaluation.class),
		SelectionRankingEvaluation ( am.extension.userfeedback.common.SelectionRankingEvaluation.class ),
		SMatrixDeltaEvaluetion (am.extension.userfeedback.clustering.disagreement.SMatrixDeltaEvaluetion.class),
		DeltaFromRef ( am.extension.userfeedback.clustering.disagreement.DeltaFromReferenceEvaluation.class ),
		ClusterBoost ( am.extension.userfeedback.clustering.disagreement.ClusterBoostEvaluation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends PropagationEvaluation> clazz;
		
		PropagationEvaluationRegistry( Class<? extends PropagationEvaluation> cs ) { clazz = cs; }
		public Class<? extends PropagationEvaluation> getEntryClass() { return clazz; }
	}
	
	public enum SaveFeedbackRegistry {
		MultiUserSaveFeedback (am.extension.userfeedback.MLutility.MultiUserSaveFeedback.class);
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends SaveFeedback> clazz;
		
		SaveFeedbackRegistry( Class<? extends SaveFeedback> cs ) { clazz = cs; }
		public Class<? extends SaveFeedback> getEntryClass() { return clazz; }
	}
	
	public enum UFLStatisticRegistry {
		ServerStatistics (am.extension.userfeedback.ServerStatistics.class);
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends UFLStatistics> clazz;
		
		UFLStatisticRegistry( Class<? extends UFLStatistics> us ) { clazz = us; }
		public Class<? extends UFLStatistics> getEntryClass() { return clazz; }
	}
}
