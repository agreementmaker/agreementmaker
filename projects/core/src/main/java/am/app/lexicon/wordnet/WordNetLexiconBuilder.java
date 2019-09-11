package am.app.lexicon.wordnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import am.app.Core;
import am.app.lexicon.GeneralLexicon;
import am.app.lexicon.GeneralLexiconSynSet;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconBuilder;
import am.app.lexicon.LexiconSynSet;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.OntResource;

import edu.smu.tspell.wordnet.api.Synset;
import edu.smu.tspell.wordnet.api.SynsetType;
import edu.smu.tspell.wordnet.api.WordNetDatabase;

/**
 * Enriches an ontology lexicon with synonyms from WordNet.
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 *
 */
public class WordNetLexiconBuilder implements LexiconBuilder {

	private static final Logger LOG = LogManager.getLogger(WordNetLexiconBuilder.class);
	
	Lexicon wordnetLexicon;
	Lexicon ontologyLexicon; // depends on the ontology lexicon
	
	Ontology currentOntology;
	
	private WordNetDatabase WordNet; // the WordNet Interface
	
	/**
	 * 
	 * @param ont The ontology for which we are building the lexicon.
	 * @param ontLexicon An ontology lexicon built for the specific ontology.
	 */
	public WordNetLexiconBuilder( Ontology ont, Lexicon ontLexicon ) {
		currentOntology = ont;
		wordnetLexicon = new GeneralLexicon( LexiconRegistry.WORDNET_LEXICON);
		ontologyLexicon = ontLexicon;
		
		// Initialize the WordNet interface.
		String wordnetdir = Core.getInstance().getRoot() + "/wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
		
		
		// Instantiate wordnet.
		try {
			WordNet = WordNetDatabase.getFileInstance();
			WordNet.getSynsets("test");
		} catch( Exception e ) {
			String message = "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir;
			LOG.error(message, e);
			throw new RuntimeException(message + "\n" + e.getMessage(), e);
		}
	}
	
	@Override
	public Lexicon buildLexicon() {
		//long id = 0;

		
		
		// Iterate through all the Synsets in the ontology lexicon
		for( Entry<OntResource, LexiconSynSet> currentEntry : ontologyLexicon.getSynSetMap().entrySet() ) {
			LexiconSynSet currentOntologySynSet = currentEntry.getValue();   			// current synset in the ontology lexicon
			List<String> currentOntologySynonyms = currentOntologySynSet.getSynonyms();	// synonyms of the current ontology synset.
			
			ArrayList<String> wordnetWordForms = new ArrayList<String>();
			ArrayList<String> wordnetDefinitions = new ArrayList<String>();
			
			// Step 1. Lookup all the word forms of the current ontology synset in WordNet.  Gather the results in a list.
			for( String currentOntologySynonym : currentOntologySynonyms ) {
				
				ArrayList<String> wordFormsFound = getAllWordForms(currentOntologySynonym);
				ArrayList<String> definitionsFound = getAllDefinitions(currentOntologySynonym); // TODO: Merge with above method.  No sense in looking up the same word twice.
				
				if( wordFormsFound.isEmpty() ) continue; // this word was not found in the wordnet dictionary.
				
				for( String wordform : wordFormsFound ) {
					if( !wordnetWordForms.contains(wordform) ) wordnetWordForms.add(wordform);
				}
				
				for( String def : definitionsFound ) {
					if( !wordnetDefinitions.contains(def) ) wordnetDefinitions.add(def);
				}
				
			}
				


			// Step 2.  Check if any of the wordnet word forms are in the ontology Lexicon already.  Keep a list of the unique entries found in wordnet.
			
			ArrayList<String> uniqueWordForms = new ArrayList<String>();
			
			for( String currentWordForm : wordnetWordForms ) {
				if( !wordnetWordForms.contains(currentWordForm) ) {
					uniqueWordForms.add(currentWordForm);
				}
			}

			// Step 2a. Check to make sure we found new information.
			//if( uniqueWordForms.isEmpty() ) continue; // no unique wordnet word forms found for the ontology synset.
				
			// Step 3. Create a new synset for this class.
			GeneralLexiconSynSet wordNetNewSynSet = new GeneralLexiconSynSet(LexiconRegistry.WORDNET_LEXICON);
			
			// Step 3a. add all the unique wornet wordforms to the new synset.
			for( String wordnetWordForm : uniqueWordForms ) {
				wordNetNewSynSet.addSynonym(wordnetWordForm);
			}
			
				
			// Step 4. Set the definition. (Problem: for multiple wordnet synsets, which definition do we choose????????) TODO
			 					  //( TODO: Answer: a robust disambiguation solution is required here. (ha ha ha, that's not going to happen anytime soon) )
			if( currentOntologySynSet.getGloss() == null && !wordnetDefinitions.isEmpty() ) {
				wordNetNewSynSet.setGloss( wordnetDefinitions.get(0) ); // the first definition found	
			} 
			if( !wordnetDefinitions.isEmpty() ) {
				// the ontology definition exists.  That's fine, but we will set a wordnet definition also.
				wordNetNewSynSet.setGloss( wordnetDefinitions.get(0)); // TODO: for multiple wordnet synsets, we need a robust disambiguation solution
			}
				
			if( wordNetNewSynSet.isEmpty() ) continue; // no new information
			
			// Step 5. Create link from ontology synset to wordnet synset (and back).
			currentOntologySynSet.addRelatedSynSet(wordNetNewSynSet);
			wordNetNewSynSet.setOntologyConcept( currentOntologySynSet.getOntologyConcept() );
			wordNetNewSynSet.addRelatedSynSet(currentOntologySynSet);
				
			// Done creating the SynSet.
			wordnetLexicon.addSynSet(wordNetNewSynSet);
			
		}
		
		return wordnetLexicon;
	}

	private ArrayList<String> getAllWordForms(String searchTerm) {
		ArrayList<String> wordFormsFound = new ArrayList<String>();

		
		// lookup
		for (SynsetType t : SynsetType.ALL_TYPES) {
			Synset[] synsets = WordNet.getSynsets(searchTerm, t);
			
			for (int i = 0; i < synsets.length; i++) {
				String[] words = synsets[i].getWordForms(); // get the wordforms of this synset
				
				for (int j = 0; j < words.length; j++) {
					if( !words[j].trim().equals("") && !wordFormsFound.contains( words[j] ) ) wordFormsFound.add(words[j]);
				}
				
			}
		}
		
		return wordFormsFound;
	}
	
	private ArrayList<String> getAllDefinitions(String searchTerm) {
		ArrayList<String> definitionsFound = new ArrayList<String>();

		
		// lookup
		for (SynsetType t : SynsetType.ALL_TYPES) {
			Synset[] synsets = WordNet.getSynsets(searchTerm, t);
			
			for (int i = 0; i < synsets.length; i++) {
				String definition = synsets[i].getDefinition(); // get the definition of this synset
				if( !definitionsFound.contains( definition ) ) definitionsFound.add(definition);
			}
		}
		
		return definitionsFound;
	}
	
}
