package am.app.feedback;

import java.util.ArrayList;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentSet;

public class CandidateSelection {

	
	InitialMatchers imStack;  // initial matchers stack
	
	
	/**
	 * Constructor
	 * @param matched - The AlignmentSet of currently matched concepts
	 */
	CandidateSelection( InitialMatchers im ) {
		
		imStack = im;
	}
	
	public ArrayList<Alignment> getTopCandidates() {
		
		
		
		return null;
		
	}

	public AlignmentSet<ExtendedAlignment> getCurrentAlignments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
