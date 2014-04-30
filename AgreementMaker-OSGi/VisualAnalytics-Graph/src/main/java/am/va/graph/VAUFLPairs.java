package am.va.graph;

import java.util.HashMap;

import am.app.ontology.Node;

/**
 * Possible alignment pairs, 1 to n
 * 
 * @author Yiting
 * 
 */
public class VAUFLPairs {
	private Node sourceNode;
	private HashMap<String, Node> targetNodes; // target name & target node
	private String bestChoice;
	private double sim;

	public VAUFLPairs(Node sourceNode) {
		super();
		this.sourceNode = sourceNode;
		this.targetNodes = new HashMap<String, Node>();
		this.bestChoice = "";
		sim = 0;
	}

	public Node getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(Node sourceNode) {
		this.sourceNode = sourceNode;
	}

	public HashMap<String, Node> getTargetNodes() {
		return targetNodes;
	}

	public void setTargetNodes(HashMap<String, Node> targetNodes) {
		this.targetNodes = targetNodes;
	}

	public void addToTargetList(Node n) { // set name here
		targetNodes.put(n.getLocalName() + "|" + n.getLabel(), n);
	}

	public void setBestChoice(String c) {
		this.bestChoice = c;
	}

	public boolean selected() {
		return !bestChoice.equals("");
	}

	public String getBestChoice() {
		return bestChoice;
	}

	public void setSim(double sim) {
		this.sim = sim;
	}

	public String getSim() {
		return String.valueOf(sim);
	}

	/**
	 * Check if target node is already in the hash map
	 * 
	 * @param target
	 * @return
	 */
	public boolean containTarget(String target) {
		if (targetNodes.containsKey(target))
			return true;
		return false;
	}

}
