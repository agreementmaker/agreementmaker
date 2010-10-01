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

		
		
		// Iterate through all the Synsets in the pervious lexicon to get all the wordforms.
		for( Entry<OntResource, LexiconSynSet> currentEntry : ontologyLexicon.getSynSetMap().entrySet() ) {
			LexiconSynSet currentInputSynSet = currentEntry.getValue();
			List<String> inputSynonyms = currentInputSynSet.getSynonyms();
			
			ArrayList<String> completeWordForms = new ArrayList<String>();
			ArrayList<String> allDefinitions = new ArrayList<String>();
			
			for( String currentInputSynonym : inputSynonyms ) {
				
				ArrayList<String> currentWordForms = getAllWordForms(currentInputSynonym);
				ArrayList<String> currentDefinitions = getAllDefinitions(currentInputSynonym); // TODO: Merge with above method.  No sense in looking up the same word twice.
				
				if( currentWordForms.isEmpty() ) continue; // this word was not found in the dictionary.
				
				for( String wordform : currentWordForms ) {
					if( !completeWordForms.contains(wordform) ) completeWordForms.add(wordform);
				}
				
				for( String def : currentDefinitions ) {
					if( !allDefinitions.contains(def) ) allDefinitions.add(def);
				}
				
			}
				
			ArrayList<String> uniqueWordForms = new ArrayList<String>();
			
			// Step 1.  Check if any of these word forms are in the input Lexicon already.
			ArrayList<LexiconSynSet> synSetsForCurrentClass = new ArrayList<LexiconSynSet>();

			String definitionFromOnt = null; // the definition we find.
			
			for( String currentWordForm : completeWordForms ) {
				LexiconSynSet synList = ontologyLexicon.getSynSet(currentWordForm);
				if( synList == null ) {
					// no SynSets found in ontoloy lexicon
					uniqueWordForms.add(currentWordForm);
				} else {
					definitionFromOnt = synList.getGloss();
					synSetsForCurrentClass.add(synList); // NOTE: There must be at least one synset found (because of at least one common wordform)!! 
				}
			}

				
				
				
			// Step 2. Create a new synset for this class.
			GeneralLexiconSynSet wordNetSynSet = new GeneralLexiconSynSet();
			
			// Step 2a. add all the wordforms to the new synset.
			for( String wordnetWordForm : uniqueWordForms ) {
				wordNetSynSet.addSynonym(wordnetWordForm);
			}
			
			// Step 3. Create cross links between the synsets.
			for( LexiconSynSet ontSynSet : synSetsForCurrentClass ) {
				ontSynSet.addRelatedSynSet(wordNetSynSet);
				wordNetSynSet.addRelatedSynSet(ontSynSet);  // TODO: Is it smart to put a backlink??? It should probably go from the OntLexicon to the WordNet lexicon only.
				if( wordNetSynSet.getOntologyConcept() != null ) wordNetSynSet.setOntologyConcept(ontSynSet.getOntologyConcept()); // set the ontology concept
			}
				
				
			// Step 4. Get the definition. (Problem: for multiple synsets, which definition do we choose????????) TODO
			if( definitionFromOnt == null ) {
				if( !allDefinitions.isEmpty() ) { wordNetSynSet.setGloss( allDefinitions.get(0) ); } // the first definition found
				// no definitions were found
			} else {
				// supplement the ontology definitions with the wordnet definitions TODO
			}
				
				
			// Done creating the SynSet.
			wordnetLexicon.addSynSet(wordNetSynSet);
			
		}
		
		
		return wordnetLexicon;
	}

	private ArrayList<String> getAllWordForms(String searchTerm) {
		ArrayList<String> wordForms = new ArrayList<String>();

		
		// lookup
		for (SynsetType t : SynsetType.ALL_TYPES) {
			Synset[] synsets = WordNet.getSynsets(searchTerm, t);
			
			for (int i = 0; i < synsets.length; i++) {
				String[] words = synsets[i].getWordForms(); // get the wordforms of this synset
				
				for (int j = 0; j < words.length; j++) {
					if( !wordForms.contains( words[j] ) ) wordForms.add(words[j]);
				}
				
			}
		}
		
		return wordForms;
	}
	
	private ArrayList<String> getAllDefinitions(String searchTerm) {
		ArrayList<String> definitions = new ArrayList<String>();

		
		// lookup
		for (SynsetType t : SynsetType.ALL_TYPES) {
			Synset[] synsets = WordNet.getSynsets(searchTerm, t);
			
			for (int i = 0; i < synsets.length; i++) {
				String words = synsets[i].getDefinition(); // get the wordforms of this synset
				if( !definitions.contains( words ) ) definitions.add(words);
			}
		}
		
		return definitions;
	}
	
}
