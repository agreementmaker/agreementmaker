package am.utility.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree Node with support for multiple inheritance.
 * 
 * @author cosmin
 *
 * @param <T>
 */
public class AMTreeNode<T> {

	private List<AMTreeNode<T>> childNodes;
	private List<AMTreeNode<T>> parentNodes;
	
	private T object;
	private AMTree<T> tree;
	
	public AMTreeNode(AMTree<T> tree, T object) {
		this.tree = tree;
		this.object = object;
		childNodes = new ArrayList<AMTreeNode<T>>();
		parentNodes = new ArrayList<AMTreeNode<T>>();
	}
	
	public boolean hasChildren() {
		if( childNodes == null || childNodes.size() == 0 ) return false;
		return true;
	}
	public List<AMTreeNode<T>> getChildren() { return childNodes; }
	
	public boolean hasParents() {
		if( parentNodes == null || parentNodes.size() == 0 ) return false;
		return true;
	}
	public List<AMTreeNode<T>> getParents() { return parentNodes; }
	
	public T getObject() { return object; }
	
	public boolean isRoot() { return this.equals(tree.getRoot()); }
	
	public void addChild(T object) {
		AMTreeNode<T> node = new AMTreeNode<T>(tree, object);
		childNodes.add(node);
		node.addParent(this);
	}
	
	public void addParent(AMTreeNode<T> parent) {
		if( !parentNodes.contains(parent) )
			parentNodes.add(parent);
	}
	
}
