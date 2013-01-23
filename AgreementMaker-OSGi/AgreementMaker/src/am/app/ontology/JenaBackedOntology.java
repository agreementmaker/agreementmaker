package am.app.ontology;

import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * An implementation of the AgreementMaker Ontology interface, using the JENA
 * library as the backend.
 * 
 * @author Cosmin Stroe (cstroe@gmail.com)
 */
public class JenaBackedOntology implements AMOntology {

	OntModel model;
	
	public JenaBackedOntology(String ontURI) {
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
		model.read( ontURI, null, "RDF/XML" );
	}
	
	
	@Override
	public List<Node> getClassesList() {
		ExtendedIterator<OntClass> classesIter = model.listClasses();
		List<Node> classesList = new LinkedList<Node>();
		
		while(classesIter.hasNext()) {
			//classesList.add()
		}
		
		return classesList;
	}

	
	
}
