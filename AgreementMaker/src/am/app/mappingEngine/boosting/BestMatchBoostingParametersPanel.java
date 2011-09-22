package am.app.mappingEngine.boosting;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;


public class BestMatchBoostingParametersPanel extends
		AbstractMatcherParametersPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5181542248311471158L;
	public double boostPercent=1.1;
	
	public BestMatchBoostingParametersPanel(){
		super();
	}
	
	private BestMatchBoostingParameters parameters;

	@Override
	public AbstractParameters getParameters() {
		parameters = new BestMatchBoostingParameters(boostPercent);
		return parameters;
	}

	

}
