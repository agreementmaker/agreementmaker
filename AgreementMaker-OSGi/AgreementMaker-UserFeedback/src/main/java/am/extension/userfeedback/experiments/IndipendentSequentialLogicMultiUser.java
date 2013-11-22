package am.extension.userfeedback.experiments;

import java.awt.event.ActionEvent;

import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.ui.UFLControlGUI.ActionCommands;

public class IndipendentSequentialLogicMultiUser extends UFLControlLogic {
	
	@Override
	public void runExperiment(UFLExperiment exp) {
		this.experiment = exp;
		runInitialMatchers();
	}

	/* actionPerformed.  Almost all the real work is done here. */
	public void actionPerformed(ActionEvent e) {
		
		System.out.println(e.getActionCommand());  // TODO: Remove this.
		
		if( experiment != null && experiment.experimentHasCompleted() ) { // check stop condition
			runSaveFeedback();
			System.out.println("Experiment has completed.  Ignoring further actions.");
		}
		
		if( e.getActionCommand() == ActionCommands.EXECUTION_SEMANTICS_DONE.name() ) {
			runCandidateSelection();
		}
		
		if( e.getActionCommand() == ActionCommands.CANDIDATE_SELECTION_DONE.name() ) {
			runCandidateSelectionEvaluation();
		}

		if( e.getActionCommand() == ActionCommands.CS_EVALUATION_DONE.name() ) {
			runUserValidation();
		}
		
		if( e.getActionCommand() == ActionCommands.USER_FEEDBACK_DONE.name() ) {
			runFeedbackPropagation();
		}
		
		if( e.getActionCommand() == ActionCommands.PROPAGATION_DONE.name() ) {
			runPropagationEvaluation();
		}
		
		if( e.getActionCommand() == ActionCommands.PROPAGATION_EVALUATION_DONE.name() ) {
			experiment.newIteration();
			runCandidateSelection(); // back to top /\
		}
	}
	
}
