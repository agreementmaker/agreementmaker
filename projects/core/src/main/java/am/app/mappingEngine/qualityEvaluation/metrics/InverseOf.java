package am.app.mappingEngine.qualityEvaluation.metrics;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.MappingQualityMetric;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.utility.parameters.AMParameter;
import am.utility.parameters.AMParameterSet;

/**
 * A metric that returns the inverse of another metric.
 * This is done by returning 1 - metric value.
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

	/**
	 * FIXME: Figure out how to modify QualityEvaluationData to return the
	 * inverse metric values.
	 */
	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher)
			throws Exception {
		throw new RuntimeException("Not implemented yet.");
	}

}
