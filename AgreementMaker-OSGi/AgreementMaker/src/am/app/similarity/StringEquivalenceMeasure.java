package am.app.similarity;

/**
 * A string similarity measure that is based on exact match of strings. This
 * measure is meant to be used for special cases or for debugging purposes.
 * 
 * @author Cosmin Stroe
 * 
 */
public class StringEquivalenceMeasure implements StringSimilarityMeasure {

	@Override
	public double getSimilarity(String s1, String s2) {
		if( s1.equals(s2) ) {
			return 1.0d;
		}
		
		return 0d;
	}

}
