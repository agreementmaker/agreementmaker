package am.va.graph;

import am.app.Core;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Ontology;

public class VAMatchingTask {
	private String taskName;
	private Ontology source;
	private Ontology target;
	private MatchingTask task;
	private SimilarityMatrix classMatrix;
	private SimilarityMatrix propertyMatrix;

	public static int totalDisplayNum = 0;

	public VAMatchingTask(int num) {
		if (totalDisplayNum == 0)
			totalDisplayNum = Core.getInstance().getMatchingTasks().size() - 1;
		task = Core.getInstance().getMatchingTasks().get(num);
		taskName = task.matchingAlgorithm.getName();
		source = task.matcherResult.getSourceOntology();
		target = task.matcherResult.getTargetOntology();
		classMatrix = task.matcherResult.getClassesMatrix();
		propertyMatrix = task.matcherResult.getPropertiesMatrix();
	}

	public String getTaskName() {
		return taskName;
	}

	public Ontology getSource() {
		return source;
	}

	public Ontology getTarget() {
		return target;
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
