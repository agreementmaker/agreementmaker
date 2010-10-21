package am.app.mappingEngine.testMatchers;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;

public class RandomMatcher extends AbstractMatcher {
	
	/**Set all alignment sim to a random value between 0 and 1*/
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		double sim = Math.random();
		String rel = Mapping.EQUIVALENCE;
		return new Mapping(source, target, sim, rel);
	}

}
