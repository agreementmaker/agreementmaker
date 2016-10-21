package am.api.matcher;


import am.api.ontology.Entity;

/**
 * <p>
 * An interface to store similarities computed by a
 * {@link Matcher matching algorithm}. Entities in the
 * source ontology are along the rows, and entities in the target ontology are
 * along the columns.
 * </p>
 * 
 * <p>
 * For example, in the matrix below, the similarity between source concept
 * <code>sC</code> and target concept <code>tC</code> is <code>1.0</code>.
 * <code>sA<code> has index of 0, <code>sB</code> has index of 1, and so on.
 * Likewise, <code>tA</code> has index 0, <code>tB</code> has index 1, and so
 * on. To get the similarity between <code>sC</code> and <code>tC</code>
 * directly, you can call <code>getSimilarity(2,2)</code> or
 * <code>getSimilarity(sC.getIndex(), tC.getIndex())</code>.
 * </p>
 * 
 * <pre>
 *                  tA  tB  tC   ...
 *                  ___ ___ ___
 *              sA |0.1|0.2|0.0|    
 *              sB |0.0|0.0|0.0| ...
 *              sC |0.0|0.3|1.0|
 *               .      .
 *               .      .
 *               .      .
 * </pre>
 */
public interface SimilarityMatrix<I extends Entity> {
	/**
	 * Retrieve the similarity of two entities.
	 *
	 * @param sourceEntity an entity in the source ontology
	 * @param targetEntity an entity in the target ontology
	 * @return A similarity value from 0 to 1.0
	 */
	double getSimilarity(I sourceEntity, I targetEntity);
}