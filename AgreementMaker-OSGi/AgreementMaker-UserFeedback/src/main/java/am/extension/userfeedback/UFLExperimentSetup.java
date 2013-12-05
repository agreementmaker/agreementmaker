package am.extension.userfeedback;

import am.extension.userfeedback.UFLRegistry.CSEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.LoopInizializationRegistry;
import am.extension.userfeedback.UFLRegistry.PropagationEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.UserValidationRegistry;
import am.extension.userfeedback.UFLRegistry.SaveFeedbackRegistry;

public class UFLExperimentSetup {

	public InitialMatcherRegistry			im;
	public LoopInizializationRegistry		fli;
	public CandidateSelectionRegistry		cs;
	public CSEvaluationRegistry				cse;
	public UserValidationRegistry			uv;
	public FeedbackPropagationRegistry		fp;
	public PropagationEvaluationRegistry	pe;
	public SaveFeedbackRegistry				sf;
	
}
