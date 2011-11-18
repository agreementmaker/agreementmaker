package am.evaluation.repair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

/*
 * This is a test class in order to become familiar with the HermiT reasoner.
 */
public class HermitReasonerTest {
	
	private static Logger log = Logger.getLogger(HermitReasonerTest.class);
	private OWLOntologyManager owlontologymanager = OWLManager.createOWLOntologyManager();
	
	public static void main(String[] args) throws OWLOntologyCreationException {
		DOMConfigurator.configure("log4j.xml");
		
		try {
			new HermitReasonerTest().mergeAndReason();
			
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Use HermiT API here to do reasoning here. */
	public void mergeAndReason() throws OWLOntologyCreationException, OWLOntologyStorageException{
		Configuration configuration = new Configuration();
        configuration.tableauMonitorType = org.semanticweb.HermiT.Configuration.TableauMonitorType.TIMING;
        Reasoner hReasoner;
        
        org.semanticweb.owlapi.model.OWLOntology ontology = owlontologymanager.loadOntologyFromOntologyDocument(
//        		new File("C:\\Users\\Renu\\Desktop\\human.owl"));
        		new File("H:\\Work\\Eclipse Workspace\\Ontologies\\OAEI\\2011\\anatomy\\human.owl"));
        
        hReasoner = new Reasoner(configuration, ontology);
        log.debug("What are the unsatisfiable classes in Human.owl? " + hReasoner.getUnsatisfiableClasses() + "\n");
        
        ontology = owlontologymanager.loadOntologyFromOntologyDocument(
//        		new File("C:\\Users\\Renu\\Desktop\\mouse.owl"));
        		new File("H:\\Work\\Eclipse Workspace\\Ontologies\\OAEI\\2011\\anatomy\\mouse.owl"));
        
        hReasoner = new Reasoner(configuration, ontology);
        log.debug("What are the unsatisfiable classes in Mouse.owl? " + hReasoner.getUnsatisfiableClasses() + "\n");
        
        
        
        //Run the reasoner on the merged ontology
        hReasoner = new Reasoner(configuration, mergeOntology());
        hReasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
//        hReasoner.getUnsatisfiableClasses();
        
        //Generate inferences
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
        gens.add(new InferredClassAssertionAxiomGenerator());
        InferredOntologyGenerator iog = new InferredOntologyGenerator(hReasoner, gens);
        
        //Save inferred ontology in a new OWL file
        OWLOntology inferredOntology = owlontologymanager.createOntology();
        iog.fillOntology(owlontologymanager, inferredOntology);
        owlontologymanager.saveOntology(inferredOntology, new RDFXMLOntologyFormat(), IRI.create("file:/inferredoutput.owl"));
        
        hReasoner = new Reasoner(configuration, inferredOntology);
        
//        inferredOntology.getClassAssertionAxioms(0);
//        hReasoner.getEquivalentClasses(classExpression);
	}

	public OWLOntology mergeOntology(){
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
        	owlontologymanager.saveOntology(mergedOntology, new RDFXMLOntologyFormat(), IRI.create("file:/output.owl"));

        } catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
		return mergedOntology;
	}
}
