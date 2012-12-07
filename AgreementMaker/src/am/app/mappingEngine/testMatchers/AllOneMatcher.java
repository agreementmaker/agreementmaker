package am.app.mappingEngine.testMatchers;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;

public class AllOneMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = 2854018267136437040L;

	/**Set all alignment sim to 1*/
	@Override
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes, SimilarityMatrix matrix) {
		double sim = 1;
		MappingRelation rel = MappingRelation.EQUIVALENCE;
		return new Mapping(source, target, sim, rel);
	}
}
