package am.app.mappingEngine.abstractMatcherNew;

import am.app.mappingEngine.AbstractMatcherParametersPanel;

public abstract class BaseMatcher extends AbstractMatcherNew {
	
	public BaseMatcher() {
		super();
		// set what distinguishes a base matcher from a refining one
		matchingParameters.setMinInputMatchers(0);
		matchingParameters.setMaxInputMatchers(0);
	}

	@Override
	public abstract AbstractMatcherParametersPanel callParametersPanel();

}
