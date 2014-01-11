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
	
	public RevalidationRate(SparseMatrix matrixPos, SparseMatrix matrixNeg)
	{
		super();
		int sum=0;
		metricResults=new SparseMatrix(matrixPos);
			
		int maxRepetition=(int)(matrixPos.getMaxValue()+matrixNeg.getMaxValue());
		for(int i=0;i<matrixPos.getRows();i++)
		{
			for(int j=0;j<matrixPos.getColumns();j++)
			{
				sum=(int)(matrixPos.getSimilarity(i, j)+matrixNeg.getSimilarity(i, j));
				metricResults.setSimilarity(i, j, sum/maxRepetition);
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