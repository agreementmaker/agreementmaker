package am.evaluation.repair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.utility.MatchingPair;

public class AlignmentRepairUtilities {

	private static Logger log;
	private File sourceOwl = new File("../Ontologies/OAEI/2011/anatomy/mouse.owl");
	private File targetOwl = new File("../Ontologies/OAEI/2011/anatomy/human.owl");
	private File referenceFile = new File("../Ontologies/OAEI/2011/anatomy/reference_2011.rdf");
	private File alignmentFile = new File("../Ontologies/OAEI/2011/anatomy/alignments/am_oaei_2011.rdf");
	private File outputFile = null;
	
	public AlignmentRepairUtilities(Logger log){
		this.log = log;
	}

	public Reasoner loadReasoner(OWLOntology ontology){
		Configuration configuration = new Configuration();
		configuration.tableauMonitorType = org.semanticweb.HermiT.Configuration.TableauMonitorType.NONE;

		return new Reasoner(configuration, ontology);
	}

	public OWLOntology loadOntologies(List<MatchingPair> alignment)
		throws OWLOntologyCreationException{

		OWLOntologyManager owlontologymanager = OWLManager.createOWLOntologyManager();

		// Load source and target ontologies.
		OWLOntology sourceOntology = owlontologymanager.loadOntologyFromOntologyDocument(sourceOwl);
		OWLOntology targetOntology = owlontologymanager.loadOntologyFromOntologyDocument(targetOwl);

		//Run reasoner and check if all classes are satisfiable.
		Node<OWLClass> sourceUnsatClass = loadReasoner(sourceOntology).getUnsatisfiableClasses();
		Node<OWLClass> targetUnsatClass = loadReasoner(targetOntology).getUnsatisfiableClasses();

//		log.info("What are the unsatisfiable classes in Human.owl? " + sourceUnsatClass);
//		log.info("What are the unsatisfiable classes in Mouse.owl? " + targetUnsatClass);

		//Merge the source and target ontologies
		OWLOntology mergedOntology = mergeOntology(owlontologymanager);

		return translateOntology(owlontologymanager, alignment, sourceOntology, targetOntology, 
				mergedOntology);
	}

	public OWLOntology translateOntology(OWLOntologyManager owlontologymanager, List<MatchingPair> alignment,
			OWLOntology sourceOntology, OWLOntology targetOntology, OWLOntology mergedOntology){

		//Translate all the mappings (from alignment file) into OWL Axioms (in merged ontology).
		OWLDataFactory dataFactory = owlontologymanager.getOWLDataFactory();

		for( MatchingPair currentMatchingPair : alignment ) {

			IRI sourceConceptIRI = IRI.create(currentMatchingPair.sourceURI);
			IRI targetConceptIRI = IRI.create(currentMatchingPair.targetURI);

			Set<OWLEntity> sourceEntities = sourceOntology.getEntitiesInSignature(sourceConceptIRI);
			Set<OWLEntity> targetEntities = targetOntology.getEntitiesInSignature(targetConceptIRI);

			//First we need to check if we are working with classes or properties
			if( currentMatchingPair.relation == MappingRelation.EQUIVALENCE ) {

				if( sourceEntities.size() == 1 && targetEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLClass ) {
					//We are working with classes, so we create EquivalentClass axioms
					OWLClass sourceClass = (OWLClass) sourceEntities.toArray()[0];
					OWLClass targetClass = (OWLClass) targetEntities.toArray()[0];

					OWLEquivalentClassesAxiom equivClasses = dataFactory.getOWLEquivalentClassesAxiom(sourceClass, targetClass);

					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivClasses);
					owlontologymanager.applyChanges(axiomList);
				}
				else if( sourceEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLDataProperty ) {
					//We are working with properties, so we create EquivalentDataProperty axioms
					OWLDataProperty sourceProperty = (OWLDataProperty) sourceEntities.toArray()[0];
					OWLDataProperty targetProperty = (OWLDataProperty) targetEntities.toArray()[0];

					OWLEquivalentDataPropertiesAxiom equivProperties = dataFactory.getOWLEquivalentDataPropertiesAxiom(sourceProperty, targetProperty);

					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivProperties);
					owlontologymanager.applyChanges(axiomList);
				}
				else if( sourceEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLObjectProperty ) {
					//We are working with properties, so we create EquivalentObjectProperty axioms
					OWLObjectProperty sourceProperty = (OWLObjectProperty) sourceEntities.toArray()[0];
					OWLObjectProperty targetProperty = (OWLObjectProperty) targetEntities.toArray()[0];

					OWLEquivalentObjectPropertiesAxiom equivProperties = dataFactory.getOWLEquivalentObjectPropertiesAxiom(sourceProperty, targetProperty);

					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivProperties);
					owlontologymanager.applyChanges(axiomList);
				}
			}
		}
		try {
			if( outputFile == null ) outputFile = File.createTempFile("output", "owl");
			owlontologymanager.saveOntology(mergedOntology, new RDFXMLOntologyFormat(), 
					IRI.create("file:" + outputFile.getAbsolutePath()));

		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mergedOntology;
	}

	public OWLOntology mergeOntology(OWLOntologyManager owlontologymanager){
		//Call the OWL merger to merge the 2 ontologies
		OWLOntologyMerger merger = new OWLOntologyMerger(owlontologymanager);
		OWLOntology mergedOntology = null;

		try {
			mergedOntology = merger.createMergedOntology(owlontologymanager, IRI.create("http://output.owl"));

			/*//Print out the axioms
			log.info("Printing axioms in merged ontology....\n");
			for(OWLAxiom axiom : mergedOntology.getAxioms()) {
				log.info(axiom);
			}*/
			//Save the merged ontology to OWL file
			if( outputFile == null ) outputFile = File.createTempFile("output", "owl");
			owlontologymanager.saveOntology(mergedOntology, new RDFXMLOntologyFormat(), 
					IRI.create("file:" + outputFile.getAbsolutePath()));

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mergedOntology;
	}

	public Double computeMeasures(String toEvaluate, String reference){
		int count = 0;

		ReferenceAlignmentMatcher matcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters param = new ReferenceAlignmentParameters();

		param.fileName = toEvaluate;
		matcher.setParam(param);

		ArrayList<MatchingPair> filePairs = null;
		ArrayList<MatchingPair> refPairs = null;

		try {
			filePairs = matcher.parseStandardOAEI();
			param.fileName = reference.toString();
			refPairs = matcher.parseStandardOAEI();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		for (MatchingPair p1 : filePairs) {
			for (MatchingPair p2 : refPairs) {
				if(p1.sourceURI.equals(p2.sourceURI) && p1.targetURI.equals(p2.targetURI)
						&& p1.relation.equals(p2.relation)){
					count++;
					break;
				}
			}
		}
		double precision = (double)count/filePairs.size();
		double recall = (float)count/refPairs.size();
		double fmeasure = 2 * precision * recall / (precision + recall);
		log.info("Precision: " + precision + " Recall: " + recall + " FMeasure: " + fmeasure);
		System.out.println(" ");

		return precision;
	}
}
