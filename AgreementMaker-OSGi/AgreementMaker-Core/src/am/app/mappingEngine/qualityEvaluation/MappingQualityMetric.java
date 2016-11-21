package am.app.mappingEngine.qualityEvaluation;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.utility.parameters.HasParameters;

public interface MappingQualityMetric extends HasParameters {
	
	double getQuality(alignType type, int i, int j);
	
	QualityEvaluationData getQuality( AbstractMatcher matcher ) throws Exception;
	
}
