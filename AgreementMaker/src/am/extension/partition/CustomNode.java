package am.extension.partition;

import java.util.ArrayList;
import java.util.HashMap;

import am.app.ontology.Node;



public class CustomNode{
	Node n;
	String localName;
	int depth;
	ArrayList<Node> ancestorList;
	HashMap<CustomNode, Float> similarNodeWeights;
	
	public CustomNode(Node node,ArrayList<Node> parents, String localName, int depth)
	{
		this.n = node;
		this.localName = localName;
		this.depth = depth;
		this.ancestorList = parents;
	}
	
	public void insertsimilarConcepts(CustomNode custNode, float linkkWeight)
	{
		this.similarNodeWeights.put(custNode, linkkWeight);
	}
	
	
}
