package am.app.mappingEngine.abstractMatcherNew;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Ontology;
import am.utility.Pair;

/**
 * Provides a default selection method that returns an empty alignment (dummy test class)
 */
public class VoidSelectionMethod implements SelectionMethod {

	@Override
	public Pair<Alignment<Mapping>, Alignment<Mapping>> select(
			Pair<SimilarityMatrix, SimilarityMatrix> inputMatrices,
			int sourceCardinality, int targetCardinality, double threshold) {
		
		Alignment<Mapping> classes = new Alignment<Mapping>(Ontology.ID_NONE, Ontology.ID_NONE);
		Alignment<Mapping> properties = new Alignment<Mapping>(Ontology.ID_NONE, Ontology.ID_NONE);
		
		return new Pair<Alignment<Mapping>, Alignment<Mapping>>(classes, properties);
	}

}
