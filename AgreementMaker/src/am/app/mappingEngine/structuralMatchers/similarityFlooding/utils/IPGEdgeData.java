/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * @author Michele Caci
 * TODO: just in case we are going to define the Induced Propagation Graph
 */
public class IPGEdgeData extends StructMatchEdgeData {

	/**
	 * 
	 */
	public IPGEdgeData() {
		super();
	}
	
	/**
	 * @param stProperty
	 */
	public IPGEdgeData(Property stProperty) {
		super(stProperty);
	}

	/**
	 * @param stProperty
	 * @param propagationCoefficient
	 */
	public IPGEdgeData(Property stProperty, double propagationCoefficient) {
		super(stProperty, propagationCoefficient);
	}
	
	/**
	 * @param stCouple
	 * @param propagationCoefficient
	 */
	public IPGEdgeData toIPGEdgeData(PCGEdgeData pcgData) {
		return new IPGEdgeData(pcgData.getStProperty(), pcgData.getPropagationCoefficient());
	} 

}
