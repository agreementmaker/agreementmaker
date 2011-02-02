package am.app.lexicon;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import am.app.Core;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;

import com.hp.hpl.jena.ontology.OntResource;


public class GeneralLexicon implements Lexicon {
	
	// Dictionaries/Lexicons are ALWAYS implemented as a hashtable.
	HashMap<OntResource,LexiconSynSet> synsetsByOntResource = new HashMap<OntResource,LexiconSynSet>();
	HashMap<String,LexiconSynSet> synsetsByString = new HashMap<String, LexiconSynSet>();
	
	long id = 0;
	int ontID = Core.ID_NONE;
	LexiconRegistry lexiconRegistryEntry; // the type of lexicon
	
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
	
	@Override public LexiconSynSet getSynSet(String wordForm) { return synsetsByString.get(wordForm); }
	@Override public LexiconSynSet getSynSet(OntResource ontRes) { return synsetsByOntResource.get(ontRes); }
	
	@Override
	public void print(PrintStream out) {
		Set<Entry<OntResource,LexiconSynSet>> synsetEntries = synsetsByOntResource.entrySet();
		for( Entry<OntResource,LexiconSynSet> currentEntry: synsetEntries) {
			out.println("Synset " + currentEntry.getValue().getID() + ": " + currentEntry.getKey() );
			currentEntry.getValue().print(out);
		}
	}




	@Override
	public LexiconRegistry getRegistryEntry() {	return lexiconRegistryEntry; }




	@Override
	public Map<OntResource, LexiconSynSet> getSynSetMap() {	return synsetsByOntResource; }




	@Override
	public int getOntologyID() { return ontID; }
	
	@Override
	public void settOntologyID(int id) { ontID = id; }
	

	
}
