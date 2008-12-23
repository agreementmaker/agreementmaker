package agreementMaker.application.mappingEngine.qualityEvaluation;

import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AlignmentMatrix;

public class QualityEvaluator {
	
		
	public final static String  LOCAL = "AM Local Quality";
	public final static String  GLOBAL = "AM Global Quality";
	public final static String COMBINED = "Combined Quality: average between Global and Local";
	
	public final static String[] QUALITIES = {LOCAL,GLOBAL,COMBINED};
	
	public static QualityEvaluationData evaluate(AbstractMatcher matcher, String quality) {
		QualityEvaluationData qData = null;
		if(quality.equals(LOCAL) || quality.equals(GLOBAL)  || quality.equals(COMBINED) ){
			//in all tree cases i have to calculate local first
			qData = AMLocalQuality.getQuality(matcher);
			//then global is the average of locals
			//combined is the average of global and each local 
			if(quality.equals(GLOBAL) || quality.equals(COMBINED)) {
				
				double[] localClassQualities = qData.getLocalClassMeasures();
				double[] localPropQualities = qData.getLocalPropMeasures();
				double classAverage = Utility.getAverageOfArray(localClassQualities);
				double propAverage = Utility.getAverageOfArray(localPropQualities);
				//then global is the average of locals
				if(quality.equals(GLOBAL)) {
					if(matcher.areClassesAligned()) {
						for(int i = 0; i < localClassQualities.length; i++) {
							localClassQualities[i] = classAverage;
						}
					}
					if(matcher.arePropertiesAligned()) {
						for(int i = 0; i < localPropQualities.length; i++) {
							localPropQualities[i] = propAverage;
						}
					}
				}
				//combined is the average of global and each local 
				else if(quality.equals(COMBINED)) {
					if(matcher.areClassesAligned()) {
						for(int i = 0; i < localClassQualities.length; i++) {
							localClassQualities[i] = ( localClassQualities[i] + classAverage ) /2;
						}
					}
					if(matcher.arePropertiesAligned()) {
						for(int i = 0; i < localPropQualities.length; i++) {
							localPropQualities[i] =  ( localPropQualities[i] + propAverage ) /2;
						}
					}
				}
				qData.setLocalClassMeasures(localClassQualities);
				qData.setLocalPropMeasures(localPropQualities);
			}
		}
		return qData;
	}

}
	