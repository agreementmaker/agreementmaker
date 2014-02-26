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
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
public class ExpandedConsensus extends AbstractQualityMetric {
		
	/** The matrix of positive user validations */
	private SparseMatrix positiveMatrix;
	
	/** The matrix of negative user validations */
	private SparseMatrix negativeMatrix;
	private SimilarityMatrix amScoreClass;
	private SimilarityMatrix amScoreProp;
	private double threshold;
	private int maxRevalidation;
	
	public ExpandedConsensus(SparseMatrix matrixPos, SparseMatrix matrixNeg, SimilarityMatrix amClass, SimilarityMatrix amProp  , int revalidation, double threshold)
	{
		super();
		this.positiveMatrix = matrixPos;
		this.negativeMatrix = matrixNeg;
		this.maxRevalidation=revalidation;
		this.amScoreClass=amClass;
		this.amScoreProp=amProp;
		this.threshold=threshold;
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
		int maxConsensus=(maxRevalidation/2)+1;
		SimilarityMatrix am=amScoreClass;
		
		if (numPos+numNeg==1)
		{
			if (type.equals(alignType.aligningProperties))
				am=amScoreProp;
			if (am.getSimilarity(i, j)>threshold)
				numPos++;
			else
				numNeg++;
		}

		if (numPos==numNeg)
			return 0.0d;
		if ((numPos==maxConsensus) || (numNeg==maxConsensus))
			return 1.0d;

		return (double)(Math.abs(numPos-numNeg))/maxConsensus;
		
	}
	
}
