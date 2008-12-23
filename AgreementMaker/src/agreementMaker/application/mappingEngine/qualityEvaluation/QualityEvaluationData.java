package agreementMaker.application.mappingEngine.qualityEvaluation;

public class QualityEvaluationData {
	
	private double[] localClassMeasures;
	private double[] localPropMeasures;
	
	private boolean localForSource;//this is true if those are the local measure for each source node, (each row) it will be false if those are localQuality for each target (each column)
    private boolean local; //this is true if the quality is local, false if is global and in this case localForSource doesn't matter

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
	
	public double getGlobalClassQuality() {
		if(localClassMeasures.length >0)
			return localClassMeasures[0];
		return 0;
	}
	
	public double getGlobalPropQuality() {
		if(localPropMeasures.length >0)
			return localClassMeasures[0];
		return 0;
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}
	
	
}
