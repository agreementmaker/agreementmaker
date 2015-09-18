package am.app.ontology.jena;

import am.api.ontology.OntoClass;

import com.hp.hpl.jena.ontology.OntClass;

public abstract class JenaClass implements OntoClass<OntClass> {

	private OntClass jenaObject;
	
	public JenaClass(OntClass cls) {
		jenaObject = cls;
	}

	@Override
	public OntClass getInner() {
		return jenaObject;
	}
	
	@Override
	public String getURI() {
		return jenaObject.getURI();
	}
}
