/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.DirectedGraphVertex;

import com.hp.hpl.jena.ontology.impl.OntClassImpl;
import com.hp.hpl.jena.ontology.impl.OntPropertyImpl;
import com.hp.hpl.jena.ontology.impl.OntResourceImpl;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

/**
 * @author Michele Caci
 */
public class WGraphVertex extends DirectedGraphVertex<RDFNode, String> {
	
	private String nodeType;
	
	public WGraphVertex(RDFNode object) {
		super(object);
		if(object.canAs(ResourceImpl.class) || object.canAs(OntClassImpl.class)){
			nodeType = new String("CLASS");
		}
		else if(object.canAs(OntPropertyImpl.class)){
			nodeType = new String("PROPERTY");
		}
		
	}

	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	public String toString(){
		return " < " + this.getObject().toString() + " > ";
	}
}
