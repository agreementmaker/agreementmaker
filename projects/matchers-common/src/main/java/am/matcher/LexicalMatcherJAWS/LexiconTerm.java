package am.matcher.LexicalMatcherJAWS;

public interface LexiconTerm {

	/**
	 * Returns true if this word is defined in the given Lexicon.
	 * @param dictionary The dictionary that the lookup is done in.
	 * @return true if a definition has been found for this term, false otherwise.
	 */
	public boolean hasDefinition( Lexicon dictionary);
	
	/**
	 * Get the textual of this term.
	 * @return String representation of this term.
	 */
	public String getWordform();
}
