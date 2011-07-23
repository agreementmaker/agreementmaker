package am.app.lexicon.subconcept;

import java.util.List;

import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;

public interface SubconceptSynonymLexicon extends Lexicon {

	
	public void addSubConceptSynonyms(String synonym1, String synonym2 );
	
	/**
	 * @param subconceptSynonym
	 * @return Will return null if the subconceptSynonym has no synonyms, otherwise a list of synonyms. 
	 */
	public List<String> getSubConceptSynonyms( String subconceptSynonym );
	
	/**
	 * @return A list of all the discovered subconcept synonyms.
	 */
	public List<String> getAllSubConceptSynonyms();
	
	
	/**
	 * Return a list of synonyms that are computed using the subconcept synonyms.
	 */
	public List<String> extendSynSet(LexiconSynSet synset);
}
