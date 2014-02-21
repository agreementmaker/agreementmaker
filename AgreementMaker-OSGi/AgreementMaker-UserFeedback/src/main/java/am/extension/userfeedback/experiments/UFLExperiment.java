package am.extension.userfeedback.experiments;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.extension.multiUserFeedback.storage.FeedbackAgregation;
import am.extension.multiUserFeedback.validation.ProbabilisticErrorAutomaticValidation;
import am.extension.userfeedback.ExecutionSemantics;
import am.extension.userfeedback.SaveFeedback;
import am.extension.userfeedback.UFLStatistics;
import am.extension.userfeedback.UserFeedback;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.common.ServerFeedbackEvaluationData;
import am.extension.userfeedback.evaluation.CandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.inizialization.FeedbackLoopInizialization;
import am.extension.userfeedback.logic.UFLControlLogic;
import am.extension.userfeedback.propagation.FeedbackPropagation;
import am.extension.userfeedback.selection.CandidateSelection;
import am.extension.userfeedback.ui.UFLProgressDisplay;

public abstract class UFLExperiment {

	private static final Logger LOG = LogManager.getLogger(UFLExperiment.class);
	
	public final UFLExperimentSetup							setup;  
	
	public ExecutionSemantics 								initialMatcher;
	public FeedbackLoopInizialization<UFLExperiment>        dataInizialization;
	public CandidateSelection<UFLExperiment> 				candidateSelection;
	public CandidateSelectionEvaluation 					csEvaluation;
	public UserFeedback										userFeedback;
	public FeedbackPropagation<UFLExperiment>				feedbackPropagation;
	public PropagationEvaluation<UFLExperiment>				propagationEvaluation;
	public UFLProgressDisplay								gui;
	public SaveFeedback< UFLExperiment>						saveFeedback;
	public FeedbackAgregation<UFLExperiment>				feedbackAggregation;
	public UFLStatistics<UFLExperiment>						uflStatistics; 
	
	/**
	 * Keep count of how many incorrect validations were generated for a
	 * specific mapping.
	 * 
	 * @see {@link ProbabilisticErrorAutomaticValidation#validate(UFLExperiment)}
	 */
	public Map<Mapping,Integer> incorrectFeedbackCount = new HashMap<>();
	
	protected Ontology sourceOntology;	
	protected Ontology targetOntology;
	protected Alignment<Mapping> referenceAlignment = null;
	
	/**
	 * A shared object store, that is used to keep objects. NOTE: This object is
	 * needed because of the way the UFLControlLogic has evolved. The
	 * UFLControlLogics we implemented did not allow UFL objects to persist
	 * beyond one iteration. On each iteration, new objects are instantitated.
	 * So we moved a lot of the data structures we wanted to persist into the
	 * UFLExperiment subclasses. This object is an attempt to simplify that
	 * task. Instead of adding a new field every time we want to save a new data
	 * structure, we will just put the object in the object store, and retrieve
	 * it later. There should be a better way to do this, or ideally move to an
	 * OSGi-based model.
	 */
	private Map<String,Object> sharedObjectStore;
	
	/**
	 * These mappings were validated by the user as being CORRECT.
	 */
    public Alignment<Mapping>				correctMappings;
    
    /**
     * These mappings were validated by the user as being INCORRECT.
     */
    public Alignment<Mapping>				incorrectMappings;
    
    private int iterationNumber = 0;

    public ServerFeedbackEvaluationData feedbackEvaluationData;
    
    public UFLExperiment(UFLExperimentSetup setup) {
		this.setup = setup;
		
		String log = setup.parameters.getParameter(Parameter.LOGFILE);
		if( log == null ) {
			log = "settings/tmp/uflLog." + System.currentTimeMillis() + ".txt";
			setup.parameters.setParameter(Parameter.LOGFILE, log);
			LOG.error("The LOGFILE parameter has not been set for this experiment. Log file is defaulting to \""+log+"\".");
		}
		
		sharedObjectStore = new HashMap<>();
		
		int numIterations = setup.parameters.getIntParameter(Parameter.NUM_ITERATIONS);
		feedbackEvaluationData = new ServerFeedbackEvaluationData(numIterations);
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
	
	public int getIterationNumber() 
	{ 
		return iterationNumber; 
	}
	
	public void	newIteration() {
		if( userFeedback.getUserFeedback() == Validation.CORRECT ) {
			if( correctMappings == null ) {
				correctMappings = new Alignment<Mapping>(getSourceOntology().getID(), getTargetOntology().getID());
			}
			if (!correctMappings.contains(userFeedback.getCandidateMapping()))
				correctMappings.add( userFeedback.getCandidateMapping() );
		} else if( userFeedback.getUserFeedback() == Validation.INCORRECT ) {
			if( incorrectMappings == null ) {
				incorrectMappings = new Alignment<Mapping>(getSourceOntology().getID(), getTargetOntology().getID());
			}
			if (!incorrectMappings.contains(userFeedback.getCandidateMapping()))
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
	

	/**
	 * @param key The key for the object.
	 * @return An object from the shared object store.
	 */
	public Object getSharedObject(String key) {
		return sharedObjectStore.get(key);
	}
	
	/**
	 * @param key The key for the object.
	 * @param object The object we want to store in the shared object store.
	 */
	public void setSharedObject(String key, Object object) {
		sharedObjectStore.put(key, object);
	}
	
	/**
	 * @return A list of all the mappings that have been validated by the user.
	 */
	public Alignment<Mapping> getValidatedMappings() {
		Alignment<Mapping> a = new Alignment<>(getSourceOntology().getID(),getTargetOntology().getID());
		if( correctMappings != null ) a.addAll(correctMappings);
		if( incorrectMappings != null ) a.addAll(incorrectMappings);
		return a;
	}
}
