package am.app.lexicon;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import am.app.Core;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.tools.LexiconLookup.LexiconLookupPanel;

import com.hp.hpl.jena.ontology.OntResource;

/**
 * This class is mainly just a container class (containing LexiconSynSet objects), 
 * meant to be populated with entries by a lexicon builder.
 * 
 * Main methods are: addSynSet, getSynSet, and lookup.
 * 
 * @author cosmin
 *
 */
public class GeneralLexicon implements Lexicon {
	
	// Dictionaries/Lexicons are ALWAYS implemented as a hashtable.
	protected HashMap<OntResource,LexiconSynSet> synsetsByOntResource = new HashMap<OntResource,LexiconSynSet>();
	protected HashMap<String,LexiconSynSet> synsetsByString = new HashMap<String, LexiconSynSet>();
	
	protected long id = 0;
	protected int ontID = Core.ID_NONE;
	protected LexiconRegistry lexiconRegistryEntry; // the type of lexicon
	
	public GeneralLexicon( LexiconRegistry lr ) { lexiconRegistryEntry = lr; }
	
	@Override
	public void addSynSet(LexiconSynSet t) {
		

		t.setID(id); 
		id++;
		
		// Synsets by OntResource
		if( synsetsByOntResource.containsKey(t.getOntologyConcept()) ) {
			// we have this ont resource with a synset already.
			LexiconSynSet existingSynSet = synsetsByOntResource.get(t.getOntologyConcept());
			existingSynSet.addRelatedSynSet(t);
		} else {
			synsetsByOntResource.put(t.getOntologyConcept(), t);
		}
		
		// Synsets by string.  Iterate through the synonyms.
		for( String currentSynonym : t.getSynonyms() ) {
			if( synsetsByString.containsKey(currentSynonym) ) {
				LexiconSynSet existingSynSet = synsetsByString.get(currentSynonym);
				existingSynSet.addRelatedSynSet(t);
			} else {
				synsetsByString.put(currentSynonym, t);
			}
		}
 
	}
	
	@Override public LexiconSynSet getSynSet(String wordForm) {
		// TODO: Make this work with partial strings - cosmin
		return synsetsByString.get(wordForm); 
	}
	
	@Override public LexiconSynSet getSynSet(OntResource ontRes) {
		// TODO: Make this lookup work with partial strings - cosmin
		return synsetsByOntResource.get(ontRes); 
	}
	
	/**
	 * Find all the word forms matching the search string.
	 * @param searchString The case insensitive search string (can be a regular expression).
	 * @return Return an empty list if no match is found. Will not return null. 
	 */
	@Override public List<LexiconSynSet> lookup( String searchString ) {
		List<LexiconSynSet> synsets = new ArrayList<LexiconSynSet>();
		
		Pattern searchPattern = Pattern.compile( searchString, Pattern.CASE_INSENSITIVE );
		
		for( Entry<String,LexiconSynSet> straw : synsetsByString.entrySet() ) {
			// check this straw if it has the needle
			Matcher matcher = searchPattern.matcher( straw.getKey() );
			
			while( matcher.find() ) {
				if( matcher.start() == matcher.end() ) {
					// zero length match, ignore
					continue;
				}
				// found a non trivial match.
				synsets.add( straw.getValue() );
				
			}
		}
	
		return synsets;
	}
	
	
	@Override
	public void print(PrintStream out) {
		Set<Entry<OntResource,LexiconSynSet>> synsetEntries = synsetsByOntResource.entrySet();
		for( Entry<OntResource,LexiconSynSet> currentEntry: synsetEntries) {
			out.println("Synset " + currentEntry.getValue().getID() + ": " + currentEntry.getKey() );
			currentEntry.getValue().print(out);
		}
	}


	@Override public LexiconRegistry getType() {	return lexiconRegistryEntry; }

	@Override public Map<OntResource, LexiconSynSet> getSynSetMap() { return synsetsByOntResource; }

	@Override public int getOntologyID() { return ontID; }
	@Override public void setOntologyID(int id) { ontID = id; }
	
	@Override public int size() { return synsetsByOntResource == null ? 0 : synsetsByOntResource.size(); }
	
	private LexiconLookupPanel lookupPanel;  // this is the user interface to this lexicon
	
	@Override public void setLookupPanel(LexiconLookupPanel wnlp) { lookupPanel = wnlp; }
	@Override public LexiconLookupPanel getLookupPanel() { return lookupPanel; }

	@Override
	public List<String> extendSynSet(LexiconSynSet synset) {
		List<String> extendedList = new ArrayList<String>();
		
		// add the current synonyms
		extendedList.addAll( synset.getSynonyms() );
		
		for( LexiconSynSet relatedSynSet : synset.getRelatedSynSets() ) {
			extendedList.addAll( relatedSynSet.getSynonyms() );
		}
		
		return extendedList;
	}
}
