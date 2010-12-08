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
	private double newSimilarityValue;
	private double oldSimilarityValue;
	
	/**
	 *  The default value for the similarity value is of 1.0
	 *  in order to exploit it when we compute the values in the edges
	 *  (In this way, if we don't have input, nodes have already similarity 1.0)
	 */
	public static final double defaultOldSMValue = 1.0;
	public static final double defaultNewSMValue = 0.0;
	
	public StructMatchVertexData(){
		this.stCouple = null;
		this.newSimilarityValue = defaultOldSMValue;
		this.oldSimilarityValue = defaultNewSMValue;
	}
	
	public StructMatchVertexData(Pair<RDFNode, RDFNode> stCouple) {
		this.stCouple = stCouple;
		this.newSimilarityValue = defaultOldSMValue;
		this.oldSimilarityValue = defaultNewSMValue;
	}
	
	public StructMatchVertexData(Pair<RDFNode, RDFNode> stCouple, double similarityValue) {
		this.stCouple = stCouple;
		this.newSimilarityValue = similarityValue;
		this.oldSimilarityValue = similarityValue;
	}
	
	public StructMatchVertexData(Pair<RDFNode, RDFNode> stCouple, double oldSV, double newSV) {
		this.stCouple = stCouple;
		this.newSimilarityValue = oldSV;
		this.oldSimilarityValue = newSV;
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
	 * @return the old similarity value
	 */
	public double getOldSimilarityValue() {
		return oldSimilarityValue;
	}
	
	/**
	 * @return the new similarity value
	 */
	public double getNewSimilarityValue() {
		return newSimilarityValue;
	}

	/**
	 * @param similarityValue the similarityValue to set
	 */
	public void setOldSimilarityValue(double similarityValue) {
		this.oldSimilarityValue = similarityValue;
	}
	
	/**
	 * @param similarityValue the similarityValue to set
	 */
	public void setNewSimilarityValue(double similarityValue) {
		this.newSimilarityValue = similarityValue;
	}

}
