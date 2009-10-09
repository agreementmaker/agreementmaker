package am.app.mappingEngine.baseSimilarity;

import am.app.mappingEngine.AbstractParameters;

public class BaseSimilarityParameters extends AbstractParameters {

	public boolean useDictionary = false;

	public void initForOAEI2009() {
		//the set of parameters for OAEI2009
		useDictionary = false;
	}
	
}
