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
/**
 * This data structure is used to store the quality evaluation of a
 * Quality Metric.
 * 
 * Note: This quality evaluation data is for only a single ontology.
 *
 */
public class QualityEvaluationData {
	
	/** 
	 * If this quality evaluation data is for the source ontology = true;
	 * if this quality evaluation data is for the target ontology = false;
	 */
	private boolean isSourceOntology;
    
	/**
	 * Local Quality Measure = true; (a local QM computes a quality value for each concept) 
	 * Global Quality Measure = false; (computes a quality value for the whole alignment)
	 */
	private boolean isLocalQualityMeasure; //this is true if the quality is local, false if is global and in this case localForSource doesn't matter
	
	/**
	 * Local QM values for each class.
	 */
	private double[] localClassMeasures;
	
	/**
	 * Local QM values for each property.
	 */
	private double[] localPropMeasures;
	
	/**
	 * Global QM value for the classes alignment.
	 */
	private double globalClassMeasure = 0.0;
	
	/**
	 * Global QM value for the properties alignment.
	 */
	private double globalPropMeasure = 0.0;
	
	

	public double[] getLocalClassMeasures() { return localClassMeasures; }
	public void setLocalClassMeasures(double[] localClassMeasures) { this.localClassMeasures = localClassMeasures; }

	public double[] getLocalPropMeasures() { return localPropMeasures; }
	public void setLocalPropMeasures(double[] localPropMeasures) { this.localPropMeasures = localPropMeasures; }

	public boolean isSourceOntology() { return isSourceOntology; }
	public void setSourceOrTarget(boolean isSource) { this.isSourceOntology = isSource; }
	

	public boolean isLocal() { return isLocalQualityMeasure; }
	public void setLocal(boolean local) { this.isLocalQualityMeasure = local; }

	public double getGlobalClassMeasure() { return globalClassMeasure; }
	public void setGlobalClassMeasure(double globalClassMeasure) { this.globalClassMeasure = globalClassMeasure; }

	public double getGlobalPropMeasure() { return globalPropMeasure; }

	public void setGlobalPropMeasure(double globalPropMeasure) { this.globalPropMeasure = globalPropMeasure; }
	
	
	/**
	 * Use similarity matrix coordinates to get the class quality value.
	 * @param i
	 * @param j
	 * @return
	 */
	public double getClassQuality(int i, int j) {
		if(isLocalQualityMeasure) {//the quality is local i have to get the value from the arrays
			if(isSourceOntology) {//if is local for source nodes, the row index must be used
				return localClassMeasures[i];
			}
			else { //else the column index (that would be target node index)
				return localClassMeasures[j];
			}
		}
		else { //global quality
			return globalClassMeasure;
		}
	}

	/**
	 * Use similarity matrix coordinates to get the property quality value.
	 * @param i
	 * @param j
	 * @return
	 */
	public double getPropQuality(int i, int j) {
		if(isLocalQualityMeasure) {//the quality is local i have to get the value from the arrays
			if(isSourceOntology) {//if is local for source nodes, the row index must be used
				return localPropMeasures[i];
			}
			else { //else the column index (that would be target node index
				return localPropMeasures[j];
			}
		}
		else { //global quality
			return globalPropMeasure;
		}
	}
	
	
	public double getAvgLocalClassQuality() {
		return Utility.getAverageOfArray(localClassMeasures);
	}
	
	public double getAvgLocalPropQuality() {
		return Utility.getAverageOfArray(localPropMeasures);
	}
	
}
