package am.app.mappingEngine.qualityEvaluation.metrics;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.qualityEvaluation.metrics.joslyn.JoslynStructuralQuality;
import am.utility.parameters.AMParameter;

public class OrderDiscrepancyQM extends AbstractQualityMetric {

	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher)
			throws Exception {

		JoslynStructuralQuality joslynQM = new JoslynStructuralQuality();
		
		joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_USE_DISTANCE, false));
		joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_USE_PRESERVATION, false));
		joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_UPPER_DISTANCE, true));
		
		return joslynQM.getQuality(matcher);
	}


}