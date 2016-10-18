package am.api.matching;

import am.api.ontology.Class;
import am.api.ontology.Instance;
import am.api.ontology.Property;

import java.util.Optional;

/**
 * <p>
 * A matching result contains three similarity matrices, one for each kind of
 * entity in the ontology.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> Not all matching algorithms can produce every kind of similarity
 * matrix.
 * </p>
 */
public interface MatcherResult {
	/**
	 * @return the computed classes similarity matrix
	 */
	Optional<SimilarityMatrix<Class>> getClasses();

	/**
	 * @return the computed properties similarity matrix
	 */
	Optional<SimilarityMatrix<Property>> getProperties();

	/**
	 * @return the computed instances similarity matrix
	 */
	Optional<SimilarityMatrix<Instance>> getInstances();
}