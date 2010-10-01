package am.app.mappingEngine.LexicalSynonymMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import am.Utility;
import am.app.Core;
import am.app.lexicon.GeneralLexicon;
import am.app.lexicon.GeneralLexiconSynSet;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.lexicon.ontology.OntologyLexiconBuilder;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class LexicalSynonymMatcher extends AbstractMatcher {
	
	
	Lexicon sourceLexicon;
	Lexicon targetLexicon;
	
	Property sourceSynonymProperty, targetSynonymProperty;
	Property sourceLabelProperty, targetLabelProperty;
	Property sourceDefinitionProperty, targetDefinitionProperty;

	
	HashMap<Node, GeneralLexiconSynSet> sourceSynsetLookup = new HashMap<Node, GeneralLexiconSynSet>();
	HashMap<Node, GeneralLexiconSynSet> targetSynsetLookup = new HashMap<Node, GeneralLexiconSynSet>();
	
	
/**
 * PRE PROCESSING STEP
 * 
 * 1) Build the Ontology Lexicons.
 */
	int alignmentsfound = 0;
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		

		sourceLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( sourceOntology.getID(), LexiconRegistry.WORDNET_LEXICON );
		
		if( sourceLexicon == null ) {
			OntModel sourceModel = sourceOntology.getModel();
			
			// the synonym property, label property and definition property
			sourceSynonymProperty = sourceModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym");
			sourceLabelProperty = sourceModel.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
			sourceDefinitionProperty = sourceModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasDefinition");
			
			OntologyLexiconBuilder sourceOLB = new OntologyLexiconBuilder(sourceOntology, sourceLabelProperty, sourceSynonymProperty, sourceDefinitionProperty);
			
			sourceLexicon = sourceOLB.buildLexicon();
			sourceLexicon.settOntologyID(sourceOntology.getID());
			
			Core.getLexiconStore().registerLexicon(sourceLexicon); // register for reuse by other matchers
		}		
		
		targetLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( targetOntology.getID(), LexiconRegistry.WORDNET_LEXICON ); 
		
		if( targetLexicon == null ) {
			
			OntModel targetModel = targetOntology.getModel();
	
			// the synonym property, label property and definition property
			targetSynonymProperty = targetModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym");	
			targetLabelProperty = targetModel.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
			targetDefinitionProperty = targetModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasDefinition");
			
			// STEP 1: Let's build the lexicons for the ontology
			OntologyLexiconBuilder targetOLB = new OntologyLexiconBuilder(targetOntology, targetLabelProperty, targetSynonymProperty, targetDefinitionProperty);

			targetLexicon = targetOLB.buildLexicon();
			targetLexicon.settOntologyID(targetOntology.getID());
			
			Core.getLexiconStore().registerLexicon(targetLexicon);
		}
		
		//sourceLexicon.print( System.out );
		//targetLexicon.print( System.out );
		
		if( !Utility.displayConfirmPane("waiting for continue", "continue?") ) cancel(true);
		
	}
