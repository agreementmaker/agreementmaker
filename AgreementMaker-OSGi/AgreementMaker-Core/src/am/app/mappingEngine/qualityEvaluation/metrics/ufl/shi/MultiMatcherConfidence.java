package am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public class MultiMatcherConfidence extends AbstractQualityMetric{
		
	private List<AbstractMatcher> initialMatcher;
	private double[] matchersWeight;
	private double threshold;
	
	public MultiMatcherConfidence(List<AbstractMatcher> am, double[] weights, double threshold)
	{
		super();
		this.initialMatcher=am;
		this.matchersWeight=weights;
		this.threshold=threshold;
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		
		double min=Double.MAX_VALUE;
		double q=0;
		for(int k=0;k<initialMatcher.size();k++)
		{
			SimilarityMatrix sm=type.equals(alignType.aligningClasses)?initialMatcher.get(k).getClassesMatrix():initialMatcher.get(k).getPropertiesMatrix();
			//q+=matchersWeight[k]*Math.abs(threshold-sm.getSimilarity(i, j));
			q=Math.abs(threshold-sm.getSimilarity(i, j));
			if (q<min)
				min=q;
		}
		return min;
		
	}
}
