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

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;

public class QualityEvaluator {
	
	public static QualityMetric getQM( QualityMetricRegistry regEntry ) {
		try {
			return (QualityMetric) regEntry.getQMClass().newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static QualityEvaluationData evaluate(AbstractMatcher matcher, QualityMetric qm) throws Exception {
		return qm.getQuality(matcher);		
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
			if(first.isSourceOntology() != second.isSourceOntology()) {
				throw new RuntimeException("Developer error, you are merging two local qualities which have a different way of defining if the quality is local for source or target");
			}
			result.setLocal(true);
			result.setSourceOrTarget(first.isSourceOntology());
			result.setLocalClassMeasures(Utility.avgArrays(first.getLocalClassMeasures(), second.getLocalClassMeasures()));
			result.setLocalPropMeasures(Utility.avgArrays(first.getLocalPropMeasures(), second.getLocalPropMeasures()));
		}
		else if(first.isLocal()) { //only first local
			result.setLocal(true);
			result.setSourceOrTarget(first.isSourceOntology());
			result.setLocalClassMeasures(Utility.avgArrayAndDouble(first.getLocalClassMeasures(), second.getGlobalClassMeasure()));
			result.setLocalPropMeasures(Utility.avgArrayAndDouble(first.getLocalPropMeasures(), second.getGlobalPropMeasure()));
		}
		else if(second.isLocal()) { //only second local
			result.setLocal(true);
			result.setSourceOrTarget(second.isSourceOntology());
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
	