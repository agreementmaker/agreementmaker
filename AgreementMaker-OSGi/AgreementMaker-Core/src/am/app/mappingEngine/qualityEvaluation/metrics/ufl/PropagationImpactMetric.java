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

public class PropagationImpactMetric extends  AbstractQualityMetric {
		
	/** The matrix of positive user validations */
	private SparseMatrix positiveMatrix;
	
	/** The matrix of negative user validations */
	private SparseMatrix negativeMatrix;
	
	private int maxConsensus;
	
	//private final int maxRevalidation=5;
	
	public PropagationImpactMetric (SparseMatrix matrixPos, SparseMatrix matrixNeg, int validation)
	{
		super();
		this.positiveMatrix = matrixPos;
		this.negativeMatrix = matrixNeg;
		this.maxConsensus=(validation/2)+1;
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		

		return 1-Math.max(positiveMatrix.getSimilarity(i, j),negativeMatrix.getSimilarity(i, j))/maxConsensus;
	}
	
	//OLD
//	@Override
//	public double getQuality(alignType type, int i, int j) 
//	{		
//
//		return Math.abs(positiveMatrix.getSimilarity(i, j)-negativeMatrix.getSimilarity(i, j))/maxConsensus;
//	}
}
