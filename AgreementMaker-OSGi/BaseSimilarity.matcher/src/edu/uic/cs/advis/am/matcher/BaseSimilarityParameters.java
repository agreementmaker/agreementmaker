package edu.uic.cs.advis.am.matcher;

import am.app.mappingEngine.DefaultMatcherParameters;

public class BaseSimilarityParameters extends DefaultMatcherParameters {

	private static final long serialVersionUID = -885334914920647752L;
	
	public boolean useDictionary = false;

	public BaseSimilarityParameters() { super(); }
	public BaseSimilarityParameters(double th, int s, int t) { super(th, s, t); }
	
	public void initForOAEI2009() {
		//the set of parameters for OAEI2009
		useDictionary = false;
	}
	
}
