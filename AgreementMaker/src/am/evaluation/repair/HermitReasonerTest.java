package am.evaluation.repair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.utility.referenceAlignment.AlignmentUtilities;
import am.utility.referenceAlignment.MappingsOutput;

/**
 * @author Renu Srinivasan, Ramya Kannan
 *
 *	This class checks for inconsistency in the alignment between a pair of source and target ontologies,
 *	and repairs it (by removal), if any.
 */
public class HermitReasonerTest {

	private static Logger log = Logger.getLogger(HermitReasonerTest.class);
//	private File referenceFile = new File("C://Users/Renu/Desktop/reference.rdf");
//	private File sourceOwl = new File("C:/Users/Renu/Desktop/mouse.owl");
//	private File targetOwl = new File("C:/Users/Renu/Desktop/human.owl");
	
	private File referenceFile = new File("../Ontologies/OAEI/2011/anatomy/reference_2011.rdf");
	private File sourceOwl = new File("../Ontologies/OAEI/2011/anatomy/mouse.owl");
	private File targetOwl = new File("../Ontologies/OAEI/2011/anatomy/human.owl");
	
//	private List<OWLAxiom> translationAxiomList = new ArrayList<OWLAxiom>();
	private HashMap<OWLObject, OWLAxiom> transAxioms = new HashMap<OWLObject, OWLAxiom>();

	public static void main(String[] args) throws OWLOntologyCreationException {
		DOMConfigurator.configure("log4j.xml");
		log.setLevel(Level.DEBUG);

		new HermitReasonerTest().repairAlignment();
		
		System.out.println("Done...");
	}
	
	public void repairAlignment(){

		AlignmentRepairUtilities util = new AlignmentRepairUtilities(log);
//		OWLOntologyManager manager = null;
		
		//Load the Reference Alignment.
//		File alignmentFile = new File("C:/Users/Renu/Desktop/alignment.rdf");
		File repairedAlignmentFile = new File("../Ontologies/OAEI/2011/test/repairedalignment.rdf");

		File alignmentFile = new File("../Ontologies/OAEI/2011/anatomy/alignments/am_oaei_2011.rdf");

		List<MatchingPair> alignment = AlignmentUtilities.getMatchingPairsOAEI(alignmentFile.getAbsolutePath());
		log.debug("Loaded alignment. The alignment contains " + alignment.size() + " mappings.");

		double precision = util.computeMeasures(alignmentFile.toString(), referenceFile.toString());

		Node<OWLClass> unsatClasses;
		List<MatchingPair> unsatAlignments = new ArrayList<MatchingPair>();
		OWLOntology translatedOntology = null;
		Reasoner transOntReasoner = null;

		try {
			translatedOntology = loadOntologies(alignment, util);
			transOntReasoner = util.getReasoner(translatedOntology);
			unsatClasses = transOntReasoner.getUnsatisfiableClasses();
			log.debug(unsatClasses.getSize());
			
			//Repair the alignment (only saving the erroneous matching pairs)
			for(OWLClass unsatClass: unsatClasses){
				if(! unsatClass.isBottomEntity()){
					Iterator<MatchingPair> mpIter = alignment.iterator();
					while(mpIter.hasNext()){
						MatchingPair pair = mpIter.next();
						if(pair.sourceURI.equals(unsatClass.getIRI().toString()) || pair.targetURI.equals(unsatClass.getIRI().toString())){
							//Save the unsatisfiable alignment pairs.
							unsatAlignments.add(pair);
							mpIter.remove();
						}
					}
				}
			}
			//Load&merge the source and target ontologies again. And run reasoner on this repaired alignment.
			OWLOntology mergedOntology = loadOntologies(alignment, util);
//			Node<OWLClass> unsatClassesFromRepaired = util.reason(mergedOntology);
//			log.debug(unsatClassesFromRepaired);

			List<MatchingPair> inconsistentPair = new ArrayList<MatchingPair>();
			Reasoner r = null;
			
			
			for(MatchingPair unsatPair : unsatAlignments){
				List<MatchingPair> repairedAlignment = AlignmentUtilities.getMatchingPairsOAEI(alignmentFile.getAbsolutePath());
				repairedAlignment.add(unsatPair);
				
				System.out.println("Adding back axiom: " + unsatPair.toString());
				
				mergedOntology = loadOntologies(repairedAlignment, util);
				r = util.getReasoner(mergedOntology);
				Node<OWLClass> unsatClassesFromRepaired = r.getUnsatisfiableClasses();
				
				if(unsatClassesFromRepaired.getSize() > 1){
					repairedAlignment.remove(unsatPair);
					inconsistentPair.add(unsatPair);
					System.out.println("Removing axiom: " + unsatPair.toString());
					
				}
				
//				System.out.println("");
				MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
				util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());
			}
			
			System.out.println("Finished Repair. Saving alignment..");
			MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
			util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());
			
			
			//Save this repaired alignment to a new file
//			MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
//			Double repairedPrecision = util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());

