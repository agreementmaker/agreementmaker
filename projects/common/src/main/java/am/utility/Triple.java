package am.utility;

/**
 * 
 * @author joe
 * 
 * this class is like the pair class, but it has three items instead of two (left, center, right)
 *
 */

public class Triple<E,V,T> {

	private E left;
	private V center;
	private T right;
	
	public Triple(E l, V c, T r){
		left=l;
		center=c;
		right=r;
	}
	
	public E getLeft(){return left;}
	public V getCenter(){return center;}
	public T getRight(){return right;}
	
	public void setLeft(E l){left=l;}
	public void setCenter(V c){center=c;}
	public void setRight(T r){right=r;}
	
}
