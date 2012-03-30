package lexicalmatcherjaws.internal;

import java.util.ArrayList;

/**
 * This interface is meant to abstract a dictionary.
 * @author cosmin
 *
 */
public interface Lexicon {

	
	public boolean exists( String wordform );  // Determines if a text is defined in the Lexicon.
	public LexiconTerm getTerm(String wordform);  // Return the LexiconTerm representation of the text.
	
	public ArrayList<LexiconTerm> getSynonyms( LexiconTerm term );
	public ArrayList<LexiconTerm> getWordforms( LexiconTerm term );
	
	
	
	
}
