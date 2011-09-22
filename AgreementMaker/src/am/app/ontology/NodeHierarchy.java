package am.app.ontology;

import java.util.Set;

/**
 * This interface represents a node hierarchy.
 * 
 * A node hierarchy can be computed when an ontology is loaded,
 * or by the user after the ontology is loaded.
 * 
 * @author Cosmin Stroe
 */
public interface NodeHierarchy {

	/**
	 * @return null if the node has no children.
	 */
	public Set<Node> getChildren(Node n);

	/**
	 * @return null if the node has no parents.
	 */
	public Set<Node> getParents(Node n);
	
	/**
	 * @return null if the node has no siblings.
	 */
	public Set<Node> getSiblings(Node n);
	
}
