/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.Pair;

/**
 * @author Michele Caci
 *
 */
public class PCGVertexData extends StructMatchVertexData {

	/**
	 * 
	 */
	public PCGVertexData() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param stCouple
	 * @param propagationCoefficient
	 */
	public PCGVertexData(Pair<WGraphVertex, WGraphVertex> stCouple) {
		super(stCouple);
	}

	/**
	 * @param stCouple
	 * @param similarityValue
	 */
	public PCGVertexData(Pair<WGraphVertex, WGraphVertex> stCouple, double similarityValue) {
		super(stCouple, similarityValue);
	}

	public PCGVertexData(Pair<WGraphVertex, WGraphVertex> stCouple, double oldSV, double newSV) {
		super(stCouple, oldSV, newSV);
	}
	
}
