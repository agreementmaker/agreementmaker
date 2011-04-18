package am.app.mappingEngine.Combination;

import am.app.mappingEngine.qualityEvaluation.QualityMetric;
import am.app.mappingEngine.qualityEvaluation.metrics.GlobalConfidenceQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.LocalConfidenceQuality;

public enum QualityMetricRegistry {

	LOCALCONFIDENCE( LocalConfidenceQuality.class ), 
	GLOBALCONFIDENCE( GlobalConfidenceQuality.class ); 
/*	UPPER_DISTANCE, 
	LOWER_DISTANCE,
	ORDER, 
	UPPER_DISTANCE_DISCREPANCY, 
	LOWER_DISTANCE_DISCREPANCY, 
	ORDER_DISCREPANCY;
*/	
	private Class<? extends QualityMetric> className;
	
	private QualityMetricRegistry(Class<? extends QualityMetric> cls) {
		this.className = cls;
	}
	
}