			/*Double repairedPrecision = null;
			
			Node<OWLClass> unsatClassesFromRepaired = null;
			OWLOntology mergedOntology = null;
			
			
			//Repair the merged ontology
			
			//Remove axiom from merged ontology (corresponding to each unsatisfiable alignment pair),
			//and check if its satisfiable and if the precision has improved.
			for(MatchingPair unsatPair : unsatAlignments){
				Set<OWLEntity> sourceEntities = translatedOntology.getEntitiesInSignature(IRI.create(unsatPair.sourceURI));
				Set<OWLEntity> targetEntities = translatedOntology.getEntitiesInSignature(IRI.create(unsatPair.targetURI));
				
				if( sourceEntities.size() == 1 && targetEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLClass ) {
					Set<OWLEquivalentClassesAxiom> ax = translatedOntology.getEquivalentClassesAxioms((OWLClass)sourceEntities.toArray()[0]);

					for(OWLEquivalentClassesAxiom a : ax){
						Iterator<OWLClass> c = a.getClassesInSignature().iterator();
						while(c.hasNext()){
							OWLClass classname = c.next();
							if(classname.getIRI().toString().equals(unsatPair.sourceURI) || classname.getIRI().toString().equals(unsatPair.targetURI)){
								System.out.println("......");
								List<OWLOntologyChange> changes = translatedOntology.getOWLOntologyManager().removeAxiom(translatedOntology, a);

								translatedOntology.getOWLOntologyManager().applyChanges(changes);
							}
						}
					}
				}
				//Run the reasoner on this repaired merged ontology
				transOntReasoner = util.getReasoner(translatedOntology);
				unsatClassesFromRepaired = transOntReasoner.getUnsatisfiableClasses();
				
				log.debug(unsatClassesFromRepaired);
				
				
				System.out.println("Removing mapping: " + unsatPair.toString());
				alignment.remove(unsatPair);
				
				MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
				repairedPrecision = util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());
				
				if(unsatClassesFromRepaired.getSize() <= 1){
					break;
				}*/
/*
			remove the axiom from ontology. save it.
			
*/
				
			}
			
		catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
	
	public OWLOntology loadOntologies(List<MatchingPair> alignment, AlignmentRepairUtilities util)
		throws OWLOntologyCreationException{
		
		OWLOntologyManager owlontologymanager = OWLManager.createOWLOntologyManager();
		
		// Load the ontologies.
		OWLOntology sourceOntology = owlontologymanager.loadOntologyFromOntologyDocument(sourceOwl);
		OWLOntology targetOntology = owlontologymanager.loadOntologyFromOntologyDocument(targetOwl);
		
		//Run reasoner and check to make sure everything is satisfiable.
		Node<OWLClass> sourceUnsatClass = util.getReasoner(sourceOntology).getUnsatisfiableClasses();
		Node<OWLClass> targetUnsatClass = util.getReasoner(targetOntology).getUnsatisfiableClasses();

		log.debug("What are the unsatisfiable classes in Human.owl? " + sourceUnsatClass + "\n");
		log.debug("What are the unsatisfiable classes in Mouse.owl? " + targetUnsatClass + "\n");

		//Merge the source and target ontologies
		OWLOntology mergedOntology = util.mergeOntology(owlontologymanager);
		
		return translateOntology(owlontologymanager, alignment, sourceOntology, targetOntology, 
				mergedOntology, util);
	}
	
	public OWLOntology translateOntology(OWLOntologyManager owlontologymanager, List<MatchingPair> alignment,
			OWLOntology sourceOntology, OWLOntology targetOntology, OWLOntology mergedOntology,
			AlignmentRepairUtilities util){
		
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
//					translationAxiomList.add(equivClasses);
					transAxioms.put(sourceClass, equivClasses);
					transAxioms.put(targetClass, equivClasses);
					
					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivClasses);
					owlontologymanager.applyChanges(axiomList);

				}
				else if( sourceEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLDataProperty ) {
					//We are working with properties, so we create EquivalentDataProperty axioms
					OWLDataProperty sourceProperty = (OWLDataProperty) sourceEntities.toArray()[0];
					OWLDataProperty targetProperty = (OWLDataProperty) targetEntities.toArray()[0];

					OWLEquivalentDataPropertiesAxiom equivProperties = dataFactory.getOWLEquivalentDataPropertiesAxiom(sourceProperty, targetProperty);
//					translationAxiomList.add(equivProperties);
					transAxioms.put(sourceProperty, equivProperties);
					transAxioms.put(targetProperty, equivProperties);

					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivProperties);
					owlontologymanager.applyChanges(axiomList);
				}
				else if( sourceEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLObjectProperty ) {
					//We are working with properties, so we create EquivalentObjectProperty axioms
					OWLObjectProperty sourceProperty = (OWLObjectProperty) sourceEntities.toArray()[0];
					OWLObjectProperty targetProperty = (OWLObjectProperty) targetEntities.toArray()[0];

					OWLEquivalentObjectPropertiesAxiom equivProperties = dataFactory.getOWLEquivalentObjectPropertiesAxiom(sourceProperty, targetProperty);
//					translationAxiomList.add(equivProperties);
					transAxioms.put(sourceProperty, equivProperties);
					transAxioms.put(targetProperty, equivProperties);

					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivProperties);
					owlontologymanager.applyChanges(axiomList);
				}
			}
			else {
				log.debug("Write Code!!");
			}

		}
		try {
			owlontologymanager.saveOntology(mergedOntology, new RDFXMLOntologyFormat(), IRI.create("file:/output.owl"));
			
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
		
		//Derive subclass equivalences
		/*for( MatchingPair currentMatchingPair : alignment ) {
			IRI sourceConceptIRI = IRI.create(currentMatchingPair.sourceURI);
			IRI targetConceptIRI = IRI.create(currentMatchingPair.targetURI);

			Set<OWLEntity> sourceEntities = sourceOntology.getEntitiesInSignature(sourceConceptIRI);
			Set<OWLEntity> targetEntities = targetOntology.getEntitiesInSignature(targetConceptIRI);
			
			if(sourceEntities.size() == 1 && targetEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLClass ) {
				mergedOntology.getObjectSubPropertyAxiomsForSuperProperty((OWLObjectPropertyExpression) sourceEntities.toArray()[0]);
			}
			
		}*/
		
		return mergedOntology;
	}
}
