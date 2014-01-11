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

public class UserDisagrement extends AbstractQualityMetric {
		
	SparseMatrix metricResults;
	
	public UserDisagrement(SparseMatrix matrixPos, SparseMatrix matrixNeg)
	{
		metricResults=new SparseMatrix(matrixPos);
		double sim=0;
		int numPos=0;
		int numNeg=0;
		int row=matrixPos.getRows();
		int col=matrixPos.getColumns();
		for (int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				numPos=(int)matrixPos.getSimilarity(i, j);
				numNeg=(int)matrixNeg.getSimilarity(i, j);
				sim=0-5-(Math.abs(numPos-numNeg)*(0.5/(Math.min(numPos, numNeg)+1)));
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
