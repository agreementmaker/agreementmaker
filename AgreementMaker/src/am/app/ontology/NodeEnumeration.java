package am.app.ontology;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Implements enumerations that transverse the hierarchy under the given node.
 * 
 * 
 * 
 * 
 *                        preorder   [ NODE ]   post
 *                                  /   in   \
 *                                 /          \ 
 *       preorder    [   CHILD1  ] post    pre [   CHILD2  ] post
 *                        in                        in
 *                        
 * Draw an Euler tour around the nodes and build the lists.  Solution for each type of enumeration below:
 * 
 * Preorder: NODE, CHILD1, CHILD2
 * Inorder: CHILD1, NODE, CHILD2
 * Postorder: CHILD1, CHILD2, NODE
 * 
 */
public class NodeEnumeration implements Enumeration<Node> {

	public enum EnumerationType {
		PREORDER,		// Preorder enumeration.
		//INORDER,		// Inorder enumeration, does not not make sense for non-tree structures, unless you define where in the list to add the current node.  Maybe later. -- Cosmin.
		POSTORDER,  	// Postorder enumeration.
		DESCENDANTS;	// Descendants only.
	}
	
	private int enumerationIndex = 0;
	private List<Node> enumerationList = new ArrayList<Node>();
	
	public NodeEnumeration(Node startingNode, EnumerationType type ) {
		buildList( startingNode, type);
	}
	
	/** Recursive call method for building the list. */
	private void buildList( Node currentNode, EnumerationType type ) {
		
		if( type == EnumerationType.PREORDER ) enumerationList.add(currentNode);
		
		for( int i = 0; i < currentNode.getChildCount(); i++ ) {
			Node currentChild = currentNode.getChildAt(i);
			if( type == EnumerationType.DESCENDANTS ) enumerationList.add(currentChild);
			buildList(currentChild, type); // recursive call
		}
		
		if( type == EnumerationType.POSTORDER ) enumerationList.add(currentNode);
	}
	
	@Override
	public boolean hasMoreElements() {
		return enumerationIndex < enumerationList.size();
	}

	@Override
	public Node nextElement() {
		Node returnVal = enumerationList.get(enumerationIndex);
		enumerationIndex++;
		return returnVal;
	}

}
