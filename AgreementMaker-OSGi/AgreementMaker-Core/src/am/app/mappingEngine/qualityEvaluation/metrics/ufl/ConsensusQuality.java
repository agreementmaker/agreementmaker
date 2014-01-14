package am.app.mappingEngine.qualityEvaluation.metrics.ufl;
/**
 * A mapping quality metric that counts how many non-zero values are in the row
 * and column of this mapping.
 * 
 * @author Francesco Loprete
 * @author Cosmin Stroe
 */

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;

public class ConsensusQuality extends AbstractQualityMetric {
		
	/** The matrix of positive user validations */
	private SparseMatrix positiveMatrix;
	
	/** The matrix of negative user validations */
	private SparseMatrix negativeMatrix;
	
	//private final int maxRevalidation=5;
	
	public ConsensusQuality(SparseMatrix matrixPos, SparseMatrix matrixNeg)
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
		
		double max=(Math.max(positiveMatrix.getMaxValue(), negativeMatrix.getMaxValue()));

		return Math.min((max-numPos)/max, (max-numNeg)/max);
	}
}
