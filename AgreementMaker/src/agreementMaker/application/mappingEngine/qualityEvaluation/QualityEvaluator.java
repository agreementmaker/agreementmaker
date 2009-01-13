package agreementMaker.application.mappingEngine.qualityEvaluation;

import java.util.ArrayList;

import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AlignmentMatrix;

public class QualityEvaluator {
	
	//LOCAL QUALITIES
	public final static String  LOCALCONFIDENCE = "Local confidence";
	
	//GLOBAL QUALITIES
	public final static String  GLOBALCONFIDENCE = "Global confidence";
	public final static String DISTANCE = "Distance Preservation";
	public final static String ORDER = "Order Preservation";
	
	//TEST QUALITIES
	//it's a try to see the difference
	public final static String GLOBALTHRESHOLDCONFIDENCE = "Global confidence considering threshold";
	public final static String LOCALTHRESHOLDCONFIDENCE = "Local confidence considering threshold";
	
	//LIST USED IN THE QUALITY COMBINATION MATCHER 
	public final static String[] QUALITIES = {LOCALCONFIDENCE, GLOBALCONFIDENCE, DISTANCE,ORDER};
	//LIST USED IN THE QUALITY EVALUATION
	public final static String[] ONLYGLOBAL = {GLOBALCONFIDENCE, DISTANCE, ORDER};
	
	public static QualityEvaluationData evaluate(AbstractMatcher matcher, String quality) {
		QualityEvaluationData qData = null;
		
		//LOCAL GLOBAL confidence without considering theshold
		if(quality.equals(LOCALCONFIDENCE) || quality.equals(GLOBALCONFIDENCE)){
			//in all 2 cases i have to calculate local first
			qData = LocalConfidenceQuality.getQuality(matcher, false);
			//then global is the average of locals
			if(quality.equals(GLOBALCONFIDENCE)) {
				
				double[] localClassQualities = qData.getLocalClassMeasures();
				double[] localPropQualities = qData.getLocalPropMeasures();
				
				//When we use the this quality as weight, when is 0 it doesn't count
				double classAverage = Utility.getAverageOfArrayNonZeroValues(localClassQualities);
				double propAverage = Utility.getAverageOfArrayNonZeroValues(localPropQualities);
				//then global is the average of locals
				qData.setLocal(false);
				if(matcher.areClassesAligned()) {
					qData.setGlobalClassMeasure(classAverage);
				}
				if(matcher.arePropertiesAligned()) {
					qData.setGlobalPropMeasure(propAverage);
				}
			}
		}
			
		//LOCAL GLOBAL confidence considering th
		if(quality.equals(LOCALTHRESHOLDCONFIDENCE) || quality.equals(GLOBALTHRESHOLDCONFIDENCE)){
			//in all 2 cases i have to calculate local first
			qData = LocalConfidenceQuality.getQuality(matcher, true);
			//then global is the average of locals
			if(quality.equals(GLOBALTHRESHOLDCONFIDENCE)) {
				
				double[] localClassQualities = qData.getLocalClassMeasures();
				double[] localPropQualities = qData.getLocalPropMeasures();
				double classAverage = Utility.getAverageOfArrayNonZeroValues(localClassQualities);
				double propAverage = Utility.getAverageOfArrayNonZeroValues(localPropQualities);
				//then global is the average of locals
				qData.setLocal(false);
				if(matcher.areClassesAligned()) {
					qData.setGlobalClassMeasure(classAverage);
				}
				if(matcher.arePropertiesAligned()) {
					qData.setGlobalPropMeasure(propAverage);
				}
			}
		}
		
		//JOslyn structural qualities
		if(quality.equals(DISTANCE) || quality.equals(ORDER)) {
			JoslynStructuralQuality evaluator = new JoslynStructuralQuality(matcher, quality);
			qData = evaluator.getQuality();
		}
			
			//OTHER QUALITIES TO BE ADDED
		return qData;
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
	