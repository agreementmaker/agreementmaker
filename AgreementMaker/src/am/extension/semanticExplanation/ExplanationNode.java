package am.extension.semanticExplanation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.collections.map.HashedMap;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import edu.uci.ics.jung.graph.DelegateTree;

public class ExplanationNode {
	double val;
	List<ExplanationNode> children;
	CombinationCriteria criteria;
	String description;
	public DelegateTree<ExplanationNode,String> tree= new DelegateTree<ExplanationNode, String>();
	static int edge=1;
	private int repetitionCount;
	String source;
	String target;
	
	public ExplanationNode(double val, List<ExplanationNode> children,
			CombinationCriteria criteria, String description) {
		this.val = val;
		this.children = children;
		this.criteria = criteria;
		this.description = description;
		setRepetitionCount(0);
		tree = new DelegateTree<ExplanationNode, String>();
	}


	public ExplanationNode() {
		this.val = 0;
		this.children = new ArrayList<ExplanationNode>();
		this.criteria = CombinationCriteria.NOTDEFINED;
		this.description = "";
		setRepetitionCount(0);
		tree = new DelegateTree<ExplanationNode, String>();
	}
	
	public ExplanationNode(String description) {
		this.val = 0;
		this.children = new ArrayList<ExplanationNode>();
		this.criteria = CombinationCriteria.NOTDEFINED;
		this.description = description;
		setRepetitionCount(0);
		tree = new DelegateTree<ExplanationNode, String>();
	}

	public void setSource(String s) {
		this.source = s;
	}
	
	public String getSource() {
		return this.source;
	}
	
	public void setTarget(String t) {
		this.target = t;
	}
	
	public String getTarget() {
		return this.target;
	}
	
	
	public void addChild(ExplanationNode node){
		this.children.add(node);
	}

	
	public void describeTopDown() {
		Queue<ExplanationNode> explnQ = new LinkedList<ExplanationNode>();
		explnQ.add(this);
		tree.setRoot(this);
//		addChildren(this, tree);
		while(explnQ.size()>0) {
			ExplanationNode node = explnQ.remove();
			node.describeNode();
			// Graph<V, E> where V is the type of the vertices
			// and E is the type of the edges
			// Add some vertices. From above we defined these to be type Integer.
			// Add some edges. From above we defined these to be of type String
			// Note that the default is for undirected edges.
			if(node.children.size()>0) {
				for(ExplanationNode child: node.children) {
					//tree.addEdge("edge "+edge, new String(String.valueOf(node.val)), new String(String.valueOf(child.val))+"");
					tree.addChild("edge "+edge, node, child);
					explnQ.add(child);
					edge++;
				}
			}

		}
		System.out.println("The graph g = " + tree.toString());
	}
	
	/**
	 * Finds the most significant path in an aligned mapping
	 * @param ExplanationNode node
	 * @return List of nodes
	 */
	public static List<ExplanationNode> findMostSignificantPath(ExplanationNode node) {
		List<ExplanationNode> mspList = new ArrayList<ExplanationNode>();
		Queue<ExplanationNode> explnQ = new LinkedList<ExplanationNode>();
		explnQ.add(node);
		mspList.add(node);
		while(explnQ.size()>0) {
			ExplanationNode currentNode = explnQ.remove();
			ExplanationNode largerChild = new ExplanationNode();
			largerChild.setVal(0);
			if(currentNode.children.size()>0) {
				for(ExplanationNode child: currentNode.children) {
					if(child.getVal() > largerChild.getVal()) {
						largerChild = child;
					}
				}
				explnQ.add(largerChild);
				mspList.add(largerChild);
			}
		}
		return mspList;
		
	}

	/**
	 * @param ExplanationNode node
	 * Finds the least significant path in an aligned mapping
	 * @return List of nodes
	 */
	public static List<ExplanationNode> findLeastSignificantPath(ExplanationNode node) {
		List<ExplanationNode> mspList = new ArrayList<ExplanationNode>();
		Queue<ExplanationNode> explnQ = new LinkedList<ExplanationNode>();
		explnQ.add(node);
		mspList.add(node);
		while(explnQ.size()>0) {
			ExplanationNode currentNode = explnQ.remove();
			ExplanationNode largerChild = new ExplanationNode();
			largerChild.setVal(1.0);
			if(currentNode.children.size()>0) {
				for(ExplanationNode child: currentNode.children) {
					if(child.getVal() < largerChild.getVal()) {
						largerChild = child;
					}
				}
				explnQ.add(largerChild);
				mspList.add(largerChild);
			}
		}
		return mspList;
		
	}
	
	
	private static void addChildren(ExplanationNode node, DelegateTree<String, String> tree) {
	    for (int i = 0; i < node.getChildren().size(); i++) {
	        tree.addChild("edge "+edge,new String(String.valueOf(node.val)), new String(String.valueOf(node.getChildren().get(i).val)));
	        edge++;
	        addChildren(node.getChildren().get(i), tree);
	    }
	    edge++;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExplanationNode other = (ExplanationNode) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}
	
	
	/**
	 * Function to copy the structure of a particular ExplanationNode Graph.
	 * @return ExplanationNode
	 */
	public ExplanationNode deepCopyStructure(){
		ExplanationNode returnNode = new ExplanationNode(this.getDescription());
		for(ExplanationNode childNode: this.getChildren()){
			if(childNode == null)
				return null;
			returnNode.addChild(childNode.deepCopyStructure());
		}
		return returnNode;
	}


	/**
	 * Will parse the graph to find where the passed Node is present, and count the repetitionCount 
	 * at the corresponding node. 
	 * 
	 * @param node
	 */
	public void addCountIntelligently(ExplanationNode node) {
		if(this == null)
			return;
		if(this.getDescription().equals(node.getDescription())){
			setRepetitionCount(getRepetitionCount() + 1);
			return;
		}
		for(ExplanationNode child: this.getChildren()){
			child.addCountIntelligently(node);
		}
		return;
	}


	/*
	 * Utility Functions for debugging
	 */
	
	
	public void describeNode(){
		System.out.println("A similarity value of "+this.val+" was generated by Algorithm: "+this.description +" and repetition: "+repetitionCount);
	}

	public void describeExplanation(){
		if(this.children.size() ==0){
			this.describeNode();
			return;
		}
		for(ExplanationNode node: this.children){
			node.describeExplanation();
			
		}
		this.describeNode();
	}
	
	/*
	 * 
	 * Getters and setters for the Class
	 * 
	 */
	
	public double getVal() {
		return val;
	}


	public void setVal(double val) {
		this.val = val;
	}


	public List<ExplanationNode> getChildren() {
		return children;
	}


	public void setChildren(List<ExplanationNode> children) {
		this.children = children;
	}


	public CombinationCriteria getCriteria() {
		return criteria;
	}


	public void setCriteria(CombinationCriteria criteria) {
		this.criteria = criteria;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getRepetitionCount() {
		return repetitionCount;
	}


	public void setRepetitionCount(int repetitionCount) {
		this.repetitionCount = repetitionCount;
	}


}


