package am.extension.userfeedback.evaluation;

import am.extension.userfeedback.common.PrecisionRecallPlot;
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
		evaluations[0] = new PrecisionRecallPlot();
		evaluations[1] = new RankingAccuracy();
		
		evaluations[0].setIgnoreDone(true);
		evaluations[1].setIgnoreDone(true);
	}
	
	@Override
	public void evaluate(UFLExperiment exp) {
		for( CandidateSelectionEvaluation eval : evaluations ) {
			eval.evaluate(exp);
		}
		done();
	}
	
}
