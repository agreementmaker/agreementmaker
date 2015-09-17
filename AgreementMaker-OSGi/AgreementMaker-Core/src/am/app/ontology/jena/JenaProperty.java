package am.app.ontology.jena;

import am.api.ontology.OntoProperty;

import com.hp.hpl.jena.ontology.OntProperty;

public class JenaProperty implements OntoProperty<OntProperty> {

	private OntProperty property;
	
	public JenaProperty(OntProperty property) {
		this.property = property;
	}
	
	@Override
	public OntProperty getInner() {
		return property;
	}
	
	@Override
	public String getURI() {
		return property.getURI();
	}

}
