package am.app.mappingEngine.testMatchers;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;

public class RandomMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = -8919055491729085463L;

	/**Set all alignment sim to a random value between 0 and 1*/
	@Override
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes, SimilarityMatrix matrix) {
		double sim = Math.random();
		return new Mapping(source, target, sim, MappingRelation.EQUIVALENCE);
	}

}
