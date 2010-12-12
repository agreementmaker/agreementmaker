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
		
		s += "( " + getObject().getStCouple().getLeft().toString() + ", " + getObject().getStCouple().getRight().toString() + " )" +
		 		" - " + getObject().getOldSimilarityValue() + " - " + getObject().getNewSimilarityValue();
		
		return s;
	}
}
