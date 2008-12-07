package agreementMaker.application.mappingEngine.qualityEvaluation;

public class QualityEvaluationData {
	
	private double[] localClassMeasures;
	private double[] localPropMeasures;
	
	private boolean localForSource;//this is true if those are the local measure for each source node, (each row) it will be false if those are localQuality for each target (each column)
    
	private boolean local; //this is true if the quality is global false in the other case
	
	public double getGlobalClassQuality() {
		double quality = 0;
		if(local) { // i have to calculate the average of local qualities
			for(int i = 0; i < localClassMeasures.length; i++) {
				quality+=localClassMeasures[i];
			}
		}
		else {
			if(localClassMeasures.length>0)
				quality = localClassMeasures[0];
		}
		return quality;
	}
	
	public double getGlobalPropQuality() {
		double quality = 0;
		if(local) { // i have to calculate the average of local qualities
			for(int i = 0; i < localPropMeasures.length; i++) {
				quality+=localPropMeasures[i];
			}
		}
		else {
			if(localPropMeasures.length>0)
				quality = localPropMeasures[0];
		}
		return quality;
	}

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
}
