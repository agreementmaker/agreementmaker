/**
 * 
 */
package am.matcher.structuralMatchers.similarityFlooding.utils;

import am.matcher.structuralMatchers.similarityFlooding.utils.EOntNodeType.EOntologyNodeType;
import am.utility.DirectedGraphVertex;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.impl.OntClassImpl;
import com.hp.hpl.jena.ontology.impl.OntPropertyImpl;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Michele Caci
 */
public class WGraphVertex extends DirectedGraphVertex<RDFNode, String> {
	
	private int matrixIndex;
	private EOntologyNodeType nodeType;
	
	public WGraphVertex(RDFNode node) {
		super(node);
		setNodeType(node);
		matrixIndex = 0;
	}
	
	public WGraphVertex(RDFNode node, int index) {
		super(node);
		setNodeType(node);
		matrixIndex = index;
	}

	/**
	 * @param matrixIndex the matrixIndex to set
	 */
	public void setMatrixIndex(int matrixIndex) {
		this.matrixIndex = matrixIndex;
	}

	/**
	 * @return the matrixIndex
	 */
	public int getMatrixIndex() {
		return matrixIndex;
	}

	/**
	 * @return the nodeType
	 */
	public EOntologyNodeType getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(EOntologyNodeType nodeType) {
		this.nodeType = nodeType;
	}
	
	public void setNodeType(RDFNode node) {
		if(node.canAs(OntClassImpl.class)){
			nodeType = EOntologyNodeType.CLASS;
		}
		else if(node.canAs(OntPropertyImpl.class)){
			nodeType = EOntologyNodeType.PROPERTY;
		}
		else{
			OntResource res = node.as(OntResource.class);
			if(res.isClass()){
				nodeType = EOntologyNodeType.CLASS;
			}
			else if(res.isProperty()){
				nodeType = EOntologyNodeType.PROPERTY;
			}
			else{
				nodeType = null;
			}
		}
	}
	
	public String toString(){
		return " < " + this.getObject().toString() + " > ";
	}
	
}
