package am.app.mappingEngine;

import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class MatcherResult {
	
	private boolean modifiedbyUser;
	private long executionTime;
	private SimilarityMatrix classesMatrix;
	private SimilarityMatrix propMatrix;
	private Ontology sourceOntology;
	private Ontology targetOntology;

	private MatchingTask task;
	
	private int id;
	
	public MatcherResult(MatchingTask task) {
		this.task = task;
	}
	
	/**
	 * @deprecated Use {@link #MatcherResult(MatchingTask)} instead.
	 */
	@Deprecated
	public MatcherResult(AbstractMatcher a) {	
		modifiedbyUser=a.isModifiedByUser();
		executionTime=a.getExecutionTime();
		classesMatrix=a.getClassesMatrix();
		propMatrix=a.getPropertiesMatrix();
	}

	public MatchingTask getMatchingTask() { return task; }

	public boolean isModifiedByUser() {return modifiedbyUser;}


	public long getExecutionTime() {return executionTime;}

	public SimilarityMatrix getClassesMatrix() {return classesMatrix;}

	public SimilarityMatrix getPropertiesMatrix() {return propMatrix;}

	public Ontology getSourceOntology() { return sourceOntology; }
	public Ontology getTargetOntology() { return targetOntology; }

	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public void removeMapping(Node source, Node target) {
		// original function is in AbstractMatcher
		throw new RuntimeException ("Not implemented yet!");
	}
}
