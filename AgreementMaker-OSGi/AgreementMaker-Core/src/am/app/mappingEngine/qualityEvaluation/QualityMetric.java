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
import am.utility.parameters.AMParameterSet;
import am.utility.parameters.HasParameters;

public interface QualityMetric extends HasParameters {
	
	public QualityEvaluationData getQuality( AbstractMatcher matcher ) throws Exception;
	
	public QualityEvaluationData getQuality( AbstractMatcher matcher, AMParameterSet params ) throws Exception;
	
}
