package am.app.mappingEngine.qualityEvaluation.metrics;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.MappingQualityMetric;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.utility.parameters.AMParameter;
import am.utility.parameters.AMParameterSet;

/**
 * Returns the inverse of a quality metric.  This is the same as returning 1 - value.
 * 
 * @author cosmin
 */
public class InverseOf implements MappingQualityMetric {

	private MappingQualityMetric metric;
	
	public InverseOf(MappingQualityMetric metric) {
		this.metric = metric;
	}
	
	@Override
	public void setParameter(AMParameter param) {
		metric.setParameter(param);
	}

	@Override
	public void setParameters(AMParameterSet params) {
		metric.setParameters(params);
	}

	@Override
	public double getQuality(alignType type, int i, int j) {
		return 1d - metric.getQuality(type, i, j);
	}

	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher)
			throws Exception {
		throw new RuntimeException("Not implemented yet.");
	}

}
