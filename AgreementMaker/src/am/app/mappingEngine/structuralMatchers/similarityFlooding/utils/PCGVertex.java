package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.DirectedGraphVertex;
import am.utility.Pair;
/**
 * Pairwise Connectivity Graph Vertex.
 * @author cosmin
 *
 */
public class PCGVertex extends DirectedGraphVertex<PCGVertexData, PCGEdgeData>{

	private boolean inserted;
	private boolean visited;
	
	public PCGVertex(PCGVertexData object) {
		super(object);
		setVisited(false);
		setInserted(false);
	}
	
	/**
	 * get the vertex from two WGraphEdges
	 */
	public PCGVertex(WGraphVertex s, WGraphVertex t) {
		super(new PCGVertexData(new Pair<WGraphVertex, WGraphVertex>(s, t)));
		setVisited(false);
		setInserted(false);
	}

	/**
	 * @param visited the visited to set
	 */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/**
	 * @return the visited
	 */
	public boolean isVisited() {
		return visited;
	}
	
	/**
	 * @param inserted the inserted to set
	 */
	public void setInserted(boolean inserted) {
		this.inserted = inserted;
	}

	/**
	 * @return the inserted
	 */
	public boolean isInserted() {
		return inserted;
	}

	/**
	 * String format: ( leftNode, rightNode )
	 */
	@Override
	public String toString() {
		String s = new String();
		
		s += "( " + getObject().getStCouple().getLeft().getObject().asResource().getLocalName().toString() + ", " + getObject().getStCouple().getRight().getObject().asResource().getLocalName().toString() + " )" +
		 		" - " + getObject().getOldSimilarityValue() + " - " + getObject().getNewSimilarityValue();
		
		return s;
	}
}
