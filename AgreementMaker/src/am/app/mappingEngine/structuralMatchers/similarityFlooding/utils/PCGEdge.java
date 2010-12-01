/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

import am.utility.DirectedGraphEdge;
import am.utility.DirectedGraphVertex;
import am.utility.Pair;

/**
 * Pairwise Connectivity Graph Edge.
 * @author cosmin
 *
 */
public class PCGEdge extends DirectedGraphEdge<Property, Pair<RDFNode, RDFNode>>{

	public PCGEdge(PCGVertex orig, PCGVertex dest, Property o) {
		super(orig, dest, o);
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		String s = new String();
		
		s += getOrigin().toString() + " ---- " + getObject().toString() + " ---> " + getDestination().toString();
		return s;
	}
	
}
