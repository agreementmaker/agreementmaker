package am.app.mappingEngine.abstractMatcherNew;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.utility.Pair;

/**
 * Every selection method must require:</br>
 * - A pair of not null similarity matrices for classes and properties</br>
 * - A source cardinality (which can be set also with a default value)</br>
 * - A target cardinality (which can be set also with a default value)</br>
 * - A threshold (which can be set also with a default value)</br>
 * Every matching method must return:</br>
 * - A pair of alignments for classes and properties</br>
 * NOTE: SelectionMethod can be reused not just by an AbstractMatcher in this way</br> 
 */
public interface SelectionMethod {

	public Pair<Alignment<Mapping>, Alignment<Mapping>>
		select(
			Pair<SimilarityMatrix, SimilarityMatrix> inputMatrices,
			int sourceCardinality,
			int targetCardinality,
			double threshold
		);
}
