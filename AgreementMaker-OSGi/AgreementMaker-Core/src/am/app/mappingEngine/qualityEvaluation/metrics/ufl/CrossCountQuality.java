package am.app.mappingEngine.qualityEvaluation.metrics.ufl;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

/**
 * A mapping quality metric that counts how many non-zero values are in the row
 * and column of this mapping.
 * 
 * @author Francesco Loprete
 * @author Cosmin Stroe
 */
public class CrossCountQuality extends AbstractQualityMetric {
	
	// weight for the Uncertain Mappings discovered in the system
	private final double weight_um = 0.5d;

	private SimilarityMatrix matrix;
	
	public CrossCountQuality(SimilarityMatrix matrix) {
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
		double weight = 0.0d;
		
		weight += weight_um * countNonzeroMappings( matrix.getColMaxValues(j, matrix.getRows()) );
		weight += weight_um * countNonzeroMappings( matrix.getRowMaxValues(i, matrix.getColumns()) );
		
		return weight;
	}

	private int countNonzeroMappings(Mapping[] map) 
	{
		int count = 0;
		for(Mapping m : map){
			if( m.getSimilarity() > 0.0 ) count++;
		}
		return count;
	}
}
