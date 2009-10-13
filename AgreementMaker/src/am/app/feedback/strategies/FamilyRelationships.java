package am.app.feedback.strategies;

import am.app.feedback.CandidateAlignment;
import am.app.feedback.CandidateSelection;
import am.app.feedback.ExtendedAlignment;
import am.app.feedback.ExtrapolationStrategy;
import am.app.mappingEngine.AlignmentSet;
import am.app.ontology.Node;

public class FamilyRelationships extends ExtrapolationStrategy {

	double STEP1_MULTIPLIER = 1.00d;
	
	public FamilyRelationships(CandidateSelection cs) {
		super(cs);
		// TODO Auto-generated constructor stub
	}

	public AlignmentSet<CandidateAlignment> getCandidates() {
		
		AlignmentSet<CandidateAlignment> candidates = new AlignmentSet<CandidateAlignment>();
		
		candidates.addAll(computeStep1Relevances());
		
		//candidates.sort();
		
		return null;
		
	}

	/**
	 * Computes step1 relevances.
	 * @return
	 */
	private AlignmentSet<CandidateAlignment> computeStep1Relevances() {
		AlignmentSet<CandidateAlignment> step1Candidates = new AlignmentSet<CandidateAlignment>();
		
		AlignmentSet<ExtendedAlignment> currentAlignments = cs.getCurrentAlignments();
		
				
		for( int i = 0; i < currentAlignments.size(); i++ ) {
			ExtendedAlignment p = currentAlignments.getAlignment(i);
			
			Node sourceNode = p.getEntity1();
			Node targetNode = p.getEntity2();
			
			int n = sourceNode.getChildren().size();
			int m = targetNode.getChildren().size();
			
			double relevance = (n + m) * STEP1_MULTIPLIER;
			
			CandidateAlignment q = new CandidateAlignment( p, relevance);
			
			step1Candidates.addAlignment(q);
			
		}
		
		
		
		return null;
	}
	
}
