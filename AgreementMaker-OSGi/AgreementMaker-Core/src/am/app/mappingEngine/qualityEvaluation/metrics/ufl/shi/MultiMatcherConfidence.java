package am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public class MultiMatcherConfidence extends AbstractQualityMetric{
		
	private List<AbstractMatcher> initialMatcher;
	private double[] matchersWeight;
	private final double threshold=0.4;
	
	public MultiMatcherConfidence(List<AbstractMatcher> am, double[] weights)
	{
		super();
		this.initialMatcher=am;
		this.matchersWeight=weights;

	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		
		double q=0;
		for(int k=0;k<initialMatcher.size();k++)
		{
			SimilarityMatrix sm=type.equals(alignType.aligningClasses)?initialMatcher.get(k).getClassesMatrix():initialMatcher.get(k).getPropertiesMatrix();
			q+=matchersWeight[k]*Math.abs(threshold-sm.getSimilarity(i, j));
		}
		return q;
		
	}
}
