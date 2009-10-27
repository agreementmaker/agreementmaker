package am.app.feedback;

import am.app.mappingEngine.AlignmentSet;

public class ExtrapolationStrategy {

	
	protected CandidateSelection cs;
	
	
	public ExtrapolationStrategy( CandidateSelection c ) {
		cs = c;
	}
	
	
	
	public AlignmentSet<CandidateMapping> getCandidates() {
		
		return null;
		
	}
	
}
