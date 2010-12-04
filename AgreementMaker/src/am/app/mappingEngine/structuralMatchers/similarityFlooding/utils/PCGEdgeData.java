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
	public PCGEdgeData(Property p) {
		super(p);
	}
	
	/**
	 * @param p
	 * @param propagationCoefficient
	 */
	public PCGEdgeData(Property p, double propagationCoefficient) {
		super(p, propagationCoefficient);
	}
	
	/**
	 * @param stCouple
	 * @param propagationCoefficient
	 */
	public PCGEdgeData toPCGEdgeData(IPGEdgeData ipgData) {
		return new PCGEdgeData(ipgData.getStProperty(), ipgData.getPropagationCoefficient());
	}

	@Override
	public String toString() {
		return stProperty.toString() + ":" + Double.toString(propagationCoefficient);
	}
}
