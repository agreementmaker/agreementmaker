package am.app.mappingEngine;

import java.util.Comparator;

/**
 * This is a comparator that can be used to sort Mapping objects by their similarity.
 * 
 * @author Cosmin Stroe, January 29th, 2011. Simplified Jan 12th, 2014.
 *
 */
public class MappingSimilarityComparator implements Comparator<Mapping> {

	private boolean descending = false;
	
	public MappingSimilarityComparator() {}
	
	public MappingSimilarityComparator(boolean descending) {
		this.descending = descending;
	}
	
	/**
	 * Compares its two arguments for order. Returns a negative integer, zero, or a positive 
	 * integer as the first mapping's similarity is less than, equal to, or greater than the second's.
	 */
	@Override
	public int compare(Mapping o1, Mapping o2) {
		if(descending) {
			return Double.compare(o2.getSimilarity(), o1.getSimilarity());
		} else {
			return Double.compare(o1.getSimilarity(), o2.getSimilarity());
		}
	}
}
