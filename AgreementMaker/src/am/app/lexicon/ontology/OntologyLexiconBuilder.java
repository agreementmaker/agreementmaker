package am.app.lexicon.ontology;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import am.app.lexicon.GeneralLexicon;
import am.app.lexicon.GeneralLexiconSynSet;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconBuilder;
import am.app.lexicon.LexiconSynSet;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Build a lexicon from an ontology (via synonyms and definitions in the ontology).
 * @author cosmin
 *
 */
public class OntologyLexiconBuilder implements LexiconBuilder {

	List<Property> synonymProperty;
	List<Property> labelProperty;
	List<Property> definitionProperty;

	Ontology currentOntology;
	
	boolean includeLocalname;
	
	Lexicon currentLexicon;
	
	public OntologyLexiconBuilder( Ontology ont, boolean includeLN, List<Property> label, List<Property> synonym, List<Property> definition ) {
		currentOntology = ont;
		currentLexicon = new GeneralLexicon( LexiconRegistry.ONTOLOGY_LEXICON );
		
		synonymProperty = synonym;
		labelProperty = label;
		definitionProperty = definition;
		includeLocalname = includeLN;
	}
	
	public Lexicon buildLexicon() {
		{
			//long id = 0;
			// Iterate through all the classes of the current ontology
			Iterator<Node> classesIterator = currentOntology.getClassesList().iterator();
			while( classesIterator.hasNext() ) {
				try {
					Node currentClassNode = classesIterator.next();
					OntClass currentClass = currentClassNode.getResource().as(OntClass.class);

					ArrayList<String> synonyms = getAllSynonyms( currentClass, labelProperty, synonymProperty );
					if( synonyms.isEmpty() ) continue; // skip this class if it has no synonyms

					// Step 1.  Check if any of these word forms are in the Lexicon already. (probably don't need to do this???)

					ArrayList<LexiconSynSet> synSetsForCurrentClass = new ArrayList<LexiconSynSet>();
					Iterator<String> currentSynonym = synonyms.iterator();
					while( currentSynonym.hasNext() ) {
						String syn = currentSynonym.next();
						LexiconSynSet synList = currentLexicon.getSynSet(syn);

						if( synList == null ) continue; // synset not found

						// we have found a synset with this word form (should not happen??)
						synSetsForCurrentClass.add(synList);
					}


					// Step 2. Create a new synset for this class.
					GeneralLexiconSynSet classSynSet = new GeneralLexiconSynSet(LexiconRegistry.ONTOLOGY_LEXICON);

					// add all the synonyms to the synset
					Iterator<String> synonymIter = synonyms.iterator();
					while( synonymIter.hasNext() ) { classSynSet.addSynonym(synonymIter.next()); }

					// keep track of what concept these synonyms are for
					classSynSet.setOntologyConcept(currentClass.as(OntResource.class));


					try {
						for( Property currentProperty : definitionProperty ) {
							// check for a definition
							if( definitionProperty != null ) {
								Statement definition = currentClass.getProperty(currentProperty);
								if( definition != null ) {
									RDFNode def = definition.getObject();
									if( def.canAs( Literal.class ) ) {
										Literal defLiteral = def.asLiteral();
										classSynSet.setGloss(defLiteral.getString());
									} else if ( def.canAs( Individual.class ) ) {
										Individual defIndividual = def.as(Individual.class);
										classSynSet.setGloss(defIndividual.getLabel(null));
									} else {
										throw new Exception("Cannot understand the definition property (" + currentProperty.getLocalName() + ").");
									}
								}
							}
						}
					} catch( Exception e ) {
						e.printStackTrace();
					}


					// add links between the two synsets
					for( LexiconSynSet currentSynSet: synSetsForCurrentClass ) {
						classSynSet.addRelatedSynSet(currentSynSet);
						currentSynSet.addRelatedSynSet(classSynSet);
					}

					// done creating the syn set
					currentLexicon.addSynSet(classSynSet); // This probably does NOT need to be done.
				} catch( Exception e ) {
					e.printStackTrace(); // just skip this term
				}

			} // while		
		}
		
		Iterator<Node> propertiesIterator = currentOntology.getPropertiesList().iterator();
		while( propertiesIterator.hasNext() ) {
			try {
				Node currentPropertyNode = propertiesIterator.next();
				OntProperty currentClass = currentPropertyNode.getResource().as(OntProperty.class);
				
				ArrayList<String> synonyms = getAllSynonyms( currentClass, labelProperty, synonymProperty );
				if( synonyms.isEmpty() ) continue; // skip this class if it has no synonyms
				
				// Step 1.  Check if any of these word forms are in the Lexicon already. (probably don't need to do this???)
				
				ArrayList<LexiconSynSet> synSetsForCurrentClass = new ArrayList<LexiconSynSet>();
				Iterator<String> currentSynonym = synonyms.iterator();
				while( currentSynonym.hasNext() ) {
					String syn = currentSynonym.next();
					LexiconSynSet synList = currentLexicon.getSynSet(syn);
					
					if( synList == null ) continue; // synset not found
					
					// we have found a synset with this word form (should not happen??)
					synSetsForCurrentClass.add(synList);
				}
				
				
				// Step 2. Create a new synset for this class.
				GeneralLexiconSynSet propertySynSet = new GeneralLexiconSynSet(LexiconRegistry.ONTOLOGY_LEXICON);
				
				// add all the synonyms to the synset
				Iterator<String> synonymIter = synonyms.iterator();
				while( synonymIter.hasNext() ) { propertySynSet.addSynonym(synonymIter.next()); }
				
				// keep track of what concept these synonyms are for
				propertySynSet.setOntologyConcept(currentClass.as(OntResource.class));
				
				
				try {
					for( Property currentProperty : definitionProperty ) {
						// check for a definition
						if( definitionProperty != null ) {
							Statement definition = currentClass.getProperty(currentProperty);
							if( definition != null ) {
								RDFNode def = definition.getObject();
								if( def.canAs( Literal.class ) ) {
									Literal defLiteral = def.asLiteral();
									propertySynSet.setGloss(defLiteral.getString());
								} else if ( def.canAs( Individual.class ) ) {
									Individual defIndividual = def.as(Individual.class);
									propertySynSet.setGloss(defIndividual.getLabel(null));
								} else {
									throw new Exception("Cannot understand the definition property (" + currentProperty.getLocalName() + ").");
								}
							}
						}
					}
				} catch( Exception e ) {
					e.printStackTrace();
				}
	
				
				// add links between the two synsets
				for( LexiconSynSet currentSynSet: synSetsForCurrentClass ) {
					propertySynSet.addRelatedSynSet(currentSynSet);
					currentSynSet.addRelatedSynSet(propertySynSet);
				}
				
				// done creating the syn set
				currentLexicon.addSynSet(propertySynSet); // This probably does NOT need to be done.
			} catch( Exception e ) {
				e.printStackTrace(); // just skip this term
			}
			
		} // while	
		
		
		return currentLexicon;
	}


