package agreementMaker.application.mappingEngine.fakeMatchers;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.ontology.Node;
import agreementMaker.userInterface.AbstractMatcherParametersDialog;

public class ReferenceAlignmentMatcher extends AbstractMatcher {
	
	public ReferenceAlignmentMatcher(int n, String s) {
		super(n, s);
		needsParam = true;
		maxSourceAlign = ANY_INT;
		maxTargetAlign = ANY_INT;
		threshold = 0.01;
		parametersPanel = new ReferenceAlignmentParametersPanel();
	}
	
	protected void beforeAlignOperations() {
		ReferenceAlignmentParameters param = (ReferenceAlignmentParameters)this.param;
		System.out.println(param.fileName+" "+param.format);
	}
	
}
