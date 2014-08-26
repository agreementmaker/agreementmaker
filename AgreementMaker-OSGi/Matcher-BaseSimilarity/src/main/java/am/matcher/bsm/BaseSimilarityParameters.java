package am.matcher.bsm;

import am.app.mappingEngine.DefaultMatcherParameters;

public class BaseSimilarityParameters extends DefaultMatcherParameters {

	private static final long serialVersionUID = -885334914920647752L;
	
	public boolean useDictionary = false;
	public boolean useProfiling = false;
	public boolean useLocalname = true;
	public boolean useLabel = true;
	
	public boolean useNorm1 = true;
	public boolean useNorm2 = true;
	public boolean useNorm3 = true;

	public BaseSimilarityParameters() { super(); }
	public BaseSimilarityParameters(double th, int s, int t) { super(th, s, t); }
	
	public void initForOAEI2009() {
		//the set of parameters for OAEI2009
		useDictionary = false;
	}
	
}
