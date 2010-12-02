/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * @author Michele Caci
 *
 */
public class PCGEdgeData extends StructMatchEdgeData {

	/**
	 * 
	 */
	public PCGEdgeData() {
		super();
	}

	/**
	 * @param stCouple
	 */
	public PCGEdgeData(Property stCouple) {
		super(stCouple);
	}
	
	/**
	 * @param stCouple
	 * @param propagationCoefficient
	 */
	public PCGEdgeData(Property stCouple, double propagationCoefficient) {
		super(stCouple, propagationCoefficient);
	}
	
	/**
	 * @param stCouple
	 * @param propagationCoefficient
	 */
	public PCGEdgeData toPCGEdgeData(IPGEdgeData ipgData) {
		return new PCGEdgeData(ipgData.getStProperty(), ipgData.getPropagationCoefficient());
	}

}
