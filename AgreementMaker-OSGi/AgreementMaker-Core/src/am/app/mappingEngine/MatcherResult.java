package am.app.mappingEngine;

import java.io.Serializable;

import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class MatcherResult implements Serializable {
	
	private static final long serialVersionUID = -3777252152922774451L;

	private boolean modifiedbyUser = false;
	
	private long executionTime = 0;
	
	
	private SimilarityMatrix classesMatrix;
	
	private SimilarityMatrix propMatrix;
	
	private transient Ontology sourceOntology;
	
	private transient Ontology targetOntology;

	private transient MatchingTask task;
	
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
		sourceOntology = a.getSourceOntology();
		targetOntology = a.getTargetOntology();
	}

	public MatchingTask getMatchingTask() { return task; }

	public boolean isModifiedByUser() {return modifiedbyUser;}


	public long getExecutionTime() {return executionTime;}

	public void setClassesMatrix(SimilarityMatrix mtx) {
		this.classesMatrix = mtx;
	}
	public SimilarityMatrix getClassesMatrix() {return classesMatrix;}

	public void setPropertiesMatrix(SimilarityMatrix mtx) {
		this.propMatrix = mtx;
	}
	public SimilarityMatrix getPropertiesMatrix() {return propMatrix;}

	public Ontology getSourceOntology() { return sourceOntology; }
	public void setSourceOntology(Ontology ont) { this.sourceOntology = ont; }
	public Ontology getTargetOntology() { return targetOntology; }
	public void setTargetOntology(Ontology ont) { this.targetOntology = ont; }

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
