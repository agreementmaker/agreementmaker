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

public class SimilarityScoreHardnest extends AbstractQualityMetric {
		
	SimilarityMatrix metricResults;
	
	public SimilarityScoreHardnest(SimilarityMatrix matrix)
	{
		super();
		double sim=0;
		metricResults=matrix.clone();
			
		for(int i=0;i<matrix.getRows();i++)
		{
			for(int j=0;j<matrix.getColumns();j++)
			{
				sim=Math.abs(matrix.getSimilarity(i, j)-0.5);
				sim=sim*2;
				metricResults.setSimilarity(i, j, sim);
			}
		}
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		
		return metricResults.getSimilarity(i, j);
	}
}
