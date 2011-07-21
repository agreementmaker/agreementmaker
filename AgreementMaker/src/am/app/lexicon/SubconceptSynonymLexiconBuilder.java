package am.app.lexicon;

import java.util.List;

/**
 * This method implements a lexicon that finds words in the concept string that 
 * are synonyms.
 * 
 * if "a b c" hasSynonym "d b c" then "a" is a synonym word of "d". 
 * 
 * @author Cosmin M. Stroe
 */
public interface SubconceptSynonymLexiconBuilder extends LexiconBuilder {

	/**
	 * Process a concept and it synonyms in order to identify and store
	 * sub concept synonyms.
	 * 
	 * @param synonymList The list of synonyms for a concept.
	 * @return A list of synonyms that are not in the synonymList passed in.  The list will be empty if no subconcept synonyms are found.
	 */
	public List<String> processSynonymList(List<String> synonymList);
	
	/**
	 * Determine if a word is a subconcept synonym.
	 * This is a utility function.
	 * @return True if the word is a subconcept synonym.
	 */
	//public boolean isSubconceptSynonym(String word); // commented out for now
	
	/**
	 * Compose extra word forms created by replacing synonym word forms. 
	 * @param term
	 * @return The list of created 
	 */
	//public List<String> composeSynonymWordForms(String term);
}
