package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

import am.utility.DirectedGraphVertex;
import am.utility.Pair;
/**
 * Pairwise Connectivity Graph Vertex.
 * @author cosmin
 *
 */
public class PCGVertex extends DirectedGraphVertex<Pair<RDFNode, RDFNode>, Property>{

	public PCGVertex(Pair<RDFNode, RDFNode> object) {
		super(object);
	}

	/**
	 * String format: ( leftNode, rightNode )
	 */
	@Override
	public String toString() {
		String s = new String();
		
		s += "( " + getObject().getLeft().toString() + ", " + getObject().getRight().toString() + " )";
		
		return s;
	}
}
