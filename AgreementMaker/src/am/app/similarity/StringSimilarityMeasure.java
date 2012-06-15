package am.app.similarity;

/**
 * Implemented by any string similarity measure in our system.
 * 
 * TODO: Consider using {@link uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric}.
 */
public interface StringSimilarityMeasure {

	/**
	 * @return A similarity value betwen 0.0 and 1.0.
	 */
	public double getSimilarity( String s1, String s2 );
	
}
