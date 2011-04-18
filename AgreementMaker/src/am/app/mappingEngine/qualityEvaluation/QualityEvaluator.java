package am.app.mappingEngine.qualityEvaluation;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.qualityEvaluation.metrics.GlobalConfidenceQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.LocalConfidenceQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.joslyn.JoslynStructuralQuality;
import am.utility.parameters.AMParameter;

public class QualityEvaluator {
	
	//LOCAL QUALITIES
	public final static String  LOCALCONFIDENCE = "Local confidence";
	
	//GLOBAL QUALITIES
	public final static String  GLOBALCONFIDENCE = "Global confidence";
	public final static String LOWER_DISTANCE = "Lower Distance Preservation";
	public final static String LOWER_DISTANCE_DISCREPANCY = "Lower Distance discrepancy";
	public final static String UPPER_DISTANCE = "Upper Distance Preservation";
	public final static String UPPER_DISTANCE_DISCREPANCY = "Upper Distance discrepancy";
	public final static String ORDER = "Order Preservation";
	public final static String ORDER_DISCREPANCY = "Order Discrepancy";
	//TEST QUALITIES
	//it's a try to see the difference
	public final static String GLOBALTHRESHOLDCONFIDENCE = "Global confidence considering threshold";
	public final static String LOCALTHRESHOLDCONFIDENCE = "Local confidence considering threshold";
	
	//LIST USED IN THE QUALITY COMBINATION MATCHER 
	public final static String[] QUALITIES = {LOCALCONFIDENCE, GLOBALCONFIDENCE, UPPER_DISTANCE, LOWER_DISTANCE,ORDER, UPPER_DISTANCE_DISCREPANCY, LOWER_DISTANCE_DISCREPANCY, ORDER_DISCREPANCY};
	
