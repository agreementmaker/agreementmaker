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
			log.info("Candidate Selection presented NULL mapping.");
			return;
		}

		// candidate mapping is contained in the reference alignment?
		boolean mappingIsInReference = false;
		if( referenceAlignment.contains(candidateMapping) ) mappingIsInReference = true;
		
		// candidate mapping is contained in the current alignment?
		Alignment<Mapping> finalAlignment = exp.getFinalAlignment();
		boolean mappingIsInAlignment = false;
		if( finalAlignment.contains(candidateMapping) ) mappingIsInAlignment = true;
				
		log.info("Candidate selection mapping: " + 
				(mappingIsInReference ? "(in reference: yes) " : "(in reference: no) ") + 
				(mappingIsInAlignment ? "(in alignment: yes) " : "(in alignment: no) ") + 
				candidateMapping );
		log.info("");
		
		if (mappingIsInReference!=mappingIsInAlignment)
			System.out.println("");
		
		done();
	}
}
