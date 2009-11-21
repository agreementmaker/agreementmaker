package am.utility;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The Graph Abstract Data Type, implemented using the adjacency list structure.
 * This data structure is used by Canvas2, in order to store graphical layout information about the ontologies loaded.
 * 
 * A graph has Vertices (represented by LayoutVertex objects) and Edges (represented by LayoutEdge objects).
 * 
 * Each Vertex is a container, storing an object o (the edge label), and an Incidence Container
 * 
 * For ontologies, the object that we store will be an Element.
 * 
 * @author cosmin
 *
 */
public class DirectedGraph<E,V> {

	protected ArrayList<V> vertices;
	protected ArrayList<E> edges;
	
	ArrayList<DirectedGraphVertex<E>> hierarchyRoots;
	
	
	public DirectedGraph() {
		vertices = new ArrayList<V>();
		edges    = new ArrayList<E>();
	}
	
	/* General Graph Methods */
	public int numVertices() { return vertices.size(); }
	public int numEdges()    { return edges.size(); }
	
	public Iterator<V> vertices() { return vertices.iterator(); }  // return an iterator of the vertices of G
	public Iterator<E>   edges()    { return edges.iterator(); } // return an iterator of the edges of G
	
	public V           aVertex()  { if( vertices.size() != 0 ) return vertices.get(0); else return null; }  // return any vertex of the graph
	
	
	/**
	 * Slightly modified insertEdge in order to keep track of the bounds.
	 */
	public void insertEdge( E edge) {
		edges.add(edge);
	}
	
	/**
	 * Slightly modified insertVertex in order to keep track of the bounds.
	 */
	public void insertVertex( V vert ) {
		vertices.add(vert);
	}
	
	public void removeEdge( E e ) { edges.remove(e); }

	public void removeVertex( V e ) { vertices.remove(e); }
	
	
}
