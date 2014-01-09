package am.extension.userfeedback.experiments;

import java.io.Serializable;

import am.extension.userfeedback.UFLRegistry.CSEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.ExperimentRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.LoopInizializationRegistry;
import am.extension.userfeedback.UFLRegistry.PropagationEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.SaveFeedbackRegistry;
import am.extension.userfeedback.UFLRegistry.UserValidationRegistry;
import am.extension.userfeedback.preset.MatchingTaskPreset;

public class UFLExperimentSetup implements Serializable {

	private static final long serialVersionUID = 2006678734079546244L;

	/** The experiment class that we are using. */
	public ExperimentRegistry				exp;
	
	public InitialMatcherRegistry			im;
	public LoopInizializationRegistry		fli;
	public CandidateSelectionRegistry		cs;
	public CSEvaluationRegistry				cse;
	public UserValidationRegistry			uv;
	public FeedbackPropagationRegistry		fp;
	public PropagationEvaluationRegistry	pe;
	public SaveFeedbackRegistry				sf;
	
	/**
	 * Experiment-wide parameters.
	 */
	public UFLExperimentParameters			parameters;
}
