package am.app.mappingEngine.oaei.oaei2011;

import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.oaei.OAEI_Track;

/**
 * This year's matching algorithm uses a completely automatic configuration. 
 *
 */
public class OAEI2011MatcherParameters extends AbstractParameters {

	private static final long serialVersionUID = 5708419970641391711L;
	
	public boolean automaticConfiguration = true;
	public OAEI2011Configuration selectedConfiguration = OAEI2011Configuration.GENERAL_PURPOSE;
	
	public boolean showIntermediateMatchers = false;
	public boolean parallelExecution = false;
	
	public enum OAEI2011Configuration {
		GENERAL_PURPOSE,
		GENERAL_PURPOSE_ADVANCED,
		LARGE_LEXICAL,
		GENERAL_MULTI,
		LARGE_LEXICAL_WITH_LOCALNAMES,
		;
	}
		
	public OAEI2011MatcherParameters() { 
		super(); 
	}
	
}
