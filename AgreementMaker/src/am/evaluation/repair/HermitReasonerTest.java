package am.evaluation.repair;

import org.apache.log4j.Logger;

import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/*
 * This is a test class in order to become familar with the HermiT reasoner.
 */
public class HermitReasonerTest {
	
	
	public static void main(String[] args) {
		
		Logger log = Logger.getLogger("am.evaluation.repair");
		
		// Load a test ontology here (Human Anatomy of OAEI2011).
		
		Ontology ont = OntoTreeBuilder.loadOWLOntology("../Ontologies/OAEI/2011/anatomy/human.owl");
		
		if( ont != null ) {
			log.info("Ontology was loaded successfully: " + ont.getTitle() );
			log.info(ont.getClassesList().size() + " classes, " + ont.getPropertiesList().size() + " properties.");
		}
		
		OntModel m = ont.getModel();
		
		//StmtIterator mStatements = m.listStatements();
		
		
		/** Use HermiT API here to do reasoning here. */
		
		
		
	}

}
