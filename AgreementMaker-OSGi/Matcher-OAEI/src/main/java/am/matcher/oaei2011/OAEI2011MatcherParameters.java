package am.matcher.oaei2011;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.ontology.profiling.classification.OntologyClassifier.OAEI2011Configuration;

/**
 * This year's matching algorithm uses a completely automatic configuration. 
 *
 */
public class OAEI2011MatcherParameters extends DefaultMatcherParameters {

	private static final long serialVersionUID = 5708419970641391711L;
	
	public boolean automaticConfiguration = true;
	public OAEI2011Configuration selectedConfiguration = OAEI2011Configuration.GENERAL_PURPOSE;
	
	public boolean showIntermediateMatchers = false;
	public boolean parallelExecution = false;
		
	public OAEI2011MatcherParameters() { 
		super(); 
	}
	
}
