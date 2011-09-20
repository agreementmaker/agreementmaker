package am.app.mappingEngine.oaei.oaei2011;

import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.oaei.OAEI_Track;

/**
 * This year's matching algorithm uses a completely automatic configuration. 
 *
 */
public class OAEI2011MatcherParameters extends AbstractParameters {

	public enum OAEI2011Configuration {
		GENERAL_PURPOSE,
		GENERAL_PURPOSE_ADVANCED,
		LARGE_LEXICAL,
		GENERAL_MULTI,
		;
	}
	
	public OAEI2011Configuration selectedConfiguration;
	
	public OAEI2011MatcherParameters() { 
		super(); 
	}
	
}
