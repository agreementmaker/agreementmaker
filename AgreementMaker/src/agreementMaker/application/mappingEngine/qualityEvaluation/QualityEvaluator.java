package agreementMaker.application.mappingEngine.qualityEvaluation;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AlignmentMatrix;

public class QualityEvaluator {
	
	public static QualityEvaluationData evaluate(AbstractMatcher matcher) {
		return myEvaluate(matcher);
	}

	private static QualityEvaluationData myEvaluate(AbstractMatcher matcher) {
		QualityEvaluationData q = new QualityEvaluationData();
		q.setLocal(true);
		q.setLocalForSource(true);
		if(matcher.getMaxSourceAlign() < matcher.getMaxTargetAlign()) {
			q.setLocalForSource(false);
		}
		if(matcher.areClassesAligned()) {
			double[] measures = evaluateMatrix(matcher.getClassesMatrix(), q.isLocalForSource());
			q.setLocalClassMeasures(measures);
		}
		else {
			double[] measures = evaluateMatrix(matcher.getPropertiesMatrix(), q.isLocalForSource());
			q.setLocalPropMeasures(measures);
		}
		return q;
	}

	private static double[] evaluateMatrix(AlignmentMatrix matrix, boolean localForSource) {
		double[] res;
		if(localForSource) {
			res = new double[matrix.getRows()];
		}
		else {
			res = new double[matrix.getColumns()];
		}
		for(int i = 0; i < res.length; i++) {
			res[i] = Math.random();
		}
		return res;
	}
}
	