package am.evaluation.repair;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
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
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import uk.ac.manchester.cs.owl.owlapi.OWLClassExpressionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;

import com.hp.hpl.jena.iri.IRIFactory;

import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.utility.referenceAlignment.AlignmentUtilities;
import am.utility.referenceAlignment.MappingsOutput;

/*
 * This is a test class in order to become familiar with the HermiT reasoner.
 */
public class HermitReasonerTest {

	private static Logger log = Logger.getLogger(HermitReasonerTest.class);

	public static void main(String[] args) throws OWLOntologyCreationException {
		//DOMConfigurator.configure("log4j.xml");
		log.setLevel(Level.DEBUG);

		try {
			new HermitReasonerTest().repairAlignment();

		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Node<OWLClass> loadOntologies(List<MatchingPair> alignment) throws OWLOntologyCreationException{
		OWLOntologyManager owlontologymanager = OWLManager.createOWLOntologyManager();
		Configuration configuration = new Configuration();
		configuration.tableauMonitorType = org.semanticweb.HermiT.Configuration.TableauMonitorType.TIMING;

		// Load the ontologies.
		OWLOntology sourceOntology = owlontologymanager.loadOntologyFromOntologyDocument(
				new File("../Ontologies/OAEI/2011/anatomy/mouse.owl"));

		OWLOntology targetOntology = owlontologymanager.loadOntologyFromOntologyDocument(
				new File("../Ontologies/OAEI/2011/anatomy/human.owl"));

		// create the reasoners for our ontologies.
		Reasoner sourceReasoner = new Reasoner(configuration, sourceOntology);
		Reasoner targetReasoner = new Reasoner(configuration, targetOntology);

		// check to make sure everything is satisfiable.
		Node<OWLClass> sourceUnsatClass = sourceReasoner.getUnsatisfiableClasses();
		Node<OWLClass> targetUnsatClass = targetReasoner.getUnsatisfiableClasses();

		log.debug("What are the unsatisfiable classes in Human.owl? " + sourceUnsatClass + "\n");
		log.debug("What are the unsatisfiable classes in Mouse.owl? " + targetUnsatClass + "\n");

		return translateOntology(owlontologymanager, alignment, sourceOntology, targetOntology);
	}
	
	public Node<OWLClass> translateOntology(OWLOntologyManager owlontologymanager, List<MatchingPair> alignment,
			OWLOntology sourceOntology, OWLOntology targetOntology){
		
		// Translate all the mappings into OWL Axioms.
		OWLDataFactory dataFactory = owlontologymanager.getOWLDataFactory();
		OWLOntology mergedOntology = mergeOntology(owlontologymanager);//TODO try to change
		
		Configuration configuration = new Configuration();
		configuration.tableauMonitorType = org.semanticweb.HermiT.Configuration.TableauMonitorType.TIMING;

		for( MatchingPair currentMatchingPair : alignment ) {

			IRI sourceConceptIRI = IRI.create(currentMatchingPair.sourceURI);
			IRI targetConceptIRI = IRI.create(currentMatchingPair.targetURI);

			Set<OWLEntity> sourceEntities = sourceOntology.getEntitiesInSignature(sourceConceptIRI);
			Set<OWLEntity> targetEntities = targetOntology.getEntitiesInSignature(targetConceptIRI);

			// first we need to check if we are working with classes or properties
			if( currentMatchingPair.relation == MappingRelation.EQUIVALENCE ) {

				if( sourceEntities.size() == 1 && targetEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLClass ) {
					// we are working with classes, so we create EquivalentClass axioms
					OWLClass sourceClass = (OWLClass) sourceEntities.toArray()[0];
					OWLClass targetClass = (OWLClass) targetEntities.toArray()[0];

					OWLEquivalentClassesAxiom equivClasses = dataFactory.getOWLEquivalentClassesAxiom(sourceClass, targetClass);

					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivClasses);
					owlontologymanager.applyChanges(axiomList);

				}
				else if( sourceEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLDataProperty ) {
					// we are working with properties, so we create EquivalentProperty axioms
					OWLDataProperty sourceProperty = (OWLDataProperty) sourceEntities.toArray()[0];
					OWLDataProperty targetProperty = (OWLDataProperty) targetEntities.toArray()[0];

					OWLEquivalentDataPropertiesAxiom equivProperties = dataFactory.getOWLEquivalentDataPropertiesAxiom(sourceProperty, targetProperty);

					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivProperties);
					owlontologymanager.applyChanges(axiomList);
				}
				else if( sourceEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLObjectProperty ) {
					// we are working with properties, so we create EquivalentProperty axioms
					OWLObjectProperty sourceProperty = (OWLObjectProperty) sourceEntities.toArray()[0];
					OWLObjectProperty targetProperty = (OWLObjectProperty) targetEntities.toArray()[0];

					OWLEquivalentObjectPropertiesAxiom equivProperties = dataFactory.getOWLEquivalentObjectPropertiesAxiom(sourceProperty, targetProperty);

					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivProperties);
					owlontologymanager.applyChanges(axiomList);
				}
			}
			else {
				log.debug("Write Code!!");
			}

		}
		Reasoner mergedReasoner = new Reasoner(configuration, mergedOntology);
		Node<OWLClass> unsatClasses = mergedReasoner.getUnsatisfiableClasses();
		log.debug(unsatClasses.getSize());
		
