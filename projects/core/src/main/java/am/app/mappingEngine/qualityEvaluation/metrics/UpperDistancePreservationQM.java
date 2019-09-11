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

package am.app.mappingEngine.qualityEvaluation.metrics;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.qualityEvaluation.metrics.joslyn.JoslynStructuralQuality;
import am.utility.parameters.AMParameter;

public class UpperDistancePreservationQM extends AbstractQualityMetric {

	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher)
			throws Exception {

		JoslynStructuralQuality joslynQM = new JoslynStructuralQuality();
		
		joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_USE_DISTANCE, true));
		joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_USE_PRESERVATION, true));
		joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_UPPER_DISTANCE, true));
		
		return joslynQM.getQuality(matcher);
	}


}
