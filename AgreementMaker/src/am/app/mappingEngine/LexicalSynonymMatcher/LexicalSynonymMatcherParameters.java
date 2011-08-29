package am.app.mappingEngine.LexicalSynonymMatcher;

import am.app.mappingEngine.AbstractParameters;

public class LexicalSynonymMatcherParameters extends AbstractParameters {

	/** Synonym Terms are a feature of the Lexicon implementation. */
	public boolean useSynonymTerms = false;
	
	public LexicalSynonymMatcherParameters() {
		super();
	}
	
	public LexicalSynonymMatcherParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign) {
		super( threshold, maxSourceAlign, maxTargetAlign );
	}
}
