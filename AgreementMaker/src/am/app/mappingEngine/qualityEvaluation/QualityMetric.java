package am.app.mappingEngine.qualityEvaluation;

import am.app.mappingEngine.AbstractMatcher;
import am.utility.parameters.AMParameter;
import am.utility.parameters.AMParameterSet;

public interface QualityMetric {

	public void setParameter( AMParameter param );
	public void setParameters( AMParameterSet params );
	
	public QualityEvaluationData getQuality( AbstractMatcher matcher ) throws Exception;
	
	public QualityEvaluationData getQuality( AbstractMatcher matcher, AMParameterSet params ) throws Exception;
	
}
