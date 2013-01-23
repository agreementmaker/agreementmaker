package am.extension.feedback;

import java.util.ArrayList;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.AMNode;
import am.app.ontology.Node;

/**
 * This class extends the Alignment class in order to add more information to be used
 * in candidate selection
 * @author cosmin
 *
 */

public class CandidateConcept extends AMNode /*implements Comparable<CandidateConcept>*/{

	private static final long serialVersionUID = 5565715572303899116L;
	
	protected double relevance = 0.00;
	protected boolean patternRepeats = false;
	
	protected int whichOntology;
	protected alignType whichType;
	protected Node originalNode;
	
	//Contains the Top M candidate mappings for this candidate concept
	//it is remains null, until the getCandidateAlignments is called;
	protected ArrayList<Mapping> candidateMappings;


	public CandidateConcept(Node n, double r, int oType, alignType t ) {
		super(n);
		relevance = r;
		whichOntology = oType;
		whichType = t;
		originalNode = n;
	}

	public double getRelevance() {
		return relevance;
	}
	
	public void setRelevance(double r){
		relevance = r;
	}
	
	public Node getNode(){
		return originalNode;
	}
	
	// to allow candidate concepts to be sorted (required by the Comparable)
	public int compareTo(CandidateConcept cc) {
		if( cc.getClass() != CandidateConcept.class ) {
			return 0;
		}
		if( cc.getRelevance() == relevance ) {
			return 0;  // equal
		}else if( relevance > cc.getRelevance() ) {
			return 1; // greater
		}else {
			return -1;
		}
	}
	
	public boolean isFromOntology( int oType ) {
		if( whichOntology == oType ) {
			return true;
		}
		return false;
	}

	public boolean isType( alignType t ) {
		if( whichType == t ) {
			return true;
		}
		return false;
	}
	
	
	public boolean equals( Node n ) {
		
		return super.equals( n );
	}
	
	public void setPatternRepeats( boolean p ) {
		patternRepeats = p;
	}
	
	public boolean getPatternRepeats(){
		return patternRepeats;
	}
	
	public String toString() {
		return super.toString() + "\t" + Double.toString(relevance);
	}
	
	public ArrayList<Mapping> getCandidateMappings() {
		return candidateMappings;
	}

	public void setCandidateMappings(ArrayList<Mapping> candidateMappings) {
		this.candidateMappings = candidateMappings;
	}

}
