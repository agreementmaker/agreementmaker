package am.app.feedback;

import am.app.feedback.measures.FamilialSimilarity;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentSet;

public class CandidateSelection {

	
	InitialMatcher imStack;  // initial matchers stack
	
	
	/**
	 * Constructor
	 * @param matched - The AlignmentSet of currently matched concepts
	 */
	CandidateSelection( InitialMatcher im ) {
		
		imStack = im;
	}
	
	public AlignmentSet<Alignment> getTopCandidates() {
		
		AlignmentSet<CandidateMapping> candidateSet = new AlignmentSet<CandidateMapping>();
		AlignmentSet<Alignment> topCandidates = new AlignmentSet<Alignment>();
		
		FamilialSimilarity fr = new FamilialSimilarity(this);
		
		
		
		
		return topCandidates;
		
	}

	public AlignmentSet<ExtendedAlignment> getCurrentAlignments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
