/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.DirectedGraphVertex;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Michele Caci
 */
public class WGraphVertex extends DirectedGraphVertex<RDFNode, String> {

	public WGraphVertex(RDFNode object) {
		super(object);
	}
	
	public String toString(){
		return " < " + this.getObject().toString() + " > ";
	}

}
