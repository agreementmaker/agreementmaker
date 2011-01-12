/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.DirectedGraphVertex;

import com.hp.hpl.jena.ontology.impl.ObjectPropertyImpl;
import com.hp.hpl.jena.ontology.impl.OntClassImpl;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Michele Caci
 */
public class WGraphVertex extends DirectedGraphVertex<RDFNode, String> {
	
	private String nodeType;
	
	public WGraphVertex(RDFNode object) {
		super(object);
		if(object.canAs(ObjectPropertyImpl.class)){
			setNodeType("PROPERTY");
		}
		else if(object.canAs(OntClassImpl.class)){
			setNodeType("CLASS");
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
