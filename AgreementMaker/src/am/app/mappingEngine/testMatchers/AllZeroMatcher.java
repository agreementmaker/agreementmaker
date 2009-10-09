package am.app.mappingEngine.testMatchers;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.ontology.Node;

public class AllZeroMatcher extends AbstractMatcher {
	
	
	/**Set all alignment sim to 1*/
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		double sim = 0;
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}
}
