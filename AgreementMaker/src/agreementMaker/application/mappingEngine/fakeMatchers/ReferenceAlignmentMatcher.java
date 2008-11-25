package agreementMaker.application.mappingEngine.fakeMatchers;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.ontology.Node;

public class ReferenceAlignmentMatcher extends AbstractMatcher {
	
	public ReferenceAlignmentMatcher(int n, String s) {
		super(n, s);
	}
	
	/**Set all alignment sim to 0*/
	public Alignment alignTwoNodes(Node source, Node target) {
		double sim = 0;
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}
}