	public static QualityEvaluationData evaluate(AbstractMatcher matcher, String quality) throws Exception {
		QualityEvaluationData finalData = null;
		QualityEvaluationData localConfData  = null;
		QualityEvaluationData globalConfData = null;
		QualityEvaluationData distData = null;

		if( quality.equals(LOCALCONFIDENCE) ) {
			QualityMetric localQ = new LocalConfidenceQuality();
			localQ.setParameter(new AMParameter(LocalConfidenceQuality.PARAM_CONSIDER_THRESHOLD, false));
			return localQ.getQuality(matcher);
		}
		
		//LOCAL GLOBAL confidence without considering theshold
		if(quality.equals(GLOBALCONFIDENCE)){
			GlobalConfidenceQuality globalQ = new GlobalConfidenceQuality();
			globalQ.setParameter(new AMParameter(GlobalConfidenceQuality.PARAM_CONSIDER_THRESHOLD, false));
			return globalQ.getQuality(matcher);
		}
			
		/*//LOCAL GLOBAL confidence considering th NOT USED BY THE SYSTEM NOW
		if(quality.equals(LOCALTHRESHOLDCONFIDENCE) || quality.equals(GLOBALTHRESHOLDCONFIDENCE) ){
			//in all 2 cases i have to calculate local first
			LocalConfidenceQuality localQ = new LocalConfidenceQuality();
			localQ.setParameter(new AMParameter(LocalConfidenceQuality.PARAM_CONSIDER_THRESHOLD, true));
			localConfData = localQ.getQuality(matcher);
			finalData = localConfData;
			//then global is the average of locals
			if(quality.equals(GLOBALTHRESHOLDCONFIDENCE)) {
				globalConfData = new QualityEvaluationData();
				double[] localClassQualities = localConfData.getLocalClassMeasures();
				double[] localPropQualities = localConfData.getLocalPropMeasures();
				double classAverage = Utility.getAverageOfArrayNonZeroValues(localClassQualities);
				double propAverage = Utility.getAverageOfArrayNonZeroValues(localPropQualities);
				//then global is the average of locals
				globalConfData.setLocal(false);
				if(matcher.areClassesAligned()) {
					globalConfData.setGlobalClassMeasure(classAverage);
				}
				if(matcher.arePropertiesAligned()) {
					globalConfData.setGlobalPropMeasure(propAverage);
				}
				finalData = globalConfData;
			}
		}*/
		
		//JOslyn structural qualities
		if(quality.equals(LOWER_DISTANCE) || 
		   quality.equals(UPPER_DISTANCE) || 
		   quality.equals(LOWER_DISTANCE_DISCREPANCY) || 
		   quality.equals(UPPER_DISTANCE_DISCREPANCY) ||
		   quality.equals(ORDER) ||
		   quality.equals(ORDER_DISCREPANCY)) {
			
			boolean distance = true;
			if(quality.equals(ORDER) || quality.equals(ORDER_DISCREPANCY))
				distance = false;
			boolean preservation = true;
			if(quality.equals(LOWER_DISTANCE_DISCREPANCY) || quality.equals(UPPER_DISTANCE_DISCREPANCY) || quality.equals(ORDER_DISCREPANCY))
				preservation = false;
			boolean upper = true;
			if(quality.equals(LOWER_DISTANCE) || quality.equals(LOWER_DISTANCE_DISCREPANCY))
				upper = false;

			JoslynStructuralQuality joslynQM = new JoslynStructuralQuality();
			joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_UPPER_DISTANCE, upper));
			joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_USE_DISTANCE, distance));
			joslynQM.setParameter(new AMParameter(JoslynStructuralQuality.PREF_USE_PRESERVATION, preservation));
			
			distData = joslynQM.getQuality(matcher);
			finalData = distData;
		}
			
		//OTHER QUALITIES TO BE ADDED
		return finalData;
	}

	
	/**
	 * VERY IMPORTANT this method merge any number of qualities and they can be either global or local
	 * BUT if one of the quality is local and is localForSource then all other local qualities involved must be localforsource
	 * for example LOCAL confidence is local for source if the maxNumOfSourceRelations is lower then maxNumOfTargetRelations
	 * if you combine local confidence with any other local quality that quality must assign the locality same way
	 * @param listQualities
	 * @return
	 */
	public static QualityEvaluationData mergeQualities(
			 QualityEvaluationData first, QualityEvaluationData second) {
		
		QualityEvaluationData result = new QualityEvaluationData();
		
		if(first.isLocal() && second.isLocal()) { //both locals
			if(first.isLocalForSource() != second.isLocalForSource()) {
				throw new RuntimeException("Developer error, you are merging two local qualities which have a different way of defining if the quality is local for source or target");
			}
			result.setLocal(true);
			result.setLocalForSource(first.isLocalForSource());
			result.setLocalClassMeasures(Utility.avgArrays(first.getLocalClassMeasures(), second.getLocalClassMeasures()));
			result.setLocalPropMeasures(Utility.avgArrays(first.getLocalPropMeasures(), second.getLocalPropMeasures()));
		}
		else if(first.isLocal()) { //only first local
			result.setLocal(true);
			result.setLocalForSource(first.isLocalForSource());
			result.setLocalClassMeasures(Utility.avgArrayAndDouble(first.getLocalClassMeasures(), second.getGlobalClassMeasure()));
			result.setLocalPropMeasures(Utility.avgArrayAndDouble(first.getLocalPropMeasures(), second.getGlobalPropMeasure()));
		}
		else if(second.isLocal()) { //only second local
			result.setLocal(true);
			result.setLocalForSource(second.isLocalForSource());
			result.setLocalClassMeasures(Utility.avgArrayAndDouble(second.getLocalClassMeasures(), first.getGlobalClassMeasure()));
			result.setLocalPropMeasures(Utility.avgArrayAndDouble(second.getLocalPropMeasures(), first.getGlobalPropMeasure()));
		}
		else { //all globals
			result.setLocal(false);
			result.setGlobalClassMeasure(first.getGlobalClassMeasure() / second.getGlobalClassMeasure());
			result.setGlobalPropMeasure(first.getGlobalPropMeasure() / second.getGlobalPropMeasure());
		}
		
		return result;
		
	}

}
	