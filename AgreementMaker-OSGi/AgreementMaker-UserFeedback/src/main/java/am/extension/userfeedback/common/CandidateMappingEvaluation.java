package am.extension.userfeedback.common;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.evaluation.CandidateSelectionEvaluation;
import am.extension.userfeedback.experiments.UFLExperiment;

/**
 * This class logs the candidate mapping and whether it's contained in the
 * reference alignment and in the current alignment.
 * 
 * @author cosmin
 * 
 */
public class CandidateMappingEvaluation extends CandidateSelectionEvaluation {

	@Override
	public void evaluate(UFLExperiment exp ) {
		UFLExperiment log = exp; // TODO: Fix this.
		
		Alignment<Mapping> referenceAlignment = exp.getReferenceAlignment();
		
		Mapping candidateMapping = exp.candidateSelection.getSelectedMapping();
		if(candidateMapping == null) {
			log.info("\tCandidate Selection presented NULL mapping.");
			return;
		}

		Alignment<Mapping> finalAlignment = exp.getFinalAlignment();
				
		log.info("\tCandidate selection mapping: " + 
				(referenceAlignment.contains(candidateMapping) ? "(in reference: yes) " : "(in reference: no) ") + 
				(finalAlignment.contains(candidateMapping) ? "(in alignment: yes) " : "(in alignment: no) ") + 
				candidateMapping );
		done();
	}
}
