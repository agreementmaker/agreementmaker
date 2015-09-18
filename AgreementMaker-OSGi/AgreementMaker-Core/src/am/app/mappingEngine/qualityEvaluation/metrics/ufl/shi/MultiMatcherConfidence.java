package am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi;

import java.util.Arrays;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public class MultiMatcherConfidence extends AbstractQualityMetric{
		
	private List<MatchingTask> initialMatcher;
	private double[] matchersWeight;
	private double threshold;
	
	public MultiMatcherConfidence(List<MatchingTask> am, double threshold) {
		super();
		this.initialMatcher = am;
		this.matchersWeight = new double[initialMatcher.size()];
		Arrays.fill(matchersWeight, 1.0);
		this.threshold = threshold;
	}
	
	public MultiMatcherConfidence(List<MatchingTask> am, double[] weights, double threshold)
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
		double sum=0.0;
		double q=0;
		for(int k=0;k<initialMatcher.size();k++)
		{
			SimilarityMatrix sm = type.equals(alignType.aligningClasses) ? initialMatcher.get(k).matcherResult.getClassesMatrix() : initialMatcher.get(k).matcherResult.getPropertiesMatrix();
		
			sum+=matchersWeight[k]*Math.abs(threshold-sm.getSimilarity(i, j));
		}
		return (sum/initialMatcher.size());
		
	}
}
