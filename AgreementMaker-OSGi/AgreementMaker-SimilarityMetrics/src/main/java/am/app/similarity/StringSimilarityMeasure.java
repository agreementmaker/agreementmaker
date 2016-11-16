package am.app.similarity;

/**
 * Implemented by any string similarity measure in our system.
 */
public interface StringSimilarityMeasure {
	/** @return A similarity value betwen 0.0 (no match) and 1.0 (exact match). */
	double getSimilarity( String s1, String s2 );
	
}
