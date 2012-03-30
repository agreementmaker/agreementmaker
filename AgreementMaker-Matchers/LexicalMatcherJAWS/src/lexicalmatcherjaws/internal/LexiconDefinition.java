package lexicalmatcherjaws.internal;

/**
 * This interface abstracts a lexicon definition.
 * @author cosmin
 *
 */
public interface LexiconDefinition {

	
	/**
	 * Returns the dictionary which contains this definition.
	 * @return The dictionary which contains this definition.
	 */
	public Lexicon getLexicon(); 
	
	
}
