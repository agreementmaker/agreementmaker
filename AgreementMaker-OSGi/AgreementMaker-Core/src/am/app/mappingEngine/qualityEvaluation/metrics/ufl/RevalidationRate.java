package am.app.mappingEngine.qualityEvaluation.metrics.ufl;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;

/**
 * A mapping quality metric that counts how many non-zero values are in the row
 * and column of this mapping.
 * 
 * @author Francesco Loprete
 * @author Cosmin Stroe
 */

public class RevalidationRate extends AbstractQualityMetric {
		
	SparseMatrix metricResults;
	
	/** The matrix of positive user validations */
	private SparseMatrix positiveMatrix;
	
	/** The matrix of negative user validations */
	private SparseMatrix negativeMatrix;
	
	/** The number of validations for the most validated mapping */
	private final int maxRepetition;
	
	public RevalidationRate(SparseMatrix matrixPos, SparseMatrix matrixNeg)
	{
		super();
		this.positiveMatrix = matrixPos;
		this.negativeMatrix = matrixNeg;
		
		maxRepetition = (int) (matrixPos.getMaxValue() + matrixNeg.getMaxValue());
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{
		int validationCount = (int)(positiveMatrix.getSimilarity(i, j) + negativeMatrix.getSimilarity(i, j));
		return (validationCount / (double)maxRepetition);
	}
}