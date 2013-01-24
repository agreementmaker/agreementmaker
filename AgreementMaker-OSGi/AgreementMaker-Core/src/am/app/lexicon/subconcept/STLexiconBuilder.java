package am.app.lexicon.subconcept;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.lexicon.ontology.OntologyLexiconBuilder;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * This lexicon builder is the same as an ontology lexicon, except that it builds
 * extra synonyms by checking for synonym terms.
 * @author cosmin
 *
 */
public class STLexiconBuilder 
	   extends OntologyLexiconBuilder 
	   implements SubconceptSynonymLexiconBuilder {

	public STLexiconBuilder(Ontology ont, boolean includeLN,
			List<Property> synonym,
			List<Property> definition) {
		super(ont, includeLN, synonym, definition);
		currentLexicon = new STLexicon( LexiconRegistry.ONTOLOGY_LEXICON );
	}

	@Override
	public Lexicon buildLexicon() {
		// Step 1.  Build the lexicon as we normally do.
		super.buildLexicon();  
		
		// Step 2.  Process the synsets, identifying subconcept synonyms.
		Map<OntResource,LexiconSynSet> lexiconSynsetMap = currentLexicon.getSynSetMap();
		for(LexiconSynSet currentSynSet : lexiconSynsetMap.values() ) {
			processSynSet(currentSynSet);
		}
		
		// Step 3.  Extend the lexicon by adding extra synonyms using the subconcept synonyms identified.
		// We run out of memory _very_ quickly doing this.  We should do it at the matcher level.
		//for(LexiconSynSet currentSynSet : lexiconSynsetMap.values() ) {
		//	extendSynSet(currentSynSet);
		//}
		
		Logger log = Logger.getLogger(this.getClass());
		STLexicon currentSCSLexicon = (STLexicon) currentLexicon;
		HashMap<String,List<String>> hashMap = currentSCSLexicon.getHashMap();
		
		log.info("Synonym Word Pairs HashMap: ");
		for( Entry<String,List<String>> currentEntry : hashMap.entrySet() ) {
			log.info( currentEntry.getKey() + ": " + currentEntry.getValue() );
		}
		
		return currentLexicon;
	}
	
	@Override
	public void processSynSet(LexiconSynSet synset) {

		List<String> synonymList = synset.getSynonyms();
		
		for( int i = 0; i < synonymList.size(); i++ ) {
			for( int j = (i+1); j < synonymList.size(); j++) {
				try {
					compareSynonyms(synonymList.get(i), synonymList.get(j));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return;
	}

	/**
	 * This method extracts sub concept synonyms from two given synonyms.
	 * @throws Exception When things go horribly wrong.  Should not happen.
	 */
	private void compareSynonyms(String synonym1, String synonym2) throws Exception {
		
		if( synonym1 == null || synonym1.isEmpty() ) return;
		if( synonym2 == null || synonym2.isEmpty() ) return;
		
		
		// Step 1. Set up the variables for our algorithm.
		
		String[] synonym1_words = synonym1.split(" ");
		String[] synonym2_words = synonym2.split(" ");
		
		// determine which synonyms has more words, to simplify the algorithm
		String[] longerSynonymWords;
		String[] shorterSynonymWords;
		if( synonym1_words.length > synonym2_words.length ) {
			longerSynonymWords = synonym1_words;
			shorterSynonymWords = synonym2_words;
		} else {
			longerSynonymWords = synonym2_words;
			shorterSynonymWords = synonym1_words;
		}
		
		// since we care to match terms at the end of the string before others, reverse the arrays to simplify the algorithm.
		ArrayUtils.reverse(longerSynonymWords); 
		ArrayUtils.reverse(shorterSynonymWords);
		
		
		// Step 2. Determine the largest overlap between the two synonyms, starting from the end, then refining the range by checking from the beginning.
		
		// Check from the end first.
		int distanceFromEnd = 0; // need the variable to be available outside the for loop.
		for( /* initial condition done above */; distanceFromEnd < longerSynonymWords.length; distanceFromEnd++ ) {
			if( distanceFromEnd >= shorterSynonymWords.length ) return; // we have gone past the beginning of the shorter synonym (shorter synonym is subsumed into the longer synonym)
			
			if( shorterSynonymWords[distanceFromEnd].equalsIgnoreCase(longerSynonymWords[distanceFromEnd]) ) continue; // the word is a match, continue to the next word.
			else break; // no more matches. 
		}
		
		
		// refine the word range by checking for overlap at the beginning of the synonyms
		// since we are now checking from the beginning, reverse the arrays to simplify the algorithm.  Note: After this, the arrays are back to the correct order.
		ArrayUtils.reverse(longerSynonymWords);
		ArrayUtils.reverse(shorterSynonymWords);
		
		int distanceFromBeginning = 0;
		for( /* initial condition done above */; distanceFromBeginning < (longerSynonymWords.length - distanceFromEnd); distanceFromBeginning++ ) {
			if( distanceFromBeginning >= shorterSynonymWords.length ) return; // the shorter synonym is subsumed into the longer synonym
			
			if( shorterSynonymWords[distanceFromBeginning].equalsIgnoreCase(longerSynonymWords[distanceFromBeginning]) ) continue; // the word is a match, continue to the next word.
			else break;
		}
		
		// Step 3.  Identify the subconcept synonyms that we have found.
		
		int startIndex = distanceFromBeginning;
		int longerSynonymEndIndex = (longerSynonymWords.length - 1) - distanceFromEnd;
		int shorterSynonymEndIndex = (shorterSynonymWords.length - 1) - distanceFromEnd;
		
		if( longerSynonymEndIndex < startIndex || shorterSynonymEndIndex < startIndex )
			return; // happens when there is no correspondence between words (for example "facial vii motor nucleus" with "facial vii nucleus" leaves motor with no match).
				    // TODO: Do something with this in the future?
			//throw new Exception("Ending index is before the starting index."); // this should never happen, and indicates a bug in the algorithm.
		
		// create the longer synonym string;
		String longerSynonym = new String();
		for( int i = startIndex; i <= longerSynonymEndIndex; i++ ) {
			if( i != startIndex ) longerSynonym += " ";
			longerSynonym += longerSynonymWords[i];
		}

		// create the shorter synonym string
		String shorterSynonym = new String();
		for( int i = startIndex; i <= shorterSynonymEndIndex; i++ ) {
			if( i != startIndex ) shorterSynonym += " ";
			shorterSynonym += shorterSynonymWords[i];
		}
		
		// Step 4.  Save the synonyms in the Lexicon.
		
		if( !(currentLexicon instanceof SynonymTermLexicon) ) 
			throw new Exception("The lexicon is not compatible with this builder."); // should never happen. 
		
		SynonymTermLexicon scsLexicon = (SynonymTermLexicon) currentLexicon; 
		scsLexicon.addSynonymTerm(shorterSynonym, longerSynonym);
		
		
		return;
	}

}