/*
	private void buildLexicon( Lexicon destinationLexicon, Ontology currentOntology, Property synonymProperty, Property labelProperty,
			Property definitionProperty) throws Exception {

		//long id = 0;
		// Iterate through all the classes of the current ontology
		Iterator<Node> classesIterator = currentOntology.getClassesList().iterator();
		while( classesIterator.hasNext() ) {
			Node currentClassNode = classesIterator.next();
			OntClass currentClass = currentClassNode.getResource().as(OntClass.class);
			
			ArrayList<String> synonyms = getAllSynonyms( currentClass, labelProperty, synonymProperty );
			if( synonyms.isEmpty() ) continue; // skip this class if it has no synonyms
			
			// Step 1.  Check if any of these word forms are in the Lexicon already. (probably don't need to do this???)
			
			ArrayList<LexiconSynSet> synSetsForCurrentClass = new ArrayList<LexiconSynSet>();
			Iterator<String> currentSynonym = synonyms.iterator();
			while( currentSynonym.hasNext() ) {
				String syn = currentSynonym.next();
				LexiconSynSet synList = destinationLexicon.getSynSet(syn);
				
				if( synList == null ) continue; // synset not found
				
				// we have found a synset with this word form (should not happen??)
				synSetsForCurrentClass.add(synList);
			}
			
			
			// Step 2. Create a new synset for this class.
			GeneralLexiconSynSet classSynSet = new GeneralLexiconSynSet();
			
			// add all the synonyms to the synset
			Iterator<String> synonymIter = synonyms.iterator();
			while( synonymIter.hasNext() ) { classSynSet.addSynonym(synonymIter.next()); }
			
			// keep track of what concept these synonyms are for
			classSynSet.setOntologyConcept(currentClass.as(OntResource.class));
			
			
			// check for a definition
			if( definitionProperty != null ) {
				Statement definition = currentClass.getProperty(definitionProperty);
				if( definition != null ) {
					RDFNode def = definition.getObject();
					if( def.canAs( Literal.class ) ) {
						Literal defLiteral = def.asLiteral();
						classSynSet.setGloss(defLiteral.getString());
					} else if ( def.canAs( Individual.class ) ) {
						Individual defIndividual = def.as(Individual.class);
						classSynSet.setGloss(defIndividual.getLabel(null));
					} else {
						throw new Exception("Cannot understand the definition property.");
					}
				}
			}

			
			// add links between the two synsets
			for( LexiconSynSet currentSynSet: synSetsForCurrentClass ) {
				classSynSet.addRelatedSynSet(currentSynSet);
				currentSynSet.addRelatedSynSet(classSynSet);
			}
			
			// done creating the syn set
			destinationLexicon.addSynSet(classSynSet); // This probably does NOT need to be done.
			
			
		} // while		
	}
*/
	/**
	 * Return a list of all the synonyms associated with a class (label + other synonyms)
	 * @param currentClass
	 * @return
	 * @throws Exception 
	 */
/*
	private ArrayList<String> getAllSynonyms(OntClass currentClass, Property labelProperty, Property synProperty) throws Exception {
		ArrayList<String> synList = new ArrayList<String>();
		
		StmtIterator currentLabels = currentClass.listProperties(labelProperty);
		while( currentLabels.hasNext() ) {
			Statement currentLabel = currentLabels.nextStatement();
			RDFNode label = currentLabel.getObject();
			if( label.canAs(Literal.class) ) {
				// the label points to a literal.  (This should always be the case.)
				Literal labelLiteral = label.asLiteral();
				synList.add(labelLiteral.getString());
			}
		}
		
		
		StmtIterator currentSynonyms = currentClass.listProperties(synProperty);
		while( currentSynonyms.hasNext() ) {
			Statement currentSynonym = currentSynonyms.nextStatement();
			
			// expecting annotation or datatype property (i.e. points to a literal)
			RDFNode syn = currentSynonym.getObject();
			
			if( syn.canAs(Literal.class) ) {
				Literal synLiteral = syn.asLiteral();
				synList.add(synLiteral.getString());
			} else  if( syn.canAs(Individual.class) ){
				Individual synIndividial = syn.as(Individual.class);
				synList.add( synIndividial.getLabel(null) );
			} else {
				throw new Exception("WTF is this?");
			}
		}
		
		return synList;
	}
	
	
*/	
	
	
	
