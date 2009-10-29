package am.app.feedback.measures;

import java.util.LinkedList;

import am.app.feedback.CandidateMapping;
import am.app.feedback.CandidateSelection;
import am.app.mappingEngine.AlignmentSet;

public class AbstractRMeasure {

	
	LinkedList<CandidateMapping> candidateList;
	
	
	public AbstractRMeasure( ) {
		candidateList = new LinkedList<CandidateMapping>();
	}
	
	
	public void calculateRelevances() {
		
	}
	
	
	
	public LinkedList<CandidateMapping> getRelevanceList() {
		// TODO: Return the correct list.
		return null;
	}
	
	
	public AlignmentSet<CandidateMapping> getCandidateMappings() {
		
		return null;
		
	}
	
}
