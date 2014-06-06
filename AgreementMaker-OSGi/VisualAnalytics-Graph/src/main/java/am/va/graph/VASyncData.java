package am.va.graph;

import java.util.ArrayList;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.va.graph.VAVariables.ontologyType;

public class VASyncData {

	private static int totalDisplayNum = 0;
	// private static int currentDisplayNum = 1; //default value = the first
	// loaded algorithm
	private static int currentDisplayNum[] = new int[] { 1, 1 };// display num
																// of up & down
																// matching sets

	private static List<MatchingTask> matchingTasks = null;

	// private static ArrayList<VAMatchingTask> lstTask = new
	// ArrayList<VAMatchingTask>();

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

	public static int getCurrentDisplayNum(int i) {
		return currentDisplayNum[i];
	}

	public static void setCurrentDisplayNum(int currentDisplayNum, int set) {
		VASyncData.currentDisplayNum[set] = currentDisplayNum;
	}

	public static List<MatchingTask> getMatchingTasks() {
		return matchingTasks;
	}

	public static MatchingTask getCurrentMatchingTask(int set) {
		MatchingTask current = null;
		if (matchingTasks == null) {
			matchingTasks = Core.getInstance().getMatchingTasks();
			totalDisplayNum = matchingTasks.size() - 1;
		}
		if (currentDisplayNum[set] <= matchingTasks.size())
			current = matchingTasks.get(currentDisplayNum[set]);
		return current;
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
	private static Node getRootNode(VAVariables.ontologyType ontologyType, int set) {
		Node rootNode = null;
		MatchingTask currentTask = getCurrentMatchingTask(set);

		if (ontologyType == VAVariables.ontologyType.Source) {

			// Testing...
			String alg = currentTask.matchingAlgorithm.getName();
			System.out.println("algorithm: " + alg);
			System.out.println("report:" + currentTask.getMatchingReport());

			Ontology sourceOntology = currentTask.matcherResult.getSourceOntology();
			if (VAPanelLogic.getCurrentNodeType() == VAVariables.nodeType.Class)
				rootNode = sourceOntology.getClassesRoot();
			else
				rootNode = sourceOntology.getPropertiesRoot();
		} else {
			Ontology targetOntology = currentTask.matcherResult.getTargetOntology();
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
	public static VAData getRootVAData(VAVariables.ontologyType ontologyType, int set) {
		Node sNode = null, tNode = null;
		double Similarity = 0.0;
		if (ontologyType == VAVariables.ontologyType.Source) {
			sNode = getRootNode(VAVariables.ontologyType.Source, set);
		} else {
			sNode = getRootNode(VAVariables.ontologyType.Target, set);
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
	private static VAData getMatchingVAData(Node n, VAVariables.ontologyType ontologyType, int set) {
		Node matchingNode = null;
		double sim = 0.00;
		MatchingTask matchingTask = Core.getInstance().getMatchingTasks().get(currentDisplayNum[set]);
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
	public static ArrayList<VAData> getChildrenData(VAData rootNodeData, VAVariables.ontologyType ontologyType, int i) {
		ArrayList<VAData> res = new ArrayList<VAData>();
		Node rootNode = rootNodeData.getSourceNode();
		for (Node n : rootNode.getChildren()) {
			// get target node info which best matches this node
			VAData newChildData = getMatchingVAData(n, ontologyType, i);
			res.add(newChildData);
		}
		// if (ontologyType == VAVariables.ontologyType.Source)
		// Collections.sort(res);
		return res;
	}

	/**
	 * Search VAData from the rootNode
	 * 
	 * @param name
	 * @param rootNode
	 * @return
	 */
	public static VAData searchFrom(String name, VAData rootNode, int set) {
		// TODO Auto-generated method stub
		// System.out.println("Search from " + rootNode.getNodeName());

		if (rootNode != null) {
			// find ontology
			if (rootNode.getNodeName() != null && rootNode.getNodeName().equals(name)) {
				System.out.println("VASearch - Return " + rootNode.getNodeName());
				return rootNode;
			}

			// Search children recursively
			if (rootNode.hasChildren()) {
				// set 0 here
				ArrayList<VAData> children = VASyncData.getChildrenData(rootNode, ontologyType.Source, set);
				for (VAData childData : children) {
					VAData result = searchFrom(name, childData, set);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}
}
