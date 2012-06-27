package am.app.mappingEngine;

import java.awt.Color;

import am.app.Core;
import am.app.ontology.Ontology;

public class MatcherResult {
	
	private boolean visible;
	private boolean modifiedbyUser;
	private long executionTime;
	private Color color;
	private SimilarityMatrix classesMatrix;
	private SimilarityMatrix propMatrix;
	private Ontology sourceOntology;
	private Ontology targetOntology;

	private MatchingTask task;
	
	private int id;
	
	public MatcherResult(MatchingTask task) {
		this.task = task;
	}
	
	@Deprecated
	public MatcherResult(AbstractMatcher a) {	
		visible=a.isShown();
		modifiedbyUser=a.isModifiedByUser();
		executionTime=a.getExecutionTime();
		color=a.getColor();
		classesMatrix=a.getClassesMatrix();
		propMatrix=a.getPropertiesMatrix();
	}

	public MatchingTask getMatchingTask() { return task; }

	public boolean isModifiedByUser() {return modifiedbyUser;}


	public long getExecutionTime() {return executionTime;}

	@Deprecated
	public Color getColor() {return color;}

	@Deprecated
	public void setColor(Color c) {
		color=c;
		MatchingTaskChangeEvent mce = new MatchingTaskChangeEvent(this, MatchingTaskChangeEvent.EventType.MATCHER_COLOR_CHANGED);
		Core.getInstance().fireEvent(mce);
	}

	@Deprecated
	public boolean isShown() {return visible;}
	
	@Deprecated
	public void setShown(boolean b) {visible=b;}

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
    
}
