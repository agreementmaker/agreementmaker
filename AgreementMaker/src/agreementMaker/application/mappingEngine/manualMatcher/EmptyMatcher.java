package agreementMaker.application.mappingEngine.manualMatcher;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.ontology.Node;

public class EmptyMatcher extends AbstractMatcher {
	
	/**Set all alignment sim to 0*/
	public Alignment alignTwoNodes(Node source, Node target) {
		double sim = 0;
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}
}
