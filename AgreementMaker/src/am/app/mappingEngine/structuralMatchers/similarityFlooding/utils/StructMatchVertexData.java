/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.utility.Pair;

/**
 * @author Michele Caci
 *
 */
public abstract class StructMatchVertexData {
	
	private Pair<WGraphVertex, WGraphVertex> stCouple;
	private double newSimilarityValue;
	private double oldSimilarityValue;
	
	/**
	 *  The default value for the similarity value is of 1.0
	 *  in order to exploit it when we compute the values in the edges
	 *  (In this way, if we don't have input, nodes have already similarity 1.0)
	 */
	public static final double defaultOldSMValue = 1.0;
	public static final double defaultNewSMValue = 1.0;
	
	public StructMatchVertexData(){
		this.stCouple = null;
		this.newSimilarityValue = defaultOldSMValue;
		this.oldSimilarityValue = defaultNewSMValue;
	}
	
	public StructMatchVertexData(Pair<WGraphVertex, WGraphVertex> stCouple) {
		this.stCouple = stCouple;
		this.newSimilarityValue = defaultOldSMValue;
		this.oldSimilarityValue = defaultNewSMValue;
	}
	
	public StructMatchVertexData(Pair<WGraphVertex, WGraphVertex> stCouple, double similarityValue) {
		this.stCouple = stCouple;
		this.newSimilarityValue = similarityValue;
		this.oldSimilarityValue = similarityValue;
	}
	
	public StructMatchVertexData(Pair<WGraphVertex, WGraphVertex> stCouple, double oldSV, double newSV) {
		this.stCouple = stCouple;
		this.newSimilarityValue = oldSV;
		this.oldSimilarityValue = newSV;
	}
	/**
	 * @return the stCouple
	 */
	public Pair<WGraphVertex, WGraphVertex> getStCouple() {
		return stCouple;
	}

	/**
	 * @param stCouple the stCouple to set
	 */
	public void setStCouple(Pair<WGraphVertex, WGraphVertex> stCouple) {
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
