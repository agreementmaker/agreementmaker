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
	public PCGEdgeData(String p) {
		super(p);
	}
	
	/**
	 * @param property
	 * @param propagationCoefficient
	 */
	public PCGEdgeData(String property, double propagationCoefficient) {
		super(property, propagationCoefficient);
	}

	@Override
	public String toString() {
		return stProperty + ":" + Double.toString(propagationCoefficient);
	}
}
