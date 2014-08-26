package am.evaluation.disagreement.variance;

import java.util.Vector;

/**
 * This class contains the functions to compute variance.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public class VarianceComputation {

	/**
	 * Computes the variance of an array of double values.
	 * 
	 * @param values
	 * @return The variance of the values. If the array is empty (length 0) then
	 *         the variance will be 0.
	 */
	public static double computeVariance(double[] values) {
		if( values.length == 0 ) return 0d;
		
		// variance is the average of the squares of the deviation of each value
		// deviation is the distance of the value from the mean
		
		// Step 1. Compute the mean.
		int n = 0;
		double sum = 0d;
		for( double d: values ) {
			sum += d;
			n++;
		}
		
		double mean = sum / n;
		
		// Step 2. Compute the deviation of each value;
		
		double[] deviation = new double[values.length];
		for(int i = 0; i < values.length; i++) {
			deviation[i] = values[i] - mean;
		}
		
		// Step 3. Square the deviation values.
		
		for( int i = 0; i < deviation.length; i++ ) {
			deviation[i] = deviation[i] * deviation[i];
		}
		
		// Step 4. Compute the variance, which is the average of the squared deviation.
		double devSum = 0d;
		for( double val : deviation ) {
			devSum += val;
		}
		
		double variance = devSum / n;
		
		return variance;
	}

	/**
	 * Helper function.
	 * 
	 * NOTE: This is an identical code to {@link #computeVariance(double[])},
	 * however, converting from Double[] to double[] so we can call the other
	 * function is costly (more so in terms of memory), so we will keep the code
	 * separate for now.
	 * 
	 * @see {@link #computeVariance(double[])}
	 */
	public static double computeVariance(Double[] similarityValues) {
		// variance is the average of the squares of the deviation of each value
		// deviation is the distance of the value from the mean
		
		// Step 1. Compute the mean.
		int n = 0;
		Double sum = 0d;
		for( Double d: similarityValues ) {
			sum += d;
			n++;
		}
		
		Double mean = sum / n;
		
		// Step 2. Compute the deviation of each value;
		
		Vector<Double> deviationVector = new Vector<Double>();
		for( Double val: similarityValues ) {
			deviationVector.add( val - mean );
		}
		
		// Step 3. Square the deviation values in the deviation Vector.
		
		for( int i = 0; i < deviationVector.size(); i++ ) {
			deviationVector.set(i,  deviationVector.get(i) * deviationVector.get(i) );
		}
		
		// Step 4. Compute the variance, which is the average of the squared deviation.
		Double devSum = 0d;
		for( Double val : deviationVector ) {
			devSum += val;
		}
		
		Double variance = devSum / n;
		
		return variance;
	}
	
	/**
	 * Helper function.
	 * @see {@link #computeVariance(Double[])}
	 */
	public static double computeVariance(Vector<Double> similarityValues) {
		return computeVariance(similarityValues.toArray(new Double[0]));
	}
}
