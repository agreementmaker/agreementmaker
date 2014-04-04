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

	private static VASyncData instance = null;

	// loaded algorithm
	private static int currentDisplayNum[] = new int[] { 1, 1 };

	private VAMatchingTask userTask = null;
	private ArrayList<VAMatchingTask> lstTask = new ArrayList<VAMatchingTask>();

	private VASyncData() {
		userTask = new VAMatchingTask(0);
		lstTask.add(userTask);// added but never use this one in the ArrayList
		for (int i = 1; i <= VAMatchingTask.totalDisplayNum; i++)
			lstTask.add(new VAMatchingTask(i));
	}

	public static VASyncData getInstance() {
		if (instance == null)
			instance = new VASyncData();
		return instance;
	}

	/**
	 * Get a node's parent node
	 * 
	 * @param n
	 * @return
	 */
	private Node getParentNode(Node n) {
		if (n.getParents().size() > 0)
			return n.getParents().get(0);
		else
			return null;
	}

	public int getCurrentDisplayNum(int i) {
		return currentDisplayNum[i];
	}

	public void setCurrentDisplayNum(int currentDisplayNum, int set) {
		VASyncData.currentDisplayNum[set] = currentDisplayNum;
	}

	/**
	 * Get a VAData's parent data
	 * 
	 * @param v
	 * @return
	 */
	public VAData getParentVAData(VAData v) {
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
	private Node getRootNode(VAVariables.ontologyType ontologyType, int set) {
		Node rootNode = null;
		VAMatchingTask vaMatchingTask;
		if (currentDisplayNum[set] <= VAMatchingTask.totalDisplayNum)// matchingTask.size())
			vaMatchingTask = lstTask.get(currentDisplayNum[set]);// bug here
		else {
			// Error! Should never enter here
			return null;
		}
		if (ontologyType == VAVariables.ontologyType.Source) {
			if (VAPanelLogic.getCurrentNodeType() == VAVariables.nodeType.Class)
				rootNode = vaMatchingTask.getSource().getClassesRoot();
			else
				rootNode = vaMatchingTask.getSource().getPropertiesRoot();
		} else {
			if (VAPanelLogic.getCurrentNodeType() == VAVariables.nodeType.Class)
				rootNode = vaMatchingTask.getTarget().getClassesRoot();
			else
				rootNode = vaMatchingTask.getTarget().getPropertiesRoot();
		}
		return rootNode;
	}

	/**
	 * Get VAData for Root node
	 * 
	 * @param ontologyType
	 * @return
	 */
	public VAData getRootVAData(VAVariables.ontologyType ontologyType, int set) {
		Node sNode = null, tNode = null;
		double Similarity = 0.0;
		if (ontologyType == VAVariables.ontologyType.Source) { // pie chart for
																// source
																// ontology
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
	private VAData getMatchingVAData(Node n, VAVariables.ontologyType ontologyType, int set) {
		Node matchingNode = null;
		double sim = 0.00;
		VAMatchingTask vaMatchingTask = lstTask.get(currentDisplayNum[set]);
		Mapping map[] = null;
		if (ontologyType == VAVariables.ontologyType.Source) // input is source,
																// find target
			/**
			 * Array out of bound error sometimes happen here, just ignore
			 */
			map = vaMatchingTask.getClassMatrix().getRowMaxValues(n.getIndex(), 1);// smMatrix.getRowMaxValues(n.getIndex(),
																					// 1);
		else
			map = vaMatchingTask.getPropertyMatrix().getColMaxValues(n.getIndex(), 1); // input
																						// is
																						// target,
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
	public ArrayList<VAData> getChildrenData(VAData rootNodeData, VAVariables.ontologyType ontologyType, int i) {
		ArrayList<VAData> res = new ArrayList<VAData>();
		Node rootNode = rootNodeData.getSourceNode();
		for (Node n : rootNode.getChildren()) {
			// get target node info which best matches this node
			VAData newChildData = getMatchingVAData(n, ontologyType, i);
			res.add(newChildData);
		}
		return res;
	}

	/**
	 * Search VAData from the rootNode
	 * 
	 * @param name
	 * @param rootNode
	 * @return
	 */
	public VAData searchFrom(String name, VAData rootNode, int set) {
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
				ArrayList<VAData> children = getChildrenData(rootNode, ontologyType.Source, set);
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
