package am.app.mappingEngine.LexicalSynonymMatcher;

import am.app.lexicon.Lexicon;
import am.app.mappingEngine.DefaultMatcherParameters;

public class LexicalSynonymMatcherParameters extends DefaultMatcherParameters {

	private static final long serialVersionUID = -1723312235796171432L;
	
	public transient Lexicon sourceLexicon = null;
	public transient Lexicon targetLexicon = null;
	
	public boolean preExtendSource = true;
	public boolean preExtendTarget = false;
	
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
