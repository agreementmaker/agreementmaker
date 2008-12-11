package agreementMaker.application.mappingEngine.qualityEvaluation;

import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AlignmentMatrix;

public class QualityEvaluator {
	
	public final static String  LOCAL = "AM Local Quality";
	public final static String  GLOBAL = "AM Global Quality";
	public final static String COMBINED = "Combined Quality: average between Global and Local";
	
	public static QualityEvaluationData evaluate(AbstractMatcher matcher, String quality, int maxSourceRelations, int maxTargetRelations) {
		QualityEvaluationData qData = null;
		if(quality.equals(LOCAL) || quality.equals(GLOBAL)  || quality.equals(COMBINED) ){
			//in all tree cases i have to calculate local first
			qData = localQuality(matcher,maxSourceRelations,maxTargetRelations);
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

	protected static QualityEvaluationData localQuality(AbstractMatcher matcher, int maxSourceRelations, int maxTargetRelations) {
		QualityEvaluationData q = new QualityEvaluationData();
		int numOfRelations = maxSourceRelations;
		q.setLocalForSource(true);
		if( maxSourceRelations > maxTargetRelations) {
			q.setLocalForSource(false);
			numOfRelations = maxTargetRelations;
		}
		if(matcher.areClassesAligned()) {
			double[] measures = evaluateMatrix(matcher.getClassesMatrix(), q.isLocalForSource(),numOfRelations );
			q.setLocalClassMeasures(measures);
		}
		else {
			double[] measures = evaluateMatrix(matcher.getPropertiesMatrix(), q.isLocalForSource(),numOfRelations);
			q.setLocalPropMeasures(measures);
		}
		return q;
	}
	

	private static double[] evaluateMatrix(AlignmentMatrix matrix, boolean localForSource, int numRelations) {
		//if sourcerelations are less then targetrelation the matrix will be scanned for row or column.
		//for each node (row or column) the quality will be calculated with this formula:
		// avg(numRelations best similarities of that nodes) - avg(similarities not choosen for that node) 
		/*
		double[] localMeasure;
		int size;
		if(localForSource) {
			size = matrix.getRows();
			localMeasure = new double[size];
		}
		else {
			size = matrix.getColumns();
			localMeasure = new double[size];
		}
		if(numRelations == AbstractMatcher.ANY_INT)
			numRelations = size;
		double[][] matrix2 = new double[10][10];
		double[] bo = matrix2[0];
		double[] bo = matrix[][0];
		for(int i = 0; i < res.length; i++) {
			res[i] = Math.random();
		}
		*/
		return null;
	}
}
	