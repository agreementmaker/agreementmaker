package am.app.mappingEngine.testMatchers;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;

public class AllOneMatcher extends AbstractMatcher {
	
	
	/**Set all alignment sim to 1*/
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		double sim = 1;
		String rel = Mapping.EQUIVALENCE;
		return new Mapping(source, target, sim, rel);
	}
}
