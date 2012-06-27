package am.app.ontology;

import java.util.List;


/** this is used to create a fake DAG given the root of tree of vertex with duplicates and so on
 *  so you will have to do new TreeToDagConverter(Vertex root) 
 *  and then invoke methods from that
 *  It is used also to avoid using the vertex structure, so that you can only use nodes
 *  So vertex should the parameter only of the constructor, all the other methods should receive and return node
 *  IT WORKS ONLY IF WE KEEP THE STRUCTURE LIKE THIS WITH ROOTS AT SECOND LEVEL
	//0 LEVEL ontology name, 1 level class and prop hierachy fake root nodes, 2 level real roots
 *
 *  TODO: Not sure what this class is used for.  It seems relatively useless, I'm merging its methods into the Node class. -- Cosmin, July 30, 2011.
 *  TODO: Now that everthing has been merged into the Node class, remove this class.
 *  */
@Deprecated
public class TreeToDagConverter {
	
	final static int REALROOTSLEVEL = 2;
	
	private Node root;
	
	public TreeToDagConverter(Node r) {
		if(r == null) {
			throw new RuntimeException("DEVELOPER ERROR, you are creating a dag from a null tree, maybe this ontology doesn't contain properties or classes, in that case the tree shouldn't be explored.");
		}
		root = r;
	}
	
	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}
	
	//*************************************** DAG TO TREE CONVERSION METHODS ********************************************************
	
	//RETURN THE LIST OF ROOTS Of this dag, it WORKS ONLY IF WE KEEP THE STRUCTURE LIKE THIS WITH ROOTS AT SECOND LEVEL
	//0 LEVEL ontology name, 1 level class and prop hierachy fake root node, 2 level real roots 
	public List<Node> getRoots() { return root.getChildren(); }
	
	/** @return the leaves of the root node (i.e. the whole hierarchy) */
	public List<Node> getLeaves() { return getLeaves(root); }
	
	/** Recursive method to return the leaves of the hierarchy under a node */
	public List<Node> getLeaves(Node root) {
		return Node.getLeaves(root);
	}

	//********************************************STATIC METHOD
	/**return the list of descendants of both this two node. Could be optimezed i guess but i don't have time.
	 * it's from the highest to the lowest, but there could be more then one at the same level.
	 * 
	 * */
	
	public static List<Node> getOrderedCommonDescendants(Node first, Node second){
		return Node.getCommonDescendants(first, second);
	}
	
	/**return the list of ancestors of both this two node. Could be optimezed i guess but i don't have time.
	 * it's from the lowest to the highest, but there could be more then one at the same level.
	 * */
	public static List<Node> getOrderedCommonAncestors(Node first, Node second){
		return Node.getCommonAncestors(first, second);
	}

}
