/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

/**
 * @author Michele Caci
 *
 */
public abstract class StructMatchEdgeData {

	protected String stProperty;
	protected double propagationCoefficient;
	
	/**
	 *  The default value for the propagation coefficient is of 1.0
	 *  in order to exploit it when we compute the values in the edges
	 *  (for efficiency reason we can skip the division of defaultPCValue/1.0)
	 */
	public static final double defaultPCValue = 1.0;
	
	public StructMatchEdgeData(){
		this.stProperty = null;
		this.propagationCoefficient = defaultPCValue;
	}
	
	public StructMatchEdgeData(String stCouple) {
		this.stProperty = stCouple;
		this.propagationCoefficient = defaultPCValue;
	}
	
	public StructMatchEdgeData(String stCouple, double propagationCoefficient) {
		this.stProperty = stCouple;
		this.propagationCoefficient = propagationCoefficient;
	}
	/**
	 * @return the stCouple
	 */
	public String getStProperty() {
		return stProperty;
	}

	/**
	 * @param stCouple the stCouple to set
	 */
	public void setStProperty(String stProperty) {
		this.stProperty = stProperty;
	}

	/**
	 * @return the propagationCoefficient
	 */
	public double getPropagationCoefficient() {
		return propagationCoefficient;
	}

	/**
	 * @param propagationCoefficient the propagationCoefficient to set
	 */
	public void setPropagationCoefficient(double propagationCoefficient) {
		this.propagationCoefficient = propagationCoefficient;
	}

}
