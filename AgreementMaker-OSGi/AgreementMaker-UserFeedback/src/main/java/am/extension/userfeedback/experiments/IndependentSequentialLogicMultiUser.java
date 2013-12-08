package am.extension.userfeedback.experiments;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import am.extension.collaborationClient.restful.RESTfulCollaborationServer;
import am.extension.multiUserFeedback.MUExperiment;
import am.extension.userfeedback.ui.UFLControlGUI.ActionCommands;

public class IndependentSequentialLogicMultiUser extends UFLControlLogic<MUExperiment> {
	
	private static Logger LOG = Logger.getLogger(IndependentSequentialLogicMultiUser.class);
	
	@Override
	public void runExperiment(MUExperiment exp) {
		this.experiment = exp;
		runInitialMatchers();
	}

	/* actionPerformed.  Almost all the real work is done here. */
	public void actionPerformed(ActionEvent e) {
		
		System.out.println(e.getActionCommand());  // TODO: Remove this.
		
		if( experiment != null && experiment.experimentHasCompleted() ) { // check stop condition
			runSaveFeedback();
			System.out.println("Experiment has completed.  Ignoring further actions.");
			return;
		}
		
		if( e.getActionCommand() == ActionCommands.EXECUTION_SEMANTICS_DONE.name() ) {
			runInizialization();
		}
		
		if( e.getActionCommand() == ActionCommands.LOOP_INIZIALIZATION_DONE.name() ) {
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
