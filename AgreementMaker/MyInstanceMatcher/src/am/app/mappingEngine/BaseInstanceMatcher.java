package am.app.mappingEngine;

import java.util.List;

import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.instance.Instance;


public class BaseInstanceMatcher extends AbstractMatcher{

	private static final long serialVersionUID = 4301685403439511365L;
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
	}
	
	@Override
	protected MatchingPair alignInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		
		System.out.println("SOURCE");
		System.out.println(sourceInstance.getSingleValuedProperty("label") + "\t" + sourceInstance.getUri());
		
		System.out.println("CANDIDATES (" + targetCandidates.size() + ")");
		
		String solutionURI = null;
		
		if(referenceAlignment != null)
			solutionURI = ReferenceAlignmentUtilities.candidatesContainSolution(referenceAlignment, 
					sourceInstance.getUri(), targetCandidates);
		
		boolean foundSolution = false;
		
		Instance candidate;
		for (int i = 0; i < targetCandidates.size(); i++) {
			candidate = targetCandidates.get(i);
			
			if(solutionURI != null && solutionURI.equals(candidate.getUri())){
				System.out.print("X ");
				foundSolution = true;
			}
				
			System.out.println(candidate.getSingleValuedProperty("label") + "\t" + candidate.getUri());
		}
				
		if(!foundSolution) System.out.println("NON SOLVABLE:\t" + sourceInstance.getSingleValuedProperty("label") + 
				"\t" + sourceInstance.getUri());
		
		return null;
	}
	
	@Override
	protected void afterAlignOperations() {
		super.afterAlignOperations();
	}
	
	@Override
	public String processLabelBeforeCandidatesGeneration(String label) {
		return super.processLabelBeforeCandidatesGeneration(label);
	}
}
