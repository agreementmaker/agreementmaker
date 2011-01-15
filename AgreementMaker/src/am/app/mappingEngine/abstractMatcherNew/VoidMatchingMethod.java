package am.app.mappingEngine.abstractMatcherNew;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.ontology.Ontology;
import am.utility.Pair;

/**
 * Provides a default matching method that returns a pair of similarity matrices
 * with 0.0 similarity value (dummy test class)
 */
public class VoidMatchingMethod implements MatchingMethod {
	
	@Override
	public Pair<SimilarityMatrix, SimilarityMatrix> match(Pair<Ontology, Ontology> inputOntologies){
		
		Ontology source = inputOntologies.getLeft();
		Ontology target = inputOntologies.getRight();
		
		SimilarityMatrix classes = new ArraySimilarityMatrix(
				source.getClassesList().size(),
				target.getClassesList().size(),
				alignType.aligningClasses);
		SimilarityMatrix properties = new ArraySimilarityMatrix(
				source.getPropertiesList().size(),
				target.getPropertiesList().size(),
				alignType.aligningProperties);
		
		return new Pair<SimilarityMatrix, SimilarityMatrix>(classes, properties);
	}

}
