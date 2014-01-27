package am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public class Confidence extends AbstractQualityMetric{
		
	private SimilarityMatrix amScoreClass;
	private SimilarityMatrix amScoreProp;
	private final double threshold=0.4;
	
	public Confidence(SimilarityMatrix amClass, SimilarityMatrix amProp)
	{
		super();
		this.amScoreClass=amClass;
		this.amScoreProp=amProp;
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		

		SimilarityMatrix am=amScoreClass;
		if (type.equals(alignType.aligningProperties))
			am=amScoreProp;
		return Math.abs(threshold-am.getSimilarity(i, j));
		
	}
}
