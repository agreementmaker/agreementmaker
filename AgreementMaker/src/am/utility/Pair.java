package am.utility;

public class Pair<E,V> {

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
