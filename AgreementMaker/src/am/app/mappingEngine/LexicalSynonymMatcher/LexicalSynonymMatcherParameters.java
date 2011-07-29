package am.app.mappingEngine.LexicalSynonymMatcher;

import am.app.mappingEngine.AbstractParameters;

public class LexicalSynonymMatcherParameters extends AbstractParameters {

	public boolean useSubconceptSynonyms = false;
	
	public LexicalSynonymMatcherParameters() {
		super();
	}
	
	public LexicalSynonymMatcherParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign) {
		super( threshold, maxSourceAlign, maxTargetAlign );
	}
}
