package am.application.mappingEngine.testMatchers;

import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.Alignment;
import am.application.mappingEngine.AbstractMatcher.alignType;
import am.application.ontology.Node;

public class AllZeroMatcher extends AbstractMatcher {
	
	
	/**Set all alignment sim to 1*/
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		double sim = 0;
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}
}
