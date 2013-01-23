package am.app.mappingEngine.abstractMatcherNew;

import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Ontology;
import am.utility.Pair;

/**
 * Every matching method must require:</br>
 * - A pair of source and target ontologies (to be already loaded and not null)</br>
 * Every matching method must return:</br>
 * - A pair of not null similarity matrices for classes and properties</br>
 * NOTE: MatchingMethods can be reused not just by an AbstractMatcher in this way</br> 
 */
public interface MatchingMethod {

	public Pair<SimilarityMatrix, SimilarityMatrix> match(Pair<Ontology, Ontology> inputOntologies);
	
}
