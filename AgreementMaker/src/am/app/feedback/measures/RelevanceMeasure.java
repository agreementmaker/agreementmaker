package am.app.feedback.measures;

import am.app.feedback.ConceptList;
import am.app.feedback.CandidateSelection.MeasuresRegistry;

public class RelevanceMeasure {

	MeasuresRegistry name;
	ConceptList candidateList;
	
	protected double threshold;
	
	public void setName( MeasuresRegistry m ) {
		name = m;
	}
	
	public RelevanceMeasure() {
		candidateList = new ConceptList();
		threshold = 0.7d;
	}
	
	public RelevanceMeasure( double th ) {
		candidateList = new ConceptList();
		threshold = th;
	}
	
	
	
	public void calculateRelevances() {
		
	}
	
	

	public ConceptList getRelevances() {
		return candidateList;
	}
	
}
