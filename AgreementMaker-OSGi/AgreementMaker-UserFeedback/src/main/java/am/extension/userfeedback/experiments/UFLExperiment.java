package am.extension.userfeedback.experiments;

import org.apache.log4j.Logger;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.extension.multiUserFeedback.storage.FeedbackAgregation;
import am.extension.userfeedback.ExecutionSemantics;
import am.extension.userfeedback.SaveFeedback;
import am.extension.userfeedback.UserFeedback;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.evaluation.CandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.inizialization.FeedbackLoopInizialization;
import am.extension.userfeedback.logic.UFLControlLogic;
import am.extension.userfeedback.propagation.FeedbackPropagation;
import am.extension.userfeedback.selection.CandidateSelection;
import am.extension.userfeedback.ui.UFLProgressDisplay;

public abstract class UFLExperiment {

	private static final Logger LOG = Logger.getLogger(UFLExperiment.class);
	
	public final UFLExperimentSetup							setup;  
	
	public ExecutionSemantics 								initialMatcher;
	public FeedbackLoopInizialization<UFLExperiment>        dataInizialization;
	public CandidateSelection<UFLExperiment> 				candidateSelection;
	public CandidateSelectionEvaluation 					csEvaluation;
	public UserFeedback										userFeedback;
	public FeedbackPropagation< UFLExperiment>				feedbackPropagation;
	public PropagationEvaluation< UFLExperiment>			propagationEvaluation;
	public UFLProgressDisplay								gui;
	public SaveFeedback< UFLExperiment>						saveFeedback;
	public 	FeedbackAgregation<UFLExperiment>					feedbackAggregation;

	protected Ontology sourceOntology;	
	protected Ontology targetOntology;
	protected Alignment<Mapping> referenceAlignment = null;
	
	/**
	 * These mappings were validated by the user as being CORRECT.
	 */
    public Alignment<Mapping>				correctMappings;
    
    /**
     * These mappings were validated by the user as being INCORRECT.
     */
    public Alignment<Mapping>				incorrectMappings;
    
    private int iterationNumber = 0;

    public UFLExperiment(UFLExperimentSetup setup) {
		this.setup = setup;
		
		String log = setup.parameters.getParameter(Parameter.LOGFILE);
		if( log == null ) {
			log = "settings/tmp/uflLog." + System.currentTimeMillis() + ".txt";
			setup.parameters.setParameter(Parameter.LOGFILE, log);
			LOG.error("The LOGFILE parameter has not been set for this experiment. Log file is defaulting to \""+log+"\".");
		}
	}
    
	public Ontology getSourceOntology()             { return sourceOntology; }
	public void     setSourceOntology(Ontology ont) { this.sourceOntology = ont; }
	public Ontology getTargetOntology()             { return targetOntology; }
	public void     setTargetOntology(Ontology ont) { this.targetOntology = ont; }
	
	public Alignment<Mapping> getReferenceAlignment() { return referenceAlignment; }
	public void setReferenceAlignment(Alignment<Mapping> ref) { this.referenceAlignment = ref; }
	
	
	public abstract Alignment<Mapping>  getFinalAlignment();
	public abstract void				info(String line);   // FIXME: Change this, or get rid of it. Or learn how to use log4j.
	
	public abstract UFLControlLogic<? extends UFLExperiment>	getControlLogic();
	
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
	
	public void setGUI(UFLProgressDisplay gui) { this.gui = gui; }
	public void setIterationNumber(int iterationNumber) {
		this.iterationNumber = iterationNumber;
	}
	
	/**
	 * @return A human-readable description of the experiment that is displayed
	 *         to the user.
	 */
	public abstract String getDescription(); 
	

}
