package am.application.mappingEngine.testMatchers;

import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.Alignment;
import am.application.ontology.Node;

public class RandomMatcher extends AbstractMatcher {
	
	/**Set all alignment sim to a random value between 0 and 1*/
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		double sim = Math.random();
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}

}
