package agreementMaker.application.mappingEngine.fakeMatchers;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.ontology.Node;

public class AllZeroMatcher extends AbstractMatcher {
	
	public AllZeroMatcher(int n) {
		super(n);
	}
	
	/**Set all alignment sim to 0*/
	private Alignment alignTwoNodes(Node source, Node target) {
		double sim = 0;
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}
}
