package am.app.mappingEngine;

import java.util.LinkedList;

import am.app.mappingEngine.utility.MatchingPair;

public class MatchingPairAlignment extends LinkedList<MatchingPair> {

	private static final long serialVersionUID = -4983343979595911813L;

	/**
	 * @param mpa
	 * @return The number of mappings contained in common between the current alignment and another alignment.
	 */
	public int intersection(MatchingPairAlignment mpa) {
		int int1 = this.innerIntersection(mpa);
		int int2 = mpa.innerIntersection(this);
		return Math.min(int1, int2); // this is a hack to deal with duplicates.  Use a better way? - Cosmin, Dec 3, 2013.		
	}
	
	private int innerIntersection(MatchingPairAlignment mpa) {
		int count = 0;
		MatchingPairAlignment temp = new MatchingPairAlignment(); // used to avoid counting duplicates
		for(MatchingPair mp : this ) {
			if( mpa.contains(mp) && !temp.contains(mp) ) {
				temp.add(mp);
				count++;
			}
		}
		return count;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int i = 1;
		for( MatchingPair mp : this ) {
			builder.append(i++);
			builder.append(". ");
			builder.append(mp.toString());
			builder.append("\n");
		}
		return builder.toString();
	}

}
