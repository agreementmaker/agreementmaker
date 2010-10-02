package am.app.lexicon.wordnet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

import am.Utility;
import am.app.lexicon.GeneralLexicon;
import am.app.lexicon.GeneralLexiconSynSet;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconBuilder;
import am.app.lexicon.LexiconSynSet;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class WordNetLexiconBuilder implements LexiconBuilder {

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
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";

		System.setProperty("wordnet.database.dir", wordnetdir);
		
		
		// Instantiate wordnet.
		try {
			WordNet = WordNetDatabase.getFileInstance();
		} catch( Exception e ) {
			Utility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}
	}
	
	@Override
	public Lexicon buildLexicon() {
		//long id = 0;

		
		
		// Iterate through all the Synsets in the ontology lexicon to get all the wordforms.
		for( Entry<OntResource, LexiconSynSet> currentEntry : ontologyLexicon.getSynSetMap().entrySet() ) {
			LexiconSynSet currentInputSynSet = currentEntry.getValue();
			List<String> inputSynonyms = currentInputSynSet.getSynonyms();
			
			ArrayList<String> wordnetWordForms = new ArrayList<String>();
			ArrayList<String> wordnetDefinitions = new ArrayList<String>();
			
			for( String currentInputSynonym : inputSynonyms ) {
				
				ArrayList<String> currentWordForms = getAllWordForms(currentInputSynonym);
				ArrayList<String> currentDefinitions = getAllDefinitions(currentInputSynonym); // TODO: Merge with above method.  No sense in looking up the same word twice.
				
				if( currentWordForms.isEmpty() ) continue; // this word was not found in the dictionary.
				
				for( String wordform : currentWordForms ) {
					if( !wordnetWordForms.contains(wordform) ) wordnetWordForms.add(wordform);
				}
				
				for( String def : currentDefinitions ) {
					if( !wordnetDefinitions.contains(def) ) wordnetDefinitions.add(def);
				}
				
			}
				

			// Step 1.  Check if any of the wordnet word forms are in the ontology Lexicon already.
			
			ArrayList<String> uniqueWordForms = new ArrayList<String>();
			ArrayList<LexiconSynSet> duplicatedWordForms = new ArrayList<LexiconSynSet>();

			String definitionFromOnt = null; // the definition we find in the ontology lexicon. (should only be one????) TODO: Allow for multiple definitions in the ontology lexicon.
			
			for( String currentWordForm : wordnetWordForms ) {
				LexiconSynSet synList = ontologyLexicon.getSynSet(currentWordForm);
				if( synList == null ) {
					// no SynSets found in ontoloy lexicon
					uniqueWordForms.add(currentWordForm);
				} else {
					definitionFromOnt = synList.getGloss();
					duplicatedWordForms.add(synList); // NOTE: There must be at least one synset found (because of at least one common wordform)!! 
				}
			}

				
				
				
			// Step 2. Create a new synset for this class.
			GeneralLexiconSynSet wordNetNewSynSet = new GeneralLexiconSynSet(LexiconRegistry.WORDNET_LEXICON);
			
			// Step 2a. add all the wordforms to the new synset.
			for( String wordnetWordForm : uniqueWordForms ) {
				wordNetNewSynSet.addSynonym(wordnetWordForm);
			}
			
			// Step 3. Create cross links between the synsets.
			for( LexiconSynSet ontSynSet : duplicatedWordForms ) {
				ontSynSet.addRelatedSynSet(wordNetNewSynSet);
				//wordNetSynSet.addRelatedSynSet(ontSynSet);  // TODO: Is it smart to put a backlink??? It should probably go from the OntLexicon to the WordNet lexicon only.
															  // DONE: Removed the back link.  Concepts must be accessed through the ontology lexicon, and then the 
															  //       wordnet lexicon synsets can be accessed via the relatedSynSets.	
				if( wordNetNewSynSet.getOntologyConcept() != null ) wordNetNewSynSet.setOntologyConcept(ontSynSet.getOntologyConcept()); // set the ontology concept (should not need this).  It is only required for the ontology lexicon.
			}
				
				
			// Step 4. Get the definition. (Problem: for multiple wordnet synsets, which definition do we choose????????) TODO
			 					  //( TODO: Answer: a robust disambiguation solution is required here. )
			if( definitionFromOnt == null ) {
				if( !wordnetDefinitions.isEmpty() ) { wordNetNewSynSet.setGloss( wordnetDefinitions.get(0) ); } // the first definition found
				// no definitions were found
			} else {
				// the ontology definition exists.  That's fine, but we will set a wordnet definition also.
				if( !wordnetDefinitions.isEmpty() ) wordNetNewSynSet.setGloss( wordnetDefinitions.get(0)); // TODO: for multiple wordnet synsets, we need a robust disambiguation solution
				// supplement the ontology definitions with the wordnet definitions TODO
			}
				
				
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
					if( !wordFormsFound.contains( words[j] ) ) wordFormsFound.add(words[j]);
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
