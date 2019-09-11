package am.extension.userfeedback.evaluation;

import am.extension.userfeedback.common.CandidateMappingEvaluation;
import am.extension.userfeedback.experiments.UFLExperiment;

/**
 * A candidate selection evaluation that allows to run multiple candidate
 * selection evaluations as one.
 * 
 * @author cosmin
 * 
 */
public class MultiplexCandidateSelectionEvaluation extends CandidateSelectionEvaluation {

	private CandidateSelectionEvaluation[] evaluations;
	
	public MultiplexCandidateSelectionEvaluation() {
		super();
		
		// hardcoded for now
		evaluations = new CandidateSelectionEvaluation[2];
		evaluations[0] = new CandidateMappingEvaluation();
		evaluations[1] = new RankingAccuracy();
	}
	
	public CandidateSelectionEvaluation getEvaluation(Class cls) {
		for(CandidateSelectionEvaluation cse : evaluations) {
			if( cse.getClass().equals(cls) ) return cse;
		}
		return null;
	}
	
	
	
	@Override
	public void evaluate(UFLExperiment exp) {
		for( CandidateSelectionEvaluation eval : evaluations ) {
			eval.evaluate(exp);
		}
		done();
	}
}
