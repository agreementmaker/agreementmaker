package am.app.mappingEngine;

public abstract class AbstractParameters {

	public double threshold = 0.6;
	public int maxSourceAlign = 1;
	public int maxTargetAlign = 1;
	public boolean completionMode = false;
	
	public AbstractParameters() {
		// TODO Auto-generated constructor stub
	}

	public AbstractParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign) {
		this.threshold = threshold;
		this.maxSourceAlign = maxSourceAlign;
		this.maxTargetAlign = maxTargetAlign;
	}

}
