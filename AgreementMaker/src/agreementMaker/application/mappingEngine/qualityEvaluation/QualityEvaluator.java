package agreementMaker.application.mappingEngine.qualityEvaluation;

import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AlignmentMatrix;

public class QualityEvaluator {
	
		
	public final static String  LOCALCONFIDENCE = "Local confidence";
	public final static String LOCALTHRESHOLDCONFIDENCE = "Local confidence considering threshold";
	public final static String  GLOBALCONFIDENCE = "Global confidence";
	public final static String GLOBALTHRESHOLDCONFIDENCE = "Global confidence considering threshold";
	public final static String DISTANCE = "Global Distance Preservation";
	public final static String ORDER = "Global Order Preservation";
	
	public final static String[] QUALITIES = {LOCALCONFIDENCE, LOCALTHRESHOLDCONFIDENCE, GLOBALCONFIDENCE,GLOBALTHRESHOLDCONFIDENCE, DISTANCE,ORDER};
	
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
				double classAverage = Utility.getAverageOfArray(localClassQualities);
				double propAverage = Utility.getAverageOfArray(localPropQualities);
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
				double classAverage = Utility.getAverageOfArray(localClassQualities);
				double propAverage = Utility.getAverageOfArray(localPropQualities);
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

}
	