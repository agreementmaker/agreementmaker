package lexicalmatcherjaws.internal;

import java.util.ArrayList;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WNLexicon implements Lexicon{

	private WordNetDatabase WordNet;
	
	/**
	 * Initialize the WordNet dictionary interface (via JAWS).
	 */
	public WNLexicon() {
		
		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";

		System.setProperty("wordnet.database.dir", wordnetdir);
		
		
		// Instantiate wordnet.
		WordNet = WordNetDatabase.getFileInstance();
	}
	
	@Override
	public LexiconTerm getTerm(String wordform ) {
		
		if( exists(wordform) ) {
			
		}
		
		return null; // should throw an exception.
	}
	
	/**
	 * Determines if this term exists in the WordNet dictionary.
	 */
	@Override
	public boolean exists(String wordform) {
		
		for (SynsetType t : SynsetType.ALL_TYPES) {
			Synset[] synsets = WordNet.getSynsets( wordform, t);
			if( synsets.length > 0 ) { return true; }
		}
		
		return false;
	}

	@Override
	public ArrayList<LexiconTerm> getSynonyms(LexiconTerm term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<LexiconTerm> getWordforms(LexiconTerm term) {
		// TODO Auto-generated method stub
		return null;
	}

}
