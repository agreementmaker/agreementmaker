package am.extension.userfeedback;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.UFLControlLogic;
import am.extension.userfeedback.ui.UFLControlGUI;

public abstract class UFLExperiment {

	public UFLExperimentSetup				setup;  
	
	public ExecutionSemantics 										initialMatcher;
	public CandidateSelection 										candidateSelection;
	public CandidateSelectionEvaluation 							csEvaluation;
	public UserFeedback												userFeedback;
	public FeedbackPropagation<UFLExperiment>				feedbackPropagation;
	public PropagationEvaluation									propagationEvaluation;
	public UFLControlGUI											gui;
	
	/**
	 * These mappings were validated by the user as being CORRECT.
	 */
    public Alignment<Mapping>				correctMappings;
    
    /**
     * These mappings were validated by the user as being INCORRECT.
     */
    public Alignment<Mapping>				incorrectMappings;
    
    private int iterationNumber = 0;


	public abstract Ontology 			getSourceOntology();
	public abstract Ontology 			getTargetOntology();
	public abstract Alignment<Mapping> 	getReferenceAlignment();
	public abstract Alignment<Mapping>  getFinalAlignment();
	public abstract void				info(String line);   // FIXME: Change this, or get rid of it. Or learn how to use log4j.
	public abstract UFLControlLogic		getControlLogic();
	
	public abstract boolean 			experimentHasCompleted();  // return true if the experiment is done, false otherwise.
	
	public int getIterationNumber() { return iterationNumber; }
	
	public void	newIteration() {
		if( userFeedback.getUserFeedback() == Validation.CORRECT ) {
			if( correctMappings == null ) {
				correctMappings = new Alignment<Mapping>(getSourceOntology().getID(), getTargetOntology().getID());
			}
			correctMappings.add( userFeedback.getCandidateMapping() );
		} else if( userFeedback.getUserFeedback() == Validation.INCORRECT ) {
			if( incorrectMappings == null ) {
				incorrectMappings = new Alignment<Mapping>(getSourceOntology().getID(), getTargetOntology().getID());
			}
			incorrectMappings.add( userFeedback.getCandidateMapping() );
		}		
		
		setIterationNumber(getIterationNumber() + 1); 
	}
	
	public void setGUI(UFLControlGUI gui) { this.gui = gui; }
	public void setIterationNumber(int iterationNumber) {
		this.iterationNumber = iterationNumber;
	}
}
