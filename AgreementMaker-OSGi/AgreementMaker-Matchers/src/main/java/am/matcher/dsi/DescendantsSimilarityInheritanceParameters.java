package am.matcher.dsi;

import am.app.mappingEngine.DefaultMatcherParameters;

public class DescendantsSimilarityInheritanceParameters extends DefaultMatcherParameters {

	public double MCP = 0.75d;

	public void initForOAEI2009() {
		MCP = 0.75d;
	}
	
	

}
