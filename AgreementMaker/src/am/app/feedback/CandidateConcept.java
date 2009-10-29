package am.app.feedback;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;

/**
 * This class extends the Alignment class in order to add more information to be used
 * in candidate selection
 * @author cosmin
 *
 */

public class CandidateConcept extends Node {

	double relevance = 0.00;
	
	public enum ontology {
		source,
		target
	}
	
	protected ontology whichOntology;
	protected alignType whichType;
	
	public CandidateConcept(Node n, double r, ontology o, alignType t ) {
		super(n.getIndex(), n.getResource(), n.getType());
		relevance = r;
		whichOntology = o;
		whichType = t;
	}
	
}
