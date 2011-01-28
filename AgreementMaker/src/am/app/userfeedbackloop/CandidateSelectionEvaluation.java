package am.app.userfeedbackloop;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;

/**
 * This class is meant to be extended by an implementation of an
 * evaluation method for the candidate selection.
 * 
 * @author Cosmin Stroe @date  January 27th, 2011
 *
 */
public abstract class CandidateSelectionEvaluation {

	Alignment<Mapping> rankedList;
	Alignment<Mapping> reference;
	
	public CandidateSelectionEvaluation( Alignment<Mapping> rL, Alignment<Mapping> ref) {
		rankedList = rL;
		reference = ref;
	}
	
	
	public abstract void evaluate();
	
}