		return unsatClasses;
	}

	/** Use HermiT API here to do reasoning here. */
	public void repairAlignment() throws OWLOntologyCreationException, OWLOntologyStorageException{
		
		// Load the Reference Alignment.
//		File alignmentFile = new File("../Ontologies/OAEI/2011/anatomy/reference_2011.rdf");
		File alignmentFile = new File("../Ontologies/OAEI/2011/anatomy/alignments/am_oaei_2011.rdf");
		List<MatchingPair> alignment = AlignmentUtilities.getMatchingPairsOAEI(alignmentFile.getAbsolutePath());

		log.debug("Loaded alignment. The alignment contains " + alignment.size() + " mappings.");

		Node<OWLClass> unsatClasses = loadOntologies(alignment);
		
		//Repair the alignment
		for(OWLClass unsatClass: unsatClasses){
			Iterator<MatchingPair> mpIter = alignment.iterator();
			while(mpIter.hasNext()){
				MatchingPair pair = mpIter.next();
				if(pair.sourceURI.equals(unsatClass.getIRI().toString()) || pair.targetURI.equals(unsatClass.getIRI().toString())){
					mpIter.remove();
				}
			}
			
		}
		
		Node<OWLClass> unsatClassesRepaired = loadOntologies(alignment);
		log.debug(unsatClassesRepaired);
		
		MappingsOutput.writeMappingsOnDisk("/home/cosmin/repairedalignment.rdf", alignment);
		
		/*Set<OWLAxiom> targetAxioms = targetOntology.getAxioms();
        for( OWLAxiom currentAxiom : targetAxioms ) {
        	Set<OWLClass> classes = currentAxiom.getClassesInSignature();
        	OWLAxiom currentNNF = currentAxiom.getNNF();
        	log.debug(currentAxiom);
        }*/

		/*//Run the reasoner on the merged ontology
		sourceReasoner = new Reasoner(configuration, mergeOntology());
		sourceReasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		//Generate inferences
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredClassAssertionAxiomGenerator());
		InferredOntologyGenerator iog = new InferredOntologyGenerator(sourceReasoner, gens);

		//Save inferred ontology in a new OWL file
		OWLOntology inferredOntology = owlontologymanager.createOntology();
		iog.fillOntology(owlontologymanager, inferredOntology);
		owlontologymanager.saveOntology(inferredOntology, new RDFXMLOntologyFormat(), IRI.create("file:///home/cosmin/inferredoutput.owl"));

		sourceReasoner = new Reasoner(configuration, inferredOntology);

		//        inferredOntology.getClassAssertionAxioms(0);
		//        hReasoner.getEquivalentClasses(classExpression);
*/	}

	public OWLOntology mergeOntology(OWLOntologyManager owlontologymanager){
		//Call the OWL merger to merge the 2 ontologies
		OWLOntologyMerger merger = new OWLOntologyMerger(owlontologymanager);
		OWLOntology mergedOntology = null;

		try {
			mergedOntology = merger.createMergedOntology(owlontologymanager, IRI.create("http://output.owl"));

			//Print out the axioms
			log.debug("Printing axioms in merged ontology....\n");
			for(OWLAxiom axiom : mergedOntology.getAxioms()) {
				log.debug(axiom);
			}
			// Save the merged ontology to OWL file
			owlontologymanager.saveOntology(mergedOntology, new RDFXMLOntologyFormat(), IRI.create("file:///home/cosmin/output.owl"));

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
		return mergedOntology;
	}
}
