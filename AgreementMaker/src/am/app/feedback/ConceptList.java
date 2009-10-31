package am.app.feedback;

import java.util.ArrayList;
import java.util.Iterator;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;

public class ConceptList extends ArrayList<CandidateConcept> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -365634132691269028L;

	
	private double weight;
	
	public double getAverage() {
		
		if( size() == 0 ) return 0.00d;  // avoid divide by zero error
		
		double total = 0.00;
		Iterator<CandidateConcept> i = iterator();
		while( i.hasNext() ) {
			total += i.next().getRelevance();
		}
		return total / size();
	}
	
	public double getMaximum() {
		double max = 0.00;
		Iterator<CandidateConcept> i = iterator();
		while( i.hasNext() ) {
			double current = i.next().getRelevance();
			if( max < current ) max = current;
		}
		
		return max;
	}
	
	public double getSpread() {
		double max = getMaximum();
		if( max == 0.00d ) return 0.00d; // avoid divide by 0
		return getAverage() / max;		
	}	
	
	public void setWeight( double totalSpread ) {
		if( totalSpread == 0.00d ) {
			weight = 0.00d;  // avoid divide by 0
		}
		weight = getSpread() / totalSpread;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public double getRelevance( Node n, CandidateConcept.ontology o, alignType t ) {
		
		Iterator<CandidateConcept> i = iterator();
		while( i.hasNext() ) {
			CandidateConcept cc = i.next();
			if( cc.isOntology(o) && cc.isType(t) && cc.equals(n) ) {
				// this is a match
				return cc.getRelevance();
			}
		}
		
		return 0.00d;
	}
}
