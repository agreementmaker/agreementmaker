package am.app.mappingEngine.qualityEvaluation;

import am.app.mappingEngine.AbstractMatcher;
import am.utility.parameters.AMParameter;
import am.utility.parameters.AMParameterSet;


/**
 * This abstract class implements a QualityMetric and implements
 * trivially easy to implement methods, but leaves the implementation 
 * of the getQuality(AbstractMatcher) method to the superclasses.
 * 
 * @author Cosmin Stroe
 *
 */
public abstract class AbstractQualityMetric implements QualityMetric {

	protected AMParameterSet params = new AMParameterSet();
		
	@Override public abstract String getNameString();
	@Override public void setParameter(AMParameter param) { params.put(param); }
	@Override public void setParameters(AMParameterSet param) { params = param; }
	@Override public QualityEvaluationData getQuality(AbstractMatcher matcher, AMParameterSet params) throws Exception {
		setParameters(params);
		return getQuality(matcher);
	}
	
	@Override public abstract QualityEvaluationData getQuality(AbstractMatcher matcher) throws Exception;

}
