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

		//return 1-Math.max(positiveMatrix.getSimilarity(i, j),negativeMatrix.getSimilarity(i, j))/maxConsensus;
		
		/*
		 * New impact metric
		 */
		int pos=(int)positiveMatrix.getSimilarity(i, j);
		int neg=(int)negativeMatrix.getSimilarity(i, j);
		if (pos==neg)
			return 1.0d;
		if ((pos==maxConsensus)||(neg==maxConsensus))
			return 0.0d;
		double numerator =Math.min(maxConsensus-pos, maxConsensus-neg);
		double denominator=Math.max(maxConsensus-pos, maxConsensus-neg);
		
		return numerator/denominator;
		
	}
	
	//OLD
//	@Override
//	public double getQuality(alignType type, int i, int j) 
//	{		
//
//		return Math.abs(positiveMatrix.getSimilarity(i, j)-negativeMatrix.getSimilarity(i, j))/maxConsensus;
//	}
}
