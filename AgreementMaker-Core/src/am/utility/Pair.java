package am.utility;

import java.io.Serializable;

public class Pair<E,V> implements Serializable{

	private static final long serialVersionUID = -2069249434335717697L;
	
	private E left;
	private V right;
	
	public Pair( E l, V r ) {
		left = l;
		right = r;
	}
	
	public E getLeft() { return left; }
	public V getRight() { return right; }
	
	public void setLeft(E l) { left = l; }
	public void setRight(V r) { right = r; }
	
	public String toString(){
		return this.getLeft().toString() + " --- " + this.getRight().toString();
	}
}
