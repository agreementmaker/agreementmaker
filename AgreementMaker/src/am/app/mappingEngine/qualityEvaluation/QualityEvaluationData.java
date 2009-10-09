package am.app.mappingEngine.qualityEvaluation;

import am.Utility;

public class QualityEvaluationData {
	
	private boolean localForSource;//this is true if those are the local measure for each source node, (each row) it will be false if those are localQuality for each target (each column)
    private boolean local; //this is true if the quality is local, false if is global and in this case localForSource doesn't matter
	
	private double[] localClassMeasures;
	private double[] localPropMeasures;
	
	private double globalClassMeasure = 0.0;
	private double globalPropMeasure = 0.0;
	
	

	public double[] getLocalClassMeasures() {
		return localClassMeasures;
	}

	public void setLocalClassMeasures(double[] localClassMeasures) {
		this.localClassMeasures = localClassMeasures;
	}

	public double[] getLocalPropMeasures() {
		return localPropMeasures;
	}

	public void setLocalPropMeasures(double[] localPropMeasures) {
		this.localPropMeasures = localPropMeasures;
	}

	public boolean isLocalForSource() {
		return localForSource;
	}

	public void setLocalForSource(boolean localForSource) {
		this.localForSource = localForSource;
	}
	

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public double getGlobalClassMeasure() {
		return globalClassMeasure;
	}

	public void setGlobalClassMeasure(double globalClassMeasure) {
		this.globalClassMeasure = globalClassMeasure;
	}

	public double getGlobalPropMeasure() {
		return globalPropMeasure;
	}

	public void setGlobalPropMeasure(double globalPropMeasure) {
		this.globalPropMeasure = globalPropMeasure;
	}
	
	
	//REAL METHODS TO GET THE QUALITY OF A CLASS NODE
	//without knowing if the quality is local or global
	public double getClassQuality(int i, int j) {
		if(local) {//the quality is local i have to get the value from the arrays
			if(localForSource) {//if is local for source nodes, the row index must be used
				return localClassMeasures[i];
			}
			else { //else the column index (that would be target node index
				return localClassMeasures[j];
			}
		}
		else { //global quality
			return globalClassMeasure;
		}
	}
	
	//REAL METHODS TO GET THE QUALITY OF A PROP NODE
	//without knowing if the quality is local or global
	public double getPropQuality(int i, int j) {
		if(local) {//the quality is local i have to get the value from the arrays
			if(localForSource) {//if is local for source nodes, the row index must be used
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
