package am.app.mappingEngine.qualityEvaluation.metrics.ufl;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import static am.Utility.IntArray.getMaxValue;

/**
 * A mapping quality metric that sum the similarity values in the row
 * and column of this mapping.
 * 
 * @author Francesco Loprete
 * @author Cosmin Stroe
 */
public class CrossSumQuality extends AbstractQualityMetric {
		
	private double[] rowCounts;
	private double[] colCounts;
	//SimilarityMatrix forbidden;
	private double normalizationFactor;
	
	public CrossSumQuality(SimilarityMatrix matrix)
	{
		super();
		//this.forbidden=forbidden;
		// row counts
		rowCounts = new double[matrix.getRows()];
		for( int i = 0; i < matrix.getRows(); i++ ) {
			rowCounts[i] = sumMappingsSimilarity(matrix.getRowMaxValues(i, matrix.getColumns()));
		}
		
		// column counts
		colCounts = new double[matrix.getColumns()];
		for( int j = 0; j < matrix.getColumns(); j++ ) {
			colCounts[j] = sumMappingsSimilarity(matrix.getColMaxValues(j, matrix.getRows()));
		}
		
		normalizationFactor = getMaxValue(rowCounts) + getMaxValue(colCounts);
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		
		return (rowCounts[i] + colCounts[j]) / normalizationFactor;
	}

	private double sumMappingsSimilarity(Mapping[] map) 
	{
		double sum = 0.0;
		for(Mapping m : map)
		{
				sum+=m.getSimilarity();
		}
		return sum;
	}
}
