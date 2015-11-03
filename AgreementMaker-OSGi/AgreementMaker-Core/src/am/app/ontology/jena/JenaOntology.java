package am.app.ontology.jena;

import java.util.Collection;
import java.util.List;

import am.api.ontology.AMOntology;
import am.api.ontology.OntoInstance;
import am.api.ontology.OntoProperty;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * An implementation of the AgreementMaker Ontology interface, using the JENA
 * library as the backend.
 * 
 * @author Cosmin Stroe (cstroe@gmail.com)
 * @param <P>
 * @param <I>
 */
public abstract class JenaOntology<I extends OntoInstance<?>> implements AMOntology<OntModel,JenaClass,JenaProperty,I> {

	OntModel model;
	
	public JenaOntology(String ontURI) {
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
		model.read( ontURI, null, "RDF/XML" );
	}
	
	@Override
	public OntModel getInner() {
		return model;
	}

	@Override
	public List<JenaClass> getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JenaProperty> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<I> getInstances() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
