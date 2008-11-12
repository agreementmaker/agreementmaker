package agreementMaker.application.mappingEngine.fakeMatchers;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.ontology.Node;

public class AllOneMatcher extends AbstractMatcher {
	
	public AllOneMatcher(int n) {
		super(n);
	}
	
	/**Set all alignment sim to 1*/
	private Alignment alignTwoNodes(Node source, Node target) {
		double sim = 1;
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}
}
