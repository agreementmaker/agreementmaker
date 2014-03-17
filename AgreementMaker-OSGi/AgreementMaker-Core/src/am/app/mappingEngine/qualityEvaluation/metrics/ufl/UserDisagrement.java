package am.app.mappingEngine.qualityEvaluation.metrics.ufl;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import static am.evaluation.disagreement.variance.VarianceComputation.computeVariance;

/**
 * A mapping quality metric that counts how many non-zero values are in the row
 * and column of this mapping.
 * 
 * @author Francesco Loprete
 * @author Cosmin Stroe
 */

public class UserDisagrement extends AbstractQualityMetric {
		
	/** The matrix of positive user validations */
	private SparseMatrix positiveMatrix;
	
	/** The matrix of negative user validations */
	private SparseMatrix negativeMatrix;
	
	public UserDisagrement(SparseMatrix matrixPos, SparseMatrix matrixNeg)
	{
		super();
		this.positiveMatrix = matrixPos;
		this.negativeMatrix = matrixNeg;
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		
		int numPos = (int)positiveMatrix.getSimilarity(i, j);
		int numNeg = (int)negativeMatrix.getSimilarity(i, j);
		
		int numValidations = numPos + numNeg;
		
		// create the validation vector
		double[] validationVector = new double[numValidations];
		for( int k = 0; k < numValidations; k++ ) {
			if( k < numPos ) 
				validationVector[k] = 1.0d;
			else
				validationVector[k] = 0.0d;
		}
		
		// The variance ranges from 0 to 0.25 because our maximum values range from 0 to 1.
		// We must multiply the variance by 4 to make the quality to be from 0 to 1.0.
		return 4 * computeVariance(validationVector);
	}
}
