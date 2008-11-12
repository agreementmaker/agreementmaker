package agreementMaker.application.mappingEngine.fakeMatchers;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.ontology.Node;

public class RandomMatcher extends AbstractMatcher {
	
	public RandomMatcher(int n) {
		super(n);
	}
	
	/**Set all alignment sim to a random value between 0 and 1*/
	private Alignment alignTwoNodes(Node source, Node target) {
		double sim = Math.random();
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}

}
