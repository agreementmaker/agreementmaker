package am.app.mappingEngine.qualityEvaluation.metrics;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.qualityEvaluation.QualityMetric;
import am.app.mappingEngine.qualityEvaluation.metrics.joslyn.JoslynStructuralQuality;
import am.utility.parameters.AMParameter;
import am.utility.parameters.AMParameterSet;

public class LowerDistancePreservationQM extends AbstractQualityMetric {

	@Override public String getNameString() { return "Lower Distance Preservation"; }
	
	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher)
			throws Exception {

		JoslynStructuralQuality joslynQM = new JoslynStructuralQuality();
		
		joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_UPPER_DISTANCE, false));
		joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_USE_DISTANCE, true));
		joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_USE_PRESERVATION, true));
		
		return joslynQM.getQuality(matcher);
	}


}