	/**
	 * Return a list of all the synonyms associated with a class (label + other synonyms)
	 * @param currentClass
	 * @return
	 * @throws Exception 
	 */
	private ArrayList<String> getAllSynonyms(OntClass currentClass, List<Property> labelProperty, List<Property> synProperty) throws Exception {
		ArrayList<String> synList = new ArrayList<String>();
		
		if( includeLocalname ) {
			synList.add( currentClass.getLocalName() );
		}
		
		for( Property currentProperty : labelProperty ) {
			StmtIterator currentLabels = currentClass.listProperties(currentProperty);
			while( currentLabels.hasNext() ) {
				Statement currentLabel = currentLabels.nextStatement();
				RDFNode label = currentLabel.getObject();
				if( label.canAs(Literal.class) ) {
					// the label points to a literal.  (This should always be the case.)
					Literal labelLiteral = label.asLiteral();
					String labelString = labelLiteral.getString();
					if( !synList.contains(labelString) ) synList.add(labelString);
				}
			}
		}
		
		for( Property currentProperty : synProperty ) {
			StmtIterator currentSynonyms = currentClass.listProperties(currentProperty);
			while( currentSynonyms.hasNext() ) {
				Statement currentSynonym = currentSynonyms.nextStatement();
				
				// expecting annotation or datatype property (i.e. points to a literal)
				RDFNode syn = currentSynonym.getObject();
				
				if( syn.canAs(Literal.class) ) {
					Literal synLiteral = syn.asLiteral();
					String synString = synLiteral.getString(); 
					if( !synList.contains(synString) ) synList.add( synString );
				} else  if( syn.canAs(Individual.class) ){
					Individual synIndividial = syn.as(Individual.class);
					String synString = synIndividial.getLabel(null);
					if( !synList.contains(synString) ) synList.add( synString );
				} else {
					throw new Exception("Cannot understand the synonym property (" + currentSynonym.getPredicate().getLocalName() + ").");
				}
			}
		}
		
		return synList;
	}

	
	/**
	 * Return a list of all the synonyms associated with a class (label + other synonyms)
	 * @param currentClass
	 * @return
	 * @throws Exception 
	 */
	private ArrayList<String> getAllSynonyms(OntProperty currentClass, List<Property> labelProperty, List<Property> synProperty) throws Exception {
		ArrayList<String> synList = new ArrayList<String>();
		
		if( includeLocalname ) {
			synList.add( currentClass.getLocalName() );
		}
		
		for( Property currentProperty : labelProperty ) {
			StmtIterator currentLabels = currentClass.listProperties(currentProperty);
			while( currentLabels.hasNext() ) {
				Statement currentLabel = currentLabels.nextStatement();
				RDFNode label = currentLabel.getObject();
				if( label.canAs(Literal.class) ) {
					// the label points to a literal.  (This should always be the case.)
					Literal labelLiteral = label.asLiteral();
					String labelString = labelLiteral.getString();
					if( !synList.contains(labelString) ) synList.add(labelString);
				}
			}
		}
		
		for( Property currentProperty : synProperty ) {
			StmtIterator currentSynonyms = currentClass.listProperties(currentProperty);
			while( currentSynonyms.hasNext() ) {
				Statement currentSynonym = currentSynonyms.nextStatement();
				
				// expecting annotation or datatype property (i.e. points to a literal)
				RDFNode syn = currentSynonym.getObject();
				
				if( syn.canAs(Literal.class) ) {
					Literal synLiteral = syn.asLiteral();
					String synString = synLiteral.getString(); 
					if( !synList.contains(synString) ) synList.add( synString );
				} else  if( syn.canAs(Individual.class) ){
					Individual synIndividial = syn.as(Individual.class);
					String synString = synIndividial.getLabel(null);
					if( !synList.contains(synString) ) synList.add( synString );
				} else {
					throw new Exception("Cannot understand the synonym property (" + currentSynonym.getPredicate().getLocalName() + ").");
				}
			}
		}
		
		return synList;
	}
}
