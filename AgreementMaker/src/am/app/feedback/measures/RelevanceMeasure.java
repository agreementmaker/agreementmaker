package am.app.feedback.measures;

import java.util.ArrayList;
import java.util.LinkedList;

import am.app.feedback.CandidateConcept;
import am.app.feedback.CandidateSelection;
import am.app.mappingEngine.AlignmentSet;

public class RelevanceMeasure {

	
	ArrayList<CandidateConcept> candidateList;
	
	protected double threshold;
	
	public RelevanceMeasure() {
		candidateList = new ArrayList<CandidateConcept>();
		threshold = 0.7d;
	}
	
	public RelevanceMeasure( double th ) {
		candidateList = new ArrayList<CandidateConcept>();
		threshold = th;
	}
	
	
	
	public void calculateRelevances() {
		
	}
	
	

	public ArrayList<CandidateConcept> getRelevances() {
		return candidateList;
	}
	
}
