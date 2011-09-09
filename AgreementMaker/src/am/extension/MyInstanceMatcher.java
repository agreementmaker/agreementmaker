package am.extension;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.instance.Instance;

public class MyInstanceMatcher extends AbstractMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8278698313888419789L;

	@Override
	protected MatchingPair alignInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) {
		
		System.out.println("Source instance: " + sourceInstance );
		System.out.println("Target instance list: " + targetCandidates );
		System.out.println("");
		
		progressDisplay.appendToReport(sourceInstance.toString() + "\n");
		
		return super.alignInstanceCandidates(sourceInstance, targetCandidates);
	}
	
}
