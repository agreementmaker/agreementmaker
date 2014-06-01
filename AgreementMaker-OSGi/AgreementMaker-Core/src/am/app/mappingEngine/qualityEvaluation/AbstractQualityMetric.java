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

package am.app.mappingEngine.qualityEvaluation;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
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
public abstract class AbstractQualityMetric implements MappingQualityMetric {

	protected AMParameterSet params = new AMParameterSet();
	protected String metricID;
	protected double weight;

<<<<<<< .working
<<<<<<< .working
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getMetricID() {
		return metricID;
	}

	public void setMetricID(String metricID) {
		this.metricID = metricID;
	}

=======
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getMetricID() {
		return metricID;
	}

	public void setMetricID(String metricID) {
		this.metricID = metricID;
	}

>>>>>>> .merge-right.r3637
	@Override 
	public void setParameter(AMParameter param) { 
		params.put(param); 
	}
	
	@Override
	public void setParameters(AMParameterSet param) {
		params = param;
	}
	
	/**
	 * Helper method for setting the parameters and computing the quality at the
	 * same time.
	 */
	public QualityEvaluationData getQuality(AbstractMatcher matcher, AMParameterSet params) throws Exception {
=======
	@Override 
	public void setParameter(AMParameter param) { 
		params.put(param); 
	}
	
	@Override
	public void setParameters(AMParameterSet param) {
		params = param;
	}
	
	/**
	 * Helper method for setting the parameters and computing the quality at the
	 * same time.
	 */
	public QualityEvaluationData getQuality(AbstractMatcher matcher, AMParameterSet params) throws Exception {
>>>>>>> .merge-right.r3574
		setParameters(params);
		return getQuality(matcher);
	}
	
	@Override 
	public QualityEvaluationData getQuality(AbstractMatcher matcher) throws Exception {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public double getQuality(alignType type, int i, int j) {
		throw new RuntimeException("Not implemented.");
	}
	
}
