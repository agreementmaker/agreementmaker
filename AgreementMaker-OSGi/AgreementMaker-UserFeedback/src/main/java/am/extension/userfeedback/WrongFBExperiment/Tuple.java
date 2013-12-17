package am.extension.userfeedback.WrongFBExperiment;

public class Tuple<A, B, C, D>{
	private A first;
	private B second;
	private C third;
	private D forth;
	
	public Tuple(){
		super();
	}
	
	public Tuple(A first, B second, C third, D forth){
		super();
		this.first = first;
		this.second = second;
		this.third = third;
		this.forth = forth;
	}
	
	public int hashCode(){
		int hashFirst = first!=null? first.hashCode():0;
		int hashSecond = second!=null? second.hashCode():0;
		int hashThird = third!=null? third.hashCode():0;
		
		return (hashFirst+hashSecond+hashThird) * hashThird + hashFirst + hashSecond;
	}
	
	public boolean equals(Object other){
		if(other instanceof Tuple){
			Tuple otherTuple = (Tuple) other;
			return 
					((this.first == otherTuple.getFirst() || 
						(this.first!=null && otherTuple.getFirst()!=null && this.first.equals(otherTuple.getFirst()))) 
					&&
					(this.second == otherTuple.getSecond() ||
						(this.second!=null && otherTuple.getSecond()!=null && this.second.equals(otherTuple.getSecond())))
					&&
					(this.third == otherTuple.getThird() ||
						(this.third!=null && otherTuple.getThird()!=null && this.third.equals(otherTuple.getThird())))
					&&
					(this.third == otherTuple.getThird() ||
						(this.third!=null && otherTuple.getThird()!=null && this.third.equals(otherTuple.getThird()))));
					
		}
		return false;
	}
	
	public void setFirst(A first){
		this.first = first;
	}
	
	public void setSecond(B second){
		this.second = second;
	}
	
	public void setThird(C third){
		this.third = third;
	}
	
	public void setForth(D forth) {
		this.forth = forth;
	}
	
	public A getFirst(){
		return this.first;
	}
	
	public B getSecond(){
		return this.second;
	}
	
	public C getThird(){
		return this.third;
	}
	
	public D getForth() {
		return this.forth;
	}
	
}
