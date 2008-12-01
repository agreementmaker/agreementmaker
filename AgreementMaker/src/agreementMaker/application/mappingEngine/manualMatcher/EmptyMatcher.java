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


	/**These 3 methods are invoked any time the user select a matcher in the matcherscombobox. Usually developers don't have to override these methods unless their default values are different from these.*/
	public double getDefaultThreshold() {
		return 0.01;
	}
	
	/**These 3 methods are invoked any time the user select a matcher in the matcherscombobox. Usually developers don't have to override these methods unless their default values are different from these.*/
	public int getDefaultMaxSourceRelations() {
		return ANY_INT;
	}

	/**These 3 methods are invoked any time the user select a matcher in the matcherscombobox. Usually developers don't have to override these methods unless their default values are different from these.*/
	public int getDefaultMaxTargetRelations() {
		return ANY_INT;
	}
}
