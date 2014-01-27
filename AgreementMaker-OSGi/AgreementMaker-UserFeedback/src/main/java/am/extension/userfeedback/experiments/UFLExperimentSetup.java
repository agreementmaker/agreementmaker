package am.extension.userfeedback.experiments;

import java.io.Serializable;
import java.lang.reflect.Field;

import am.extension.userfeedback.UFLRegistry.CSEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.ExperimentRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackAggregationRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.LoopInizializationRegistry;
import am.extension.userfeedback.UFLRegistry.PropagationEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.SaveFeedbackRegistry;
import am.extension.userfeedback.UFLRegistry.UserValidationRegistry;

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
	public FeedbackAggregationRegistry		fa;
	
	/**
	 * Experiment-wide parameters.
	 */
	public UFLExperimentParameters			parameters;
	
	public UFLExperimentSetup() {}
	
	/** Cloning constructor */
	public UFLExperimentSetup(UFLExperimentSetup s) {
		
		for( Field f : getClass().getFields() ) {
			try {
				f.set(this, f.get(s));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		this.parameters = s.parameters.clone();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new UFLExperimentSetup(this);
	}
}
