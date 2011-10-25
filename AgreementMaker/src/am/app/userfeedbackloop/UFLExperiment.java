package am.app.userfeedbackloop;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.app.userfeedbackloop.UserFeedback.Validation;
import am.app.userfeedbackloop.common.ValidatedMapping;
import am.app.userfeedbackloop.ui.UFLControlGUI;

public abstract class UFLExperiment {

	public ExecutionSemantics 				initialMatcher;
	public CandidateSelection 				candidateSelection;
    public CandidateSelectionEvaluation 	csEvaluation;
    public UserFeedback						userFeedback;
    public FeedbackPropagation				feedbackPropagation;
    public PropagationEvaluation			propagationEvaluation;
    public UFLControlGUI					gui;
    
    public Alignment<Mapping>				correctMappings;
    public Alignment<Mapping>				incorrectMappings;
    
    private int iterationNumber = 0;
    
	public abstract Ontology 			getSourceOntology();
	public abstract Ontology 			getTargetOntology();
	public abstract Alignment<Mapping> 	getReferenceAlignment();
	public abstract Alignment<Mapping>  getFinalAlignment();
	public abstract void				info(String line);   // FIXME: Change this, or get rid of it. Or learn how to use log4j.
	
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
		
		iterationNumber++; 
	}
	
	public void setGUI(UFLControlGUI gui) { this.gui = gui; }
}
