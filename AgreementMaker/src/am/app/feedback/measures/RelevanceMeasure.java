package am.app.feedback.measures;

import java.util.Collections;
import java.util.Iterator;

import am.app.feedback.CandidateConcept;
import am.app.feedback.ConceptList;
import am.app.feedback.FeedbackLoop;
import am.app.feedback.CandidateSelection.MeasuresRegistry;

public class RelevanceMeasure {

	protected MeasuresRegistry name;
	protected ConceptList candidateList;
	protected FeedbackLoop fbl;
	Class myClass = null;
	
	protected double threshold;
	
	public void setName( MeasuresRegistry m ) {
		name = m;
	}
	
	public RelevanceMeasure() {
		candidateList = new ConceptList();
		threshold = 0.5d;
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
	
	
	
	public void setFeedbackLoop(FeedbackLoop FBL){
		fbl = FBL;
	}

	public void printCandidates() {
		System.out.println("");
		if( myClass != null ) {
			System.out.println( myClass.toString() + ": Candidate list:");
		} else {
			System.out.println("-----: Candidate List:");
		}
		
		Collections.sort(candidateList); // SORTS IN ASCENDING ORDER!
		
		Iterator<CandidateConcept> ccitr = candidateList.iterator();
		while( ccitr.hasNext() ) {
			System.out.println( "     * " + ccitr.next().toString() );
		}
		
	}
	
}
