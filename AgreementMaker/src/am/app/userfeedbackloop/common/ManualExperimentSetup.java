package am.app.userfeedbackloop.common;

import java.util.List;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.ontology.Ontology;
import am.app.userfeedbackloop.UFLExperiment;
import am.app.userfeedbackloop.UserFeedback.Validation;

/**
 * This is a manual experiment setup.
 * 
 * The source and target ontologies are loaded into AgreementMaker,
 * and the reference alignment is imported.
 * 
 * @author Cosmin Stroe - Feb 1, 2011.
 *
 */
public class ManualExperimentSetup extends UFLExperiment {
	
	@Override
	public Ontology getSourceOntology() {
		return Core.getInstance().getSourceOntology();
	}

	@Override
	public Ontology getTargetOntology() {
		return Core.getInstance().getTargetOntology();
	}

	@Override
	public Alignment<Mapping> getReferenceAlignment() {
		List<AbstractMatcher> matchers = Core.getInstance().getMatcherInstances();
		for( AbstractMatcher m : matchers ) {
			if( m instanceof ReferenceAlignmentMatcher ) {
				// return the alignment of the first reference alignment matcher
				return m.getAlignment();
			}
		}
		return null;
	}

	@Override
	public boolean experimentHasCompleted() {
		if( userFeedback != null && userFeedback.getUserFeedback() == Validation.END_EXPERIMENT ) return true;  // we're done when the user says so
		return false;
	}

	@Override
	public void newIteration() {
		super.newIteration();
		// TODO: Save all the objects that we used in the previous iteration.
	}

	@Override
	public Alignment<Mapping> getFinalAlignment() {
		return initialMatcher.getAlignment();
	}

}
