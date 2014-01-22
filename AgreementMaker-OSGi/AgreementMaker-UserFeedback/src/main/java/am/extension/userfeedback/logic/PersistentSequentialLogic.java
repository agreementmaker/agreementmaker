package am.extension.userfeedback.logic;

import java.awt.event.ActionEvent;

import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.ui.UFLControlGUI.ActionCommands;

public class PersistentSequentialLogic<T extends UFLExperiment> extends PersistentControlLogic<T> {

	@Override
	public void runExperiment(T exp) {
		this.experiment = exp;
		runInitialMatchers();
	}

	/* actionPerformed.  Almost all the real work is done here. */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		System.out.println(e.getActionCommand());  // TODO: Remove this.
		
		if( experiment != null && experiment.experimentHasCompleted() ) { // check stop condition
			System.out.println("Experiment has completed.  Ignoring further actions.");
			return;
		}
		
		if( e.getActionCommand().equals(ActionCommands.EXECUTION_SEMANTICS_DONE.name()) ) {
			runCandidateSelection();
		}
		
		if( e.getActionCommand().equals(ActionCommands.CANDIDATE_SELECTION_DONE.name()) ) {
			runCandidateSelectionEvaluation();
		}

		if( e.getActionCommand().equals(ActionCommands.CS_EVALUATION_DONE.name()) ) {
			runUserValidation();
		}
		
		if( e.getActionCommand().equals(ActionCommands.USER_FEEDBACK_DONE.name()) ) {
			runFeedbackPropagation();
		}
		
		if( e.getActionCommand().equals(ActionCommands.PROPAGATION_DONE.name()) ) {
			runPropagationEvaluation();
		}
		
		if( e.getActionCommand().equals(ActionCommands.PROPAGATION_EVALUATION_DONE.name()) ) {
			experiment.newIteration();
			runCandidateSelection(); // back to top /\
		}
	}
	
}
