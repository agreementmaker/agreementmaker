package am.app.lexicon.subconcept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import am.app.lexicon.GeneralLexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;

/**
 * A lexicon that is able to keep track of words that should be synonyms, based on 
 * looking at common words between synonyms declared in an ontology.
 * 
 * @author cosmin
 */
public class STLexicon extends GeneralLexicon implements SynonymTermLexicon {

	protected HashMap<String,List<String>> subconceptSynonymMap = new HashMap<String,List<String>>();
	
	public STLexicon(LexiconRegistry lr) {
		super(lr);
	}

	@Override
	public void addSynonymTerm(String synonym1, String synonym2 ) {	
		addToMap( synonym1, synonym2);
		addToMap( synonym2, synonym1);
	}

	private void addToMap(String synonym1, String synonym2) {
		Logger log = Logger.getLogger(this.getClass());	
		
		if( subconceptSynonymMap.containsKey(synonym1) ) {
			List<String> synonym1List = subconceptSynonymMap.get(synonym1);
			if( !synonym1List.contains(synonym2) ) {
				log.info("Adding subconcept synonyms: " + synonym1 + " = " + synonym2);
				synonym1List.add(synonym2);
			}
		} else {
			List<String> synonym1List = new LinkedList<String>(); // LinkedList to save space. (if you need to access via index, change to ArrayList).
			log.info("Adding subconcept synonyms: " + synonym1 + " = " + synonym2);
			synonym1List.add(synonym2);
			subconceptSynonymMap.put(synonym1, synonym1List);
		}
	}
	
	@Override
	public List<String> getSynonymTerms(String synonym) {
		return subconceptSynonymMap.get(synonym);
	}
	
	@Override
	public List<String> getAllSynonymTerms() {
		return new ArrayList<String>(subconceptSynonymMap.keySet());
	}


	/**
	 * After the sub concept synonyms have been found (by calling compareSynonyms),
	 * extend the synsets in the Lexicon with newly created synonyms.
	 */
	@Override
	public Set<String> extendSynSet(LexiconSynSet synset) {
		
		//SubconceptSynonymLexicon scsLexicon = (SubconceptSynonymLexicon) currentLexicon;
		
		List<String> subconceptSynonyms = getAllSynonymTerms();
		Set<String> currentSynonyms = super.extendSynSet(synset);
		
		Set<String> newSynonymList = new TreeSet<String>();
		newSynonymList.addAll(currentSynonyms);
		
		// Step 1. Compute the new synonyms.		
		for( String existingSynonym : currentSynonyms ) {
			for( String subconceptSynonym : subconceptSynonyms ) {
				if( existingSynonym.contains(" " + subconceptSynonym + " ") ) {
					// the current synonym contains the sub concept synonym.
					List<String> subconceptEntries = getSynonymTerms(subconceptSynonym);
					for( String subconceptPairWord : subconceptEntries ) {
						String newSynonym = existingSynonym.replace(" " + subconceptSynonym + " ", " " + subconceptPairWord + " ");
						newSynonymList.add(newSynonym);
					}
//Catia: edited to account for cases where the subconceptSynonym is a very short string: eg "c" is a synonym for "parafollicular", so epigastric was being extended to epigastriparafollicular.
				} else if( existingSynonym.contains( " " + subconceptSynonym ) && subconceptSynonym.length()>2 ) {
					// the current synonym contains the sub concept synonym.
					List<String> subconceptEntries = getSynonymTerms(subconceptSynonym);
					for( String subconceptPairWord : subconceptEntries ) {
						String newSynonym = existingSynonym.replace(" " + subconceptSynonym, " " + subconceptPairWord);
						newSynonymList.add(newSynonym);
					}
				} else if( existingSynonym.contains( subconceptSynonym + " ")  && subconceptSynonym.length()>2) {
					// the current synonym contains the sub concept synonym.
					List<String> subconceptEntries = getSynonymTerms(subconceptSynonym);
					for( String subconceptPairWord : subconceptEntries ) {
						String newSynonym = existingSynonym.replace(subconceptSynonym + " ", subconceptPairWord + " ");
						newSynonymList.add(newSynonym);
					}
				}
			}
		}
		
		return newSynonymList;
	}

	
	public HashMap<String,List<String>> getHashMap() { return subconceptSynonymMap; }
}
