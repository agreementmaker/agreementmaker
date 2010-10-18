package am.utility;

import java.util.ArrayList;
import java.util.Iterator;


public class DirectedGraphVertex<E> {

	
	protected ArrayList<DirectedGraphEdge<E>> edgesIn;
	protected ArrayList<DirectedGraphEdge<E>> edgesOut;
	
	protected E d;  // the object stored within this vertex
	
	public DirectedGraphVertex(E object) {
		edgesIn  = new ArrayList<DirectedGraphEdge<E>>();
		edgesOut = new ArrayList<DirectedGraphEdge<E>>();
		d   	 = object; 
	}
	
	public int inDegree()  { return edgesIn.size(); }
	public int outDegree() { return edgesOut.size(); }
	
	public Iterator<DirectedGraphEdge<E>> edgesInIter()  { return edgesIn.iterator(); }
	public Iterator<DirectedGraphEdge<E>> edgesOutIter() { return edgesOut.iterator(); }

	public ArrayList<DirectedGraphEdge<E>> edgesInList() { return edgesIn; }
	public ArrayList<DirectedGraphEdge<E>> edgesOutList() { return edgesOut; }
	
	public void setObject(E object) { this.d = object; }
	public E    getObject()         { return d; }
	
	public void addInEdge(DirectedGraphEdge<E> inEdge)   { edgesIn.add(inEdge);   }
	public void addOutEdge(DirectedGraphEdge<E> outEdge) { edgesOut.add(outEdge); }

	
	// inAdjacentVertices() -- need to implement this
	// outAdjacentVertices() -- need to implement this
}
