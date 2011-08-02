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

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.utility.parameters.AMParameter;

public class GlobalConfidenceQuality extends AbstractQualityMetric {

	public static final String PARAM_CONSIDER_THRESHOLD = "CONSIDER_THRESHOLD";
	
	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher)
			throws Exception {
		
		LocalConfidenceQuality localQ = new LocalConfidenceQuality();
		localQ.setParameter(new AMParameter( LocalConfidenceQuality.PARAM_CONSIDER_THRESHOLD, 
											 params.containsKey(PARAM_CONSIDER_THRESHOLD) && params.getBit(PARAM_CONSIDER_THRESHOLD)));
		QualityEvaluationData localConfData = localQ.getQuality(matcher);

		QualityEvaluationData globalConfData = new QualityEvaluationData();

		double[] localClassQualities = localConfData.getLocalClassMeasures();
		double[] localPropQualities = localConfData.getLocalPropMeasures();
			
		//When we use the this quality as weight, when is 0 it doesn't count
		double classAverage = Utility.getAverageOfArrayNonZeroValues(localClassQualities);
		double propAverage = Utility.getAverageOfArrayNonZeroValues(localPropQualities);

		//then global is the average of locals
		globalConfData.setLocal(false);
		if(matcher.areClassesAligned()) { globalConfData.setGlobalClassMeasure(classAverage); }
		if(matcher.arePropertiesAligned()) { globalConfData.setGlobalPropMeasure(propAverage); }
		
		return globalConfData;
	}

}
