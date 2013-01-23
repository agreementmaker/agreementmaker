package am.app.ontology;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import am.app.mappingEngine.AbstractMatcher.alignType;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class NodeUtility {

	/** Recursive method to return the leaves of the hierarchy under a node */
	public static List<Node> getLeaves(Node root) {
		LinkedList<Node> nodeList = new LinkedList<Node>();
		if( root.isLeaf() ) {
			nodeList.add(root);
		} else {
			List<Node> childList = root.getChildren();
			for( int i = 0; i < childList.size(); i++ ) {
				nodeList.addAll( getLeaves(childList.get(i)) ); // recursive call
			}
		}
		return nodeList;
	}

	/**
	 * @return A list of common descendants between two nodes.
	 */
	public static List<Node> getCommonDescendants( Node node1, Node node2 ) {

		Set<Node> node1Descendants = node1.getDescendants();
		Set<Node> node2Descendants = node2.getDescendants();

		node1Descendants.add(node1);
		node2Descendants.add(node2);

		// node1Descendants = node1Descendants INTERSECTION node2Descendants.
		node1Descendants.retainAll(node2Descendants);

		return new ArrayList<Node>(node1Descendants);
	}

	/** @return a list of common ancestors between two nodes. */
	public static List<Node> getCommonAncestors( Node node1, Node node2 ) {
		Set<Node> node1Ancestors = node1.getAncestors();
		Set<Node> node2Ancestors = node2.getAncestors();

		node1Ancestors.add(node1);
		node2Ancestors.add(node2);

		// node1Ancestors = node1Ancestors INTERSECTION node2Ancestors.
		node1Ancestors.retainAll(node2Ancestors);

		return new ArrayList<Node>(node1Ancestors);
	}

	public static List<Node> getSiblings(Node node) {
		List<Node> siblingsList = new ArrayList<Node>();

		for( Node parentNode : node.getParents() ) {
			for( Node siblingNode : parentNode.getChildren() ) {
				if( !siblingNode.equals(node) ) {
					siblingsList.add(siblingNode);
				}
			}
		}

		return siblingsList;
	}
	
	public static OntResource getOntResourceFromRDFNode(RDFNode node){
		// try to get the ontResource from them
		if(node.canAs(OntResource.class)){
			return node.as(OntResource.class);
		}
		else{
			return null;
		}
	}


	public static Node getNodefromOntResource(Ontology ont, OntResource res, alignType aType){
		try{
			Node n = ont.getNodefromOntResource(res, aType);
			if(n != null){
				return n;
			}
			else{
				return null;
			}
		}
		catch(Exception eClass){
			return null;
		}
	}

	public static Node getNodefromRDFNode(Ontology ont, RDFNode node, alignType aType){
		return getNodefromOntResource(ont, getOntResourceFromRDFNode(node), aType);
	}
}
