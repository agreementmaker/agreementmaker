package agreementMaker.application.mappingEngine.testMatchers;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.ontology.Node;

public class RandomMatcher extends AbstractMatcher {
	
	/**Set all alignment sim to a random value between 0 and 1*/
	public Alignment alignTwoNodes(Node source, Node target) {
		double sim = Math.random();
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}

}
