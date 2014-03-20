package am.va.graph;

import java.util.HashMap;

import am.app.ontology.Node;

/**
 * Possible alignment pairs
 * @author Yiting
 *
 */
public class VAUFLPairs {
	private Node sourceNode;
	private HashMap<String, Node> targetNodes;
	
	
	
	public VAUFLPairs(Node sourceNode) {
		super();
		this.sourceNode = sourceNode;
		this.targetNodes = new HashMap<String, Node>();
	}
	
	public Node getSourceNode() {
		return sourceNode;
	}
	public void setSourceNode(Node sourceNode) {
		this.sourceNode = sourceNode;
	}
	
	public void addToTargetList(String s, Node n){
		targetNodes.put(s, n);
	}
	
	
}
