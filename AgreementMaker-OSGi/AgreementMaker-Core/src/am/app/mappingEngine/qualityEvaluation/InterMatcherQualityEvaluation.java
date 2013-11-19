package am.app.mappingEngine.qualityEvaluation;

import am.app.mappingEngine.AbstractMatcher;

/**
 * 
 * This interface allows the quality metric to evaluate a matcher based on all
 * the matchers to be combined.
 * 
 * @author frank
 * 
 */
public interface InterMatcherQualityEvaluation extends QualityMetric {
	
	public QualityEvaluationData getQuality( AbstractMatcher matcher, AbstractMatcher[] matcherList ) throws Exception;
}
