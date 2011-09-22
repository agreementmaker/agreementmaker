package am.app.mappingEngine.boosting;

import am.app.mappingEngine.AbstractParameters;

public class BestMatchBoostingParameters extends AbstractParameters{
	
	public double boostPercent;

	public BestMatchBoostingParameters(double boostPercent) {
		super();
		this.boostPercent = boostPercent;
	}
	
	

}
