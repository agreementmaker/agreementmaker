package am.app.ontology;

import com.hp.hpl.jena.rdf.model.Resource;

public abstract class AbstractNode implements Node {

	/** This is the reference of the Resource in the ontology, this is a Jena class.
	 *  When parsing an XML ontology, there will be a Jena OntModel built in order to provide this resource.	
	 *  The Node object is built taking information from this Jena Resource.
	 *  If we have loaded an OWL ontology this resource will be instance of OntResource.
	 *  If this node is a ClassNode, the resource will be instance of OntClass.
	 *  The Jena Resource is used to access all information in the ontology regarding this concept.
	 */
	protected final Resource resource;
	
	public AbstractNode(Resource resource) {
		this.resource = resource;
	}
	
	@Override public abstract int compareTo(Node arg0);

	@Override public Resource getResource() { return resource; }
	//@Override public void setResource(Resource res) { this.resource = res; }
	
	@Override public String getLocalName() { return resource.getLocalName(); }

}
