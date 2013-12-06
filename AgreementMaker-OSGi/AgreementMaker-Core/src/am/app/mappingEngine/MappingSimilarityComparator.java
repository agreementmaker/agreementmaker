package am.app.mappingEngine;

import java.util.Comparator;

/**
 * This is a comparator that can be used to sort Mapping objects by their similarity.
 * 
 * @author Cosmin Stroe, January 29th, 2011.
 *
 */
public class MappingSimilarityComparator implements Comparator<Mapping> {

	/**
	 * Compares its two arguments for order. Returns a negative integer, zero, or a positive 
	 * integer as the first mapping's similarity is less than, equal to, or greater than the second's.
	 */
	@Override
	public int compare(Mapping o1, Mapping o2) {
		if( o1 == null && o2 == null ) return 0;
		if( o1 == null ) return -1;
		if( o2 == null ) return  1;
		if( o1.getSimilarity() <  o2.getSimilarity() ) return -1;
		if( o1.getSimilarity() == o2.getSimilarity() ) return 0;
		if( o1.getSimilarity() >  o2.getSimilarity() ) return 1;
		return 0;
	}

}
