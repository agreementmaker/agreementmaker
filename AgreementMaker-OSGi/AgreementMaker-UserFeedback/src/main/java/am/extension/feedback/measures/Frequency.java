package am.app.feedback.measures;

public class Frequency implements Comparable<Frequency> {

	
	double similarity;
	int occurs;
	
	public int compareTo(Frequency arg0) {
		if( similarity > arg0.similarity ) {
			return 1; // greater
		} else if( similarity == arg0.similarity ) {
			return 0;
		}
		return -1;
	}
	
	
	public boolean equals( Frequency fr ) {
		return similarity == fr.similarity;
	}
	
}
