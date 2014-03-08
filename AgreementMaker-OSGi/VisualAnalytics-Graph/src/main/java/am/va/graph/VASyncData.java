package am.va.graph;

import java.util.ArrayList;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class VASyncData {

	private static int totalDisplayNum = 0;
	private static int currentDisplayNum = 1;

	/**
	 * Get a node's parent node
	 * 
	 * @param n
	 * @return
	 */
	private static Node getParentNode(Node n) {
		if (n.getParents().size() > 0)
			return n.getParents().get(0);
		else
			return null;
	}

	public static int getTotalDisplayNum() {
		return totalDisplayNum;
	}

	public static void setTotalDisplayNum(int totalDisplayNum) {
		VASyncData.totalDisplayNum = totalDisplayNum;
	}

	public static int getCurrentDisplayNum() {
		return currentDisplayNum;
	}

	public static void setCurrentDisplayNum(int currentDisplayNum) {
		VASyncData.currentDisplayNum = currentDisplayNum;
	}

	/**
	 * Get a VAData's parent data
	 * 
	 * @param v
	 * @return
	 */
	public static VAData getParentVAData(VAData v) {
		Node sNode = null, tNode = null;
		double Similarity = 0.0;
		sNode = getParentNode(v.getSourceNode());
		if (sNode != null) {
			// for now we don't care about the target node and sim value
		} else {
			System.out.println("- Parent node empty ?!!");
		}
		return new VAData(sNode, tNode, Similarity);
	}

	/**
	 * Get the root Node, called by getRootVAData
	 * 
	 * @param ontologyType
	 * @return
	 */
	private static Node getRootNode(VAVariables.ontologyType ontologyType) {
		Node rootNode = null;
		List<MatchingTask> matchingTask = Core.getInstance().getMatchingTasks();
		
		//Set total number of difference matchings
		totalDisplayNum = matchingTask.size() - 1;
		
		MatchingTask currentTask = null;
		if (currentDisplayNum <= matchingTask.size())
			currentTask = matchingTask.get(currentDisplayNum);
		else {
			// Error!
			return null;
		}

		if (ontologyType == VAVariables.ontologyType.Source) {

			// Testing...
			String alg = currentTask.matchingAlgorithm.getName();
			System.out.println("algorithm: " + alg);
			System.out.println("report:" + currentTask.getMatchingReport());

			Ontology sourceOntology = currentTask.matcherResult
					.getSourceOntology();
			if (VAPanelLogic.getCurrentNodeType() == VAVariables.nodeType.Class)
				rootNode = sourceOntology.getClassesRoot();
			else
				rootNode = sourceOntology.getPropertiesRoot();
		} else {
			Ontology targetOntology = currentTask.matcherResult
					.getTargetOntology();
			if (VAPanelLogic.getCurrentNodeType() == VAVariables.nodeType.Class)
				rootNode = targetOntology.getClassesRoot();
			else
				rootNode = targetOntology.getPropertiesRoot();
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
		MatchingTask matchingTask = Core.getInstance().getMatchingTasks()
				.get(currentDisplayNum);
		SimilarityMatrix smMatrix = null;
		if (VAPanelLogic.getCurrentNodeType() == VAVariables.nodeType.Class)
			smMatrix = matchingTask.matcherResult.getClassesMatrix();
		else
			smMatrix = matchingTask.matcherResult.getPropertiesMatrix();
		Mapping map[] = null;
		if (ontologyType == VAVariables.ontologyType.Source) // input is source,
																// find target
			/**
			 * Array out of bound error sometimes happen here, just ignore
			 */
			map = smMatrix.getRowMaxValues(n.getIndex(), 1);
		else
			map = smMatrix.getColMaxValues(n.getIndex(), 1); // input is target,
																// find source
		if (map != null) {
			if (ontologyType == VAVariables.ontologyType.Source)
				matchingNode = map[0].getEntity2();
			else
				matchingNode = map[0].getEntity1();
			if (VAVariables.DEBUG == 1)
				sim = Math.random(); // only for testing
			else
				sim = map[0].getSimilarity();

		} else {
			System.out.println("mapping data is null ???");
		}
		sim = Math.round(sim * 100.0) / 100.0;
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
		Node rootNode = rootNodeData.getSourceNode();
		for (Node n : rootNode.getChildren()) {
			// get target node info which best matches this node
			VAData newChildData = getMatchingVAData(n, ontologyType);
			res.add(newChildData);
		}
		// if (ontologyType == VAVariables.ontologyType.Source)
		// Collections.sort(res);
		return res;
	}
}