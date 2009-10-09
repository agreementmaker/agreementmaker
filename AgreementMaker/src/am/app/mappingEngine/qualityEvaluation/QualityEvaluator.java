package am.app.mappingEngine.qualityEvaluation;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;

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
	
	public static QualityEvaluationData evaluate(AbstractMatcher matcher, String quality) {
		QualityEvaluationData finalData = null;
		QualityEvaluationData localConfData  = null;
		QualityEvaluationData globalConfData = null;
		QualityEvaluationData distData = null;

		//LOCAL GLOBAL confidence without considering theshold
		if(quality.equals(LOCALCONFIDENCE) || quality.equals(GLOBALCONFIDENCE)){
			//in all 2 cases i have to calculate local first
			localConfData = LocalConfidenceQuality.getQuality(matcher, false);
			finalData = localConfData;
			//then global is the average of locals
			if(quality.equals(GLOBALCONFIDENCE) ){
				globalConfData = new QualityEvaluationData();
				double[] localClassQualities = localConfData.getLocalClassMeasures();
				double[] localPropQualities = localConfData.getLocalPropMeasures();
				
				//When we use the this quality as weight, when is 0 it doesn't count
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
		}
			
		//LOCAL GLOBAL confidence considering th NOT USED BY THE SYSTEM NOW
		if(quality.equals(LOCALTHRESHOLDCONFIDENCE) || quality.equals(GLOBALTHRESHOLDCONFIDENCE) ){
			//in all 2 cases i have to calculate local first
			localConfData = LocalConfidenceQuality.getQuality(matcher, true);
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
		}
		
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
			JoslynStructuralQuality evaluator = new JoslynStructuralQuality(matcher, distance, preservation, upper);
			distData = evaluator.getQuality();
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
	