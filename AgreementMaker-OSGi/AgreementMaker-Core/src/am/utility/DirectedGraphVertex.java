package am.utility;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Directed Graph Vertex.  
 * This is a container class, storing objects of type V (type is specified at compile time).
 * 
 * @author cosmin
 *
 * @param <V>
 */
public class DirectedGraphVertex<V, E> {

	
	protected ArrayList<DirectedGraphEdge<E, V>> edgesIn;
	protected ArrayList<DirectedGraphEdge<E, V>> edgesOut;
	
	protected V d;  // the object stored within this vertex
	
	public DirectedGraphVertex(V object) {
		edgesIn  = new ArrayList<DirectedGraphEdge<E, V>>();
		edgesOut = new ArrayList<DirectedGraphEdge<E, V>>();
		d   	 = object; 
	}
	
	public int inDegree()  { return edgesIn.size(); }
	public int outDegree() { return edgesOut.size(); }
	
	public Iterator<DirectedGraphEdge<E, V>> edgesInIter()  { return edgesIn.iterator(); }
	public Iterator<DirectedGraphEdge<E, V>> edgesOutIter() { return edgesOut.iterator(); }

	public ArrayList<DirectedGraphEdge<E, V>> edgesInList() { return edgesIn; }
	public ArrayList<DirectedGraphEdge<E, V>> edgesOutList() { return edgesOut; }
	
	public void setObject(V object) { this.d = object; }
	public V    getObject()         { return d; }
	
	public void addInEdge(DirectedGraphEdge<E, V> inEdge)   { edgesIn.add(inEdge);   }
	public void addOutEdge(DirectedGraphEdge<E, V> outEdge) { edgesOut.add(outEdge); }

	
	// inAdjacentVertices() -- need to implement this
	// outAdjacentVertices() -- need to implement this
}
