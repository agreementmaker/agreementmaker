package am.utility;

/**
 * Directed Graph Edge.  This is a container class, storing objects of type E.
 *
 * @author cosmin
 *
 * @param <E>
 */
public class DirectedGraphEdge<E, V> {
	
	protected DirectedGraphVertex<V, E> origin;  // we can mix Vertex types
	protected DirectedGraphVertex<V, E> destination; 
	
	protected E d;
	
	public DirectedGraphEdge(DirectedGraphVertex<V, E> orig, DirectedGraphVertex<V, E> dest, E o) {
		origin      = orig;
		destination = dest;
		d      = o;
	}
	public DirectedGraphVertex<V, E> getDestination() { return destination; }
	public DirectedGraphVertex<V, E> getOrigin()      { return origin; }
	public void        setDestination( DirectedGraphVertex<V, E> v ) { destination = v; }
	public void        setOrigin     ( DirectedGraphVertex<V, E> v ) { origin      = v; }
	
	public void setObject(E object) { this.d = object; }
	public E    getObject()         { return d; }

/*  // Commented out because we cannot reverse direction without origin and destination types being the same.
	public void reverseDirection() {
		DirectedGraphVertex<O> temp = origin;
		origin = destination;
		destination = temp;
	}
*/
}