/**
 * MATCHING WITH SYNONYMS
 */
	
	@Override
	protected Alignment alignTwoNodes(Node source, Node target,
			alignType typeOfNodes) throws Exception {


		
		OntResource sourceOR = source.getResource().as(OntResource.class);
		OntResource targetOR = target.getResource().as(OntResource.class);
		
		LexiconSynSet sourceSet = sourceLexicon.getSynSet(sourceOR);
		LexiconSynSet targetSet = targetLexicon.getSynSet(targetOR);
		
		double synonymSimilarity = synonymSimilarity( sourceSet, targetSet );
		
		if( synonymSimilarity > 0.0d ) {
			alignmentsfound++;
			return new Alignment(source, target, synonymSimilarity);
		}
		return null;
	}

	private double synonymSimilarity(LexiconSynSet sourceSet, LexiconSynSet targetSet) {
	
		double greatestLexicalSimilarity = 0.0d;
		
		try {
			greatestLexicalSimilarity = computeLexicalSimilarity(sourceSet, targetSet);
			
			List<LexiconSynSet> sourceRelatedSets = sourceSet.getRelatedSynSets();
			List<LexiconSynSet> targetRelatedSets = targetSet.getRelatedSynSets();
			
			for( LexiconSynSet sourceRelatedSet : sourceRelatedSets ) {
				for( LexiconSynSet targetRelatedSet : targetRelatedSets ) {
					double computedLexicalSimilarity = computeLexicalSimilarity(sourceRelatedSet, targetRelatedSet);
					if( computedLexicalSimilarity > greatestLexicalSimilarity ) greatestLexicalSimilarity = computedLexicalSimilarity;
				}
			}
		} catch( NullPointerException e ) {
			e.printStackTrace();
		}
		
/*
		Iterator<LexiconSynSet> sourceSetsIter = sourceSets.iterator();
		while( sourceSetsIter.hasNext() ) {
			LexiconSynSet sourceLexicon = sourceSetsIter.next();
			Iterator<LexiconSynSet> targetSetsIter = targetSets.iterator();
			while( targetSetsIter.hasNext() ) {
				LexiconSynSet targetLexicon = targetSetsIter.next();
				
				double computedLexicalSimilarity = computeLexicalSimilarity(sourceLexicon, targetLexicon);
				if( computedLexicalSimilarity > greatestLexicalSimilarity ) greatestLexicalSimilarity = computedLexicalSimilarity;
			}
		}
*/		
		return greatestLexicalSimilarity;
	}

	private double computeLexicalSimilarity(LexiconSynSet sourceLexicon2,
			LexiconSynSet targetLexicon2) throws NullPointerException {


		if( sourceLexicon2 == null ) throw new NullPointerException("Source lexicon is null.");
		if( targetLexicon2 == null ) throw new NullPointerException("Target lexicon is null.");
		
		
		double greatestWordSimilarity = 0.0d;

		
		boolean breakout = false;
		List<String> sourceSyns = sourceLexicon2.getSynonyms();
		for( int i = 0; i < sourceSyns.size(); i++ ) {
			String sourceSynonym = sourceSyns.get(i);
			List<String> targetSyns = targetLexicon2.getSynonyms();
			for( int j = 0; j < targetSyns.size(); j++ ) {
				String targetSynonym = targetSyns.get(j);
				
				if( sourceSynonym.equalsIgnoreCase(targetSynonym) ) {
					greatestWordSimilarity = 1.0d;
					breakout = true;
					break;
				}
			}
			if( breakout ) break;
		}
		
/*		
		Iterator<String> sourceSynonyms = sourceLexicon2.getSynonyms().iterator();
		boolean breakout = false;
		while( sourceSynonyms.hasNext() ) {
			String sourceSynonym = sourceSynonyms.next();
			Iterator<String> targetSynonyms = targetLexicon2.getSynonyms().iterator();
			while( targetSynonyms.hasNext() ) {
				String targetSynonym = targetSynonyms.next();
				
				String cleanedSS = BaseSimilarityMatcher.removeLines(sourceSynonym);
				String cleanedTS = BaseSimilarityMatcher.removeLines(targetSynonym);
				
				if( cleanedSS.equalsIgnoreCase(cleanedTS) ) {
					greatestWordSimilarity = 1.0d;
					breakout = true;
					break;
				}
				
			}
			if( breakout ) { break; }
		}
*/	
		return greatestWordSimilarity;
	}
	
}
