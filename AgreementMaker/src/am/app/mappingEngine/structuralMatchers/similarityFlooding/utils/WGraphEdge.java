/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.DirectedGraphEdge;
import am.utility.DirectedGraphVertex;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Michele Caci
 */
public class WGraphEdge extends DirectedGraphEdge<String, RDFNode> {

	public WGraphEdge(DirectedGraphVertex<RDFNode, String> orig,
			DirectedGraphVertex<RDFNode, String> dest, String o) {
		super(orig, dest, o);
	}
	
	public String toString(){
		return " <" + this.getOrigin().getObject().toString() + " --- "
					+ this.getObject().toString() + " --- "
					+ this.getDestination().toString() + "> ";
	}
}
