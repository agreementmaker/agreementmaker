package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.DirectedGraphVertex;
/**
 * Pairwise Connectivity Graph Vertex.
 * @author cosmin
 *
 */
public class PCGVertex extends DirectedGraphVertex<PCGVertexData, PCGEdgeData>{

	public PCGVertex(PCGVertexData object) {
		super(object);
	}

	/**
	 * String format: ( leftNode, rightNode )
	 */
	@Override
	public String toString() {
		String s = new String();
		
		s += "( " + getObject().getStCouple().getLeft().asResource().getLocalName().toString() + ", " + getObject().getStCouple().getRight().asResource().getLocalName().toString() + " )" +
		 		" - " + getObject().getOldSimilarityValue() + " - " + getObject().getNewSimilarityValue();
		
		return s;
	}
}
