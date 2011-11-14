package am.evaluation.repair;

import java.io.File;

import org.apache.log4j.Logger;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/*
 * This is a test class in order to become familiar with the HermiT reasoner.
 */
public class HermitReasonerTest {
	
	
	public static void main(String[] args) throws OWLOntologyCreationException {
		
		Logger log = Logger.getLogger("am.evaluation.repair");
		
		// Load a test ontology here (Human Anatomy of OAEI2011).
		
		/*Ontology ont = OntoTreeBuilder.loadOWLOntology("D:/Dropbox/Ontologies/From LogMap/Ontologies Used/fma.owl");
		
		if( ont != null ) {
			log.info("Ontology was loaded successfully: " + ont.getTitle() );
			log.info(ont.getClassesList().size() + " classes, " + ont.getPropertiesList().size() + " properties.");
		}
		
		OntModel m = ont.getModel();*/
		
		//StmtIterator mStatements = m.listStatements();
		
		
		/** Use HermiT API here to do reasoning here. */
		OWLOntologyManager owlontologymanager = OWLManager.createOWLOntologyManager();

		Configuration configuration = new Configuration();
        configuration.tableauMonitorType = org.semanticweb.HermiT.Configuration.TableauMonitorType.TIMING;
        
        org.semanticweb.owlapi.model.OWLOntology ontology = owlontologymanager.loadOntologyFromOntologyDocument(
        		new File("H:\\Work\\CS586\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Anatomy Track\\mouse_anatomy_2010.owl"));
        Reasoner hReasoner = new Reasoner(configuration, ontology);
        
        System.out.println("What are the unsatisfiable classes? " + hReasoner.getUnsatisfiableClasses());
        
		
	}

}
