package am.utility;

public class Capsule<E> implements Comparable<Capsule<E>> {

	double rank;
	E payLoad;
	
	public Capsule(double rank, E payLoad) {
		this.rank = rank;
		this.payLoad = payLoad;
	}
	
	public E getPayload() { return payLoad; }
	public double getRank() { return rank; }
	
	@Override
	public int compareTo(Capsule<E> o) {
		if( rank - o.getRank() > 0d ) return 1;
		if( rank - o.getRank() < 0d ) return -1;
		return 0;
	}

}
