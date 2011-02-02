package am.app.userfeedbackloop;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;

public abstract class UFLExperiment {

	public ExecutionSemantics 				initialMatcher;
	public CandidateSelection 				candidateSelection;
    public CandidateSelectionEvaluation 	csEvaluation;
    public UserFeedback						userFeedback;
    public FeedbackPropagation				feedbackPropagation;
    public PropagationEvaluation			propagationEvaluation;
    
	public abstract Ontology 			getSourceOntology();
	public abstract Ontology 			getTargetOntology();
	public abstract Alignment<Mapping> 	getReferenceAlignment();
	
	public abstract boolean 			isDone();  // return true if the experiment is done, false otherwise.
	public abstract void				newIteration();
}
