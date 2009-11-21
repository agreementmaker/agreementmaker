package am.utility;

/**
 * Directed Graph Edge.  This is a container class, storing objects of type E.
 * @author cosmin
 *
 * @param <E>
 */
public class DirectedGraphEdge<E> {
	
	protected DirectedGraphVertex<E> origin;
	protected DirectedGraphVertex<E> destination;
	
	protected E d;
	
	public DirectedGraphEdge(DirectedGraphVertex<E> orig, DirectedGraphVertex<E> dest, E o) {
		origin      = orig;
		destination = dest;
		d      = o;
	}
	public DirectedGraphVertex<E> getDestination() { return destination; }
	public DirectedGraphVertex<E> getOrigin()      { return origin; }
	public void        setDestination( DirectedGraphVertex<E> v ) { destination = v; }
	public void        setOrigin     ( DirectedGraphVertex<E> v ) { origin      = v; }
	
	public void setObject(E object) { this.d = object; }
	public E    getObject()         { return d; }

	public void reverseDirection() {
		DirectedGraphVertex<E> temp = origin;
		origin = destination;
		destination = temp;
	}
}
