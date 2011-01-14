/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.DirectedGraphEdge;

/**
 * Pairwise Connectivity Graph Edge.
 * @author cosmin
 *
 */
public class PCGEdge extends DirectedGraphEdge<PCGEdgeData, PCGVertexData>{
	
	private boolean inserted;
	
	public PCGEdge(PCGVertex orig, PCGVertex dest, PCGEdgeData o) {
		super(orig, dest, o);
	}

	/**
	 * @param visited the visited to set
	 */
	public void setInserted(boolean inserted) {
		this.inserted = inserted;
	}

	/**
	 * @return the visited
	 */
	public boolean isInserted() {
		return inserted;
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
