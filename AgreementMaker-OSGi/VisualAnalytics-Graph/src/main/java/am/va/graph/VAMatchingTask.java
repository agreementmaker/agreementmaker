package am.va.graph;

import am.app.Core;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Ontology;

public class VAMatchingTask {
	private String taskName;
	private Ontology taskSource;
	private Ontology taskTarget;
	private MatchingTask task;
	private SimilarityMatrix classMatrix;
	private SimilarityMatrix propertyMatrix;

	public VAMatchingTask(int num) {
		task = Core.getInstance().getMatchingTasks().get(num);
		taskName = task.matchingAlgorithm.getName();
		taskSource = task.matcherResult.getSourceOntology();
		taskTarget = task.matcherResult.getTargetOntology();
		classMatrix = task.matcherResult.getClassesMatrix();
		propertyMatrix = task.matcherResult.getPropertiesMatrix();
	}

	public String getTaskName() {
		return taskName;
	}

	public Ontology getTaskSource() {
		return taskSource;
	}

	public Ontology getTaskTarget() {
		return taskTarget;
	}

	public MatchingTask getTask() {
		return task;
	}

	public SimilarityMatrix getClassMatrix() {
		return classMatrix;
	}

	public SimilarityMatrix getPropertyMatrix() {
		return propertyMatrix;
	}
	
	
}
