package am.utility.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * A Tree data structure.
 * @author cosmin
 *
 * @param <T>
 */
public class AMTree<T> {

	private AMTreeNode<T> root;
	
	public AMTree(T rootObject) {
		root = new AMTreeNode<T>(this, rootObject);
	}

	public AMTreeNode<T> getRoot() { return root; } 
	
	public List<T> getElementList() { return getElementSubList(root); }
	
	public List<T> getElementSubList(AMTreeNode<T> node) {
		List<T> l = new ArrayList<T>();
		
		l.add(node.getObject()); // add the object of the current node;
		if( node.hasChildren() )
			for( AMTreeNode<T> currentChild : node.getChildren() )
				l.addAll( getElementSubList(currentChild) ); // recursive call
		
		return l;
	}
	
}
