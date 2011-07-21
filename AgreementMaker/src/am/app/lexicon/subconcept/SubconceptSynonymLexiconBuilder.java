package am.app.lexicon.subconcept;

import am.app.lexicon.LexiconBuilder;
import am.app.lexicon.LexiconSynSet;

/**
 * This method implements a lexicon that finds words in the concept string that 
 * are synonyms.
 * 
 * if "a b c" hasSynonym "d b c" then "a" is a synonym word of "d". 
 * 
 * if "a b c d" hasSynonym "a e c d" then "b" is a synonym word of "e";
 *  
 */
public interface SubconceptSynonymLexiconBuilder extends LexiconBuilder {

	/**
	 * Process a synset and its synonyms in order to identify and store
	 * sub concept synonyms.
	 */
	public void processSynSet(LexiconSynSet synset);
	
	/**
	 * Extend a synset with synonyms created by using the subconcept synonyms.
	 */
	public void extendSynSet(LexiconSynSet synset);
	
}
