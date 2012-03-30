package lexicalmatcherjaws.internal;

/**
 * A WordNet dictionary term.
 * @author cosmin
 *
 */
public class WNLexiconTerm implements LexiconTerm {

	String wordform;
	
	public WNLexiconTerm( String wordform ) {
		this.wordform = wordform;
	}
	
	@Override
	public boolean hasDefinition(Lexicon dictionary) {
		return dictionary.exists(wordform);
	}

	@Override
	public String getWordform() { return wordform; }
	
}
