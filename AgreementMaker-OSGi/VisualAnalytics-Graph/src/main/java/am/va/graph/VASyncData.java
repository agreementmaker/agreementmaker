package am.va.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class VASyncData {
	/**
	 * Get the root Node, called by getRootVAData
	 * 
	 * @param ontologyType
	 * @return
	 */
	private static Node getRootNode(VAVariables.ontologyType ontologyType) {
		Node rootNode = null;
		List<MatchingTask> matchingTask = Core.getInstance().getMatchingTasks();
		MatchingTask currentTask = matchingTask.get(0); // for now only select
														// the first task
		if (ontologyType == VAVariables.ontologyType.Source) {
			Ontology sourceOntology = currentTask.matcherResult
					.getSourceOntology();
			rootNode = sourceOntology.getClassesRoot();
		} else {
			Ontology targetOntology = currentTask.matcherResult
					.getTargetOntology();
			rootNode = targetOntology.getClassesRoot();
		}

		return rootNode;
	}

	/**
	 * Get VAData for Root node
	 * 
	 * @param ontologyType
	 * @return
	 */
	public static VAData getRootVAData(VAVariables.ontologyType ontologyType) {
		Node sNode = null, tNode = null;
		double Similarity = 0.0;
		if (ontologyType == VAVariables.ontologyType.Source) { // pie chart for
																// source
																// ontology
			sNode = getRootNode(VAVariables.ontologyType.Source);

		} else {
			sNode = getRootNode(VAVariables.ontologyType.Target);
		}
		tNode = null;
		return new VAData(sNode, tNode, Similarity);
	}

	/**
	 * Generate one child's matching VAData, called by getChildrenData
	 * 
	 * @param n
	 * @return
	 */
	private static VAData getMatchingVAData(Node n,
			VAVariables.ontologyType ontologyType) {
		Node matchingNode = null;
		double sim = 0.00;
		MatchingTask matchingTask = Core.getInstance()
				.getMatchingTasksWithoutUserManualMatcher().get(0);
		SimilarityMatrix smClass = matchingTask.matcherResult
				.getClassesMatrix();
		Mapping map[] = null;
		if (ontologyType == VAVariables.ontologyType.Source) // input is source,
																// find target
			map = smClass.getRowMaxValues(n.getIndex(), 1);
		else
			map = smClass.getColMaxValues(n.getIndex(), 1); // input is target,
															// find source
		if (map != null) {
			if (ontologyType == VAVariables.ontologyType.Source)
				matchingNode = map[0].getEntity2();
			else
				matchingNode = map[0].getEntity1();
			// sim = map[0].getSimilarity();
			sim = Math.random(); // only for testing
		} else {
			System.out.println("mapping data is null ???");
		}
		return new VAData(n, matchingNode, sim);
	}

	/**
	 * Get children nodes of current root node Sorted by similarity
	 * 
	 * @param rootNodeData
	 * @return
	 */
	public static ArrayList<VAData> getChildrenData(VAData rootNodeData,
			VAVariables.ontologyType ontologyType) {
		ArrayList<VAData> res = new ArrayList<VAData>();
		Node rootNode = rootNodeData.sourceNode;
		for (Node n : rootNode.getChildren()) {
			// get target node info which best matches this node
			VAData newChildData = VASyncData.getMatchingVAData(n, ontologyType);
			res.add(newChildData);
		}
		Collections.sort(res);
		return res;
	}
}
