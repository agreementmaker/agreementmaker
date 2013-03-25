/**    ________________________________________
 * ___/ Copyright Notice / Warranty Disclaimer \________________
 *
 * @copyright { Copyright (c) 2010
 * Advances in Information Systems Laboratory at the
 * University of Illinois at Chicago
 * All Rights Reserved. }
 * 
 * @disclaimer {
 * This work is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. }
 * 
 *     _____________________
 * ___/ Authors / Changelog \___________________________________          
 * 
 *  
 */

package am.app.mappingEngine.qualityEvaluation;

import am.app.mappingEngine.qualityEvaluation.metrics.GlobalConfidenceQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.LocalConfidenceQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.LowerDistanceDiscrepancyQM;
import am.app.mappingEngine.qualityEvaluation.metrics.LowerDistancePreservationQM;
import am.app.mappingEngine.qualityEvaluation.metrics.OrderDiscrepancyQM;
import am.app.mappingEngine.qualityEvaluation.metrics.OrderPreservationQM;
import am.app.mappingEngine.qualityEvaluation.metrics.UpperDistanceDiscrepancyQM;
import am.app.mappingEngine.qualityEvaluation.metrics.UpperDistancePreservationQM;

/**
 * The Quality Metric Registry, used to plug in new quality metrics into LWC.
 */

public enum QualityMetricRegistry {

	LOCAL_CONFIDENCE	( "Local Confidence", LocalConfidenceQuality.class ), 
	GLOBAL_CONFIDENCE	( "Global Confidence", GlobalConfidenceQuality.class ),
	UPPER_DISTANCE		( "Upper Distance Preservation", UpperDistancePreservationQM.class ),
	LOWER_DISTANCE		( "Lower Distance Preservation", LowerDistancePreservationQM.class ),
	ORDER_PRESERVATION 	( "Order Preservation", OrderPreservationQM.class),
	ORDER_DISCREPANCY 	( "Order Discrepancy", OrderDiscrepancyQM.class),
	UPPER_DISTANCE_DISCREPANCY( "Upper Distance Discrepancy", UpperDistanceDiscrepancyQM.class),
	LOWER_DISTANCE_DISCREPANCY( "Lower Distance Discrepancy", LowerDistanceDiscrepancyQM.class);

	/* ------- Do not edit below this line -------- */
	
	private String name;
	private Class<? extends QualityMetric> className;
	
	private QualityMetricRegistry(String name, Class<? extends QualityMetric> cls) {
		this.name = name;
		this.className = cls;
	}
	
	public Class<? extends QualityMetric> getQMClass() { return className; }
	public String toString() { return name; } 
	
	public QualityMetricRegistry getRegistryEntry(String name) {
		for( QualityMetricRegistry qm : values() ) {
			if( qm.toString().equals(name) ) return qm;
		}
		return null;
	}
}
