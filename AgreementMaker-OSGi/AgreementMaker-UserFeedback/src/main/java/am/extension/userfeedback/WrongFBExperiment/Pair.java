package am.extension.userfeedback.WrongFBExperiment;

public class Pair<A, B> {
	private A first;
	private B second;
	
	public Pair(A first, B second){
		super();
		this.first = first;
		this.second = second;
	}
	public Pair(){
		super();
	}
	
	public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;
        return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}
	
	public boolean equals(Object other) {
        if (other instanceof Pair) { 
        	Pair otherPair = (Pair) other;
        	return
        			((this.first == otherPair.first ||
        			(this.first != null && otherPair.first != null &&
        			this.first.equals(otherPair.first))) &&
        			(this.second == otherPair.second ||
        			( this.second != null && otherPair.second != null &&
        			this.second.equals(otherPair.second))));
        }

        return false;
	}
	
	public A getFirst(){
		return this.first;
	}
	
	public B getSecond(){
		return this.second;
	}
	
	public void setFirst(A first){
		this.first = first;
	}
	
	public void setSecond(B second){
		this.second = second;
	}
	
	public void set(Pair<A, B> other){
		this.first = other.first;
		this.second = other.second;
	}
}
