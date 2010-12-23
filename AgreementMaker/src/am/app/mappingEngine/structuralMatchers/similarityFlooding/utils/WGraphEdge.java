/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.DirectedGraphEdge;
import am.utility.DirectedGraphVertex;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Michele Caci
 */
public class WGraphEdge extends DirectedGraphEdge<RDFNode, RDFNode> {

	public WGraphEdge(DirectedGraphVertex<RDFNode, RDFNode> orig,
			DirectedGraphVertex<RDFNode, RDFNode> dest, RDFNode o) {
		super(orig, dest, o);
	}

	
}
