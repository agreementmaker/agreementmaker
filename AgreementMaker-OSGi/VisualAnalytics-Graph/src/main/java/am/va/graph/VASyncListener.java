package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.MatchingTaskChangeEvent.EventType;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class VASyncListener implements MatcherChangeListener {

	public VASyncListener() {
		Core.getInstance().addMatcherChangeListener(this);
	}

	@Override
	public void matcherChanged(MatchingTaskChangeEvent e) {

		// Build root panel for source ontology
		if (!(e.getTask().matchingAlgorithm instanceof UserManualMatcher)) {
			VAGroup rootGroup = new VAGroup();
			rootGroup.setParent(0);
			rootGroup
					.setRootNode(getRootVAData(VAVariables.ontologyType.Source));
			rootGroup.setDataList(getChildrenData(rootGroup.getRootNode()));
			TEST(rootGroup);
		}
	}

	private void TEST(VAGroup rootGroup) {
		String rootNodeName = rootGroup.getRootNode().getSourceNode()
				.getLocalName();
		System.out.println(rootNodeName);
		ArrayList<VAData> vaData = rootGroup.getLstVAData();
		for (VAData d : vaData) {
			System.out.println(d.getSourceNode().getLocalName() + ","
					+ d.getTargetNode().getLocalName() + ","
					+ d.getSimilarity());
		}
		HashMap<String, Integer> slots = rootGroup.getSlots();
		for (String s : slots.keySet()) {
			System.out.println(s + ":" + slots.get(s));
		}
	}

	/**
	 * Get the children list of current root node
	 * 
	 * @param rootNodeData
	 * @return
	 */
	private ArrayList<VAData> getChildrenData(VAData rootNodeData) {
		ArrayList<VAData> res = new ArrayList<VAData>();
		Node rootNode = rootNodeData.sourceNode;
		for (Node n : rootNode.getChildren()) {
			// get target node info which best matches this node
			VAData newChildData = getMatchingVAData(n);
			res.add(newChildData);
		}
		return res;
	}

	/**
	 * Build children matching VAData
	 * 
	 * @param n
	 * @return
	 */
	private VAData getMatchingVAData(Node n) {
		Node matchingNode = null;
		double sim = 0.00;
		MatchingTask matchingTask = Core.getInstance()
				.getMatchingTasksWithoutUserManualMatcher().get(0);
		SimilarityMatrix smClass = matchingTask.matcherResult
				.getClassesMatrix();
		Mapping map[] = smClass.getRowMaxValues(n.getIndex(), 1); // bug
		if (map != null) {
			matchingNode = map[0].getEntity2();
			sim = map[0].getSimilarity();
		}
		return new VAData(n, matchingNode, sim);
	}

	/**
	 * Get VAData for Root node
	 * 
	 * @param ontologyType
	 * @return
	 */
	private VAData getRootVAData(VAVariables.ontologyType ontologyType) {
		Node sNode = null, tNode = null;
		double Similarity = 0.0;
		if (ontologyType == VAVariables.ontologyType.Source) { // pie chart for
																// source
																// ontology
			sNode = getRootNode(VAVariables.ontologyType.Source);
			tNode = null;
		}
		return new VAData(sNode, tNode, Similarity);
	}

	/**
	 * Get the root Node
	 * 
	 * @param ontologyType
	 * @return
	 */
	private Node getRootNode(VAVariables.ontologyType ontologyType) {
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

}
