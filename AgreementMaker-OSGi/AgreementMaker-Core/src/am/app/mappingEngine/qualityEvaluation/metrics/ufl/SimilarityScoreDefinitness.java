package am.app.mappingEngine.qualityEvaluation.metrics.ufl;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

/**
 * A mapping quality metric that counts how many non-zero values are in the row
 * and column of this mapping.
 * 
 * @author Francesco Loprete
 * @author Cosmin Stroe
 */

public class SimilarityScoreDefinitness extends AbstractQualityMetric {
		
	private SimilarityMatrix matrix;
	
	public SimilarityScoreDefinitness(SimilarityMatrix matrix)
	{
		super();
		this.matrix = matrix;
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{	
		double sim = Math.abs(matrix.getSimilarity(i, j) - 0.5);
		sim = sim * 2;
		return sim;
	}
}
