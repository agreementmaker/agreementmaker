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
		SingleUser( am.extension.userfeedback.experiments.SUExperiment.class),
		ClientExperiment ( am.extension.userfeedback.experiments.MLFExperiment.class),
		ServerExperiment ( am.extension.multiUserFeedback.experiment.MUExperiment.class ),
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
	
	public enum LoopInizializationRegistry {
		ClientDataInizialization (am.extension.userfeedback.inizialization.RestfulDataInizialization.class),
		ServerDataInizialization (am.extension.multiUserFeedback.initialization.MUDataInizialization.class),
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
		ServerMultiStrategy (am.extension.multiUserFeedback.selection.ServerMultiStrategyCandidateSelection.class),
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
		PrecisionRecallEval ( am.extension.userfeedback.common.PrecisionRecallPlot.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends CandidateSelectionEvaluation> clazz;
		
		CSEvaluationRegistry( Class<? extends CandidateSelectionEvaluation> cs ) { clazz = cs; }
		public Class<? extends CandidateSelectionEvaluation> getEntryClass() { return clazz; }
	}
	
	public enum UserValidationRegistry {
		FakeClient (am.extension.multiUserFeedback.validation.BMAutomaticValidation.class),
		ClientFeedbackValidation (am.extension.multiUserFeedback.validation.ClientFeedbackValidation.class),
		AutomaticReference ( am.extension.userfeedback.common.AutomaticUserValidation.class ),
		Manual ( am.extension.userfeedback.common.ManualUserValidation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends UserFeedback> clazz;
		
		UserValidationRegistry( Class<? extends UserFeedback> cs ) { clazz = cs; }
		public Class<? extends UserFeedback> getEntryClass() { return clazz; }
	}
	
	public enum FeedbackAggregationRegistry {
		ServerFeedbackAggregation (am.extension.multiUserFeedback.storage.ServerFeedbackStorage.class);
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends FeedbackAgregation> clazz;
		
		FeedbackAggregationRegistry( Class<? extends FeedbackAgregation> fa ) { clazz = fa; }
		public Class<? extends FeedbackAgregation> getEntryClass() { return clazz; }
	}
	
	
	public enum FeedbackPropagationRegistry {
		SUFeedbackPropagation (am.extension.userfeedback.propagation.SUFeedbcackPropagation.class),
		ClientFeedbackPropagation (am.extension.userfeedback.propagation.MLFeedbackPropagation.class),
		ServerFeedbackPropagation (am.extension.multiUserFeedback.propagation.ServerFeedbackPropagation.class),
		ClusterBoost ( am.extension.userfeedback.clustering.disagreement.ClusterBoostPropagation.class );
		
		/* *********************** DO NOT EDIT BELOW THIS LINE **************************** */
		Class<? extends FeedbackPropagation> clazz;
		
		FeedbackPropagationRegistry( Class<? extends FeedbackPropagation> cs ) { clazz = cs; }
		public Class<? extends FeedbackPropagation> getEntryClass() { return clazz; }
	}
	
	public enum PropagationEvaluationRegistry {
		SelectionRankingEvaluation ( am.extension.userfeedback.common.SelectionRankingEvaluation.class ),
		SMatrixDeltaEvaluetion (am.extension.userfeedback.clustering.disagreement.SMatrixDeltaEvaluetion.class),
		ServerPropagationEvaluation (am.extension.multiUserFeedback.evaluation.ServerFeedbackEvaluation.class),
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
}
