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
public class PCGEdge extends DirectedGraphEdge<PCGEdgeData, PCGVertexData>{
	
	public PCGEdge(PCGVertex orig, PCGVertex dest, PCGEdgeData o) {
		super(orig, dest, o);
		// TODO Auto-generated constructor stub
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
