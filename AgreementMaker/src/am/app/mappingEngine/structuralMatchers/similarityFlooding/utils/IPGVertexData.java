/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.Pair;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Michele Caci
 *
 */
public class IPGVertexData extends StructMatchVertexData {

	/**
	 * 
	 */
	public IPGVertexData() {
		super();
	}
	
	/**
	 * @param stCouple
	 */
	public IPGVertexData(Pair<RDFNode, RDFNode> stCouple) {
		super(stCouple);
	}

	/**
	 * @param stCouple
	 * @param propagationCoefficient
	 */
	public IPGVertexData(Pair<RDFNode, RDFNode> stCouple, double propagationCoefficient) {
		super(stCouple, propagationCoefficient);
	}

}
