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
public abstract class StructMatchVertexData {
	
	private Pair<RDFNode, RDFNode> stCouple;
	private double similarityValue;
	
	/**
	 *  The default value for the similarity value is of 1.0
	 *  in order to exploit it when we compute the values in the edges
	 *  (In this way, if we don't have input, nodes have already similarity 1.0)
	 */
	public static final double defaultSMValue = 1.0;
	
	public StructMatchVertexData(){
		this.stCouple = null;
		this.similarityValue = defaultSMValue;
	}
	
	public StructMatchVertexData(Pair<RDFNode, RDFNode> stCouple) {
		this.stCouple = stCouple;
		this.similarityValue = defaultSMValue;
	}
	
	public StructMatchVertexData(Pair<RDFNode, RDFNode> stCouple, double similarityValue) {
		this.stCouple = stCouple;
		this.similarityValue = similarityValue;
	}
	/**
	 * @return the stCouple
	 */
	public Pair<RDFNode, RDFNode> getStCouple() {
		return stCouple;
	}

	/**
	 * @param stCouple the stCouple to set
	 */
	public void setStCouple(Pair<RDFNode, RDFNode> stCouple) {
		this.stCouple = stCouple;
	}

	/**
	 * @return the propagationCoefficient
	 */
	public double getSimilarityValue() {
		return similarityValue;
	}

	/**
	 * @param propagationCoefficient the propagationCoefficient to set
	 */
	public void setSimilarityValue(double similarityValue) {
		this.similarityValue = similarityValue;
	}

}
