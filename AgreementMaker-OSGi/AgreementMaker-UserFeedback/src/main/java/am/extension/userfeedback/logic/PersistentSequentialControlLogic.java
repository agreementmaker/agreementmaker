package am.extension.userfeedback.logic;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.ui.UFLControlGUI.ActionCommands;

public class PersistentSequentialControlLogic extends PersistentControlLogic<MUExperiment> {

	private static final Logger LOG = LogManager.getLogger(PersistentSequentialControlLogic.class);
	
	@Override
	public void runExperiment(MUExperiment exp) {
		this.experiment = exp;
		runInitialMatchers();
	}

	/* actionPerformed.  Almost all the real work is done here. */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		LOG.info(e.getActionCommand());
		
		if( experiment != null && experiment.experimentHasCompleted() ) { // check stop condition
			//runSaveFeedback();
			System.out.println("Experiment has completed.  Ignoring further actions.");
				
			runStatistic();
			
			return;
		}
		
		if( e.getActionCommand().equals(ActionCommands.EXECUTION_SEMANTICS_DONE.name()) ) {
			runInizialization();
		}
		
		if( e.getActionCommand().equals(ActionCommands.LOOP_INIZIALIZATION_DONE.name()) ) {
			runCandidateSelection();
		}
		
		if( e.getActionCommand().equals(ActionCommands.CANDIDATE_SELECTION_DONE.name()) ) {
			runCandidateSelectionEvaluation();
		}

		if( e.getActionCommand().equals(ActionCommands.CS_EVALUATION_DONE.name()) ) {
			runUserValidation();
		}
		
		if( e.getActionCommand().equals(ActionCommands.USER_FEEDBACK_DONE.name()) ) {
			if( experiment.userFeedback.getUserFeedback() == Validation.CORRECT || 
				experiment.userFeedback.getUserFeedback() == Validation.INCORRECT ) {
				experiment.feedbackCount++;
			}
			
			runFeedbackAggregation();
		}
		
		if( e.getActionCommand().equals(ActionCommands.FEEDBACK_AGREGATION_DONE.name()) ) {
			runFeedbackPropagation();
		}
		
		if( e.getActionCommand().equals(ActionCommands.PROPAGATION_DONE.name()) ) {
			runPropagationEvaluation();
		}
		
		if( e.getActionCommand().equals(ActionCommands.PROPAGATION_EVALUATION_DONE.name()) ) {
			experiment.newIteration();
			LOG.info("New iteration: " + experiment.getIterationNumber());
			runCandidateSelection(); // back to top /\
		}
	}

//	@Override
//	protected void runInitialMatchers() {
//		// Run the initial matchers in a separate thread.
//		try {
//			if( experiment.initialMatcher == null ) {
//				experiment.initialMatcher = experiment.setup.im.getEntryClass().newInstance();
//				experiment.initialMatcher.addActionListener(this);
//			}
//			startThread(new Runnable(){
//				@Override public void run() {
//					experiment.initialMatcher.run(experiment);
//					final AbstractMatcher finalMatcher = experiment.initialMatcher.getFinalMatcher();
//					DefaultSelectionParameters selParam = new DefaultSelectionParameters();
//					MatchingTask task = new MatchingTask(finalMatcher, finalMatcher.getParam(), new MwbmSelection(), selParam);
//					
//					task.matcherResult = finalMatcher.getResult();
//					
//					selParam.matchingTask = task;
//					selParam.inputResult = task.matcherResult;
//					task.select();
//					
//					Core.getInstance().addMatchingTask(task);
//				}
//			});
//		} catch (InstantiationException | IllegalAccessException e) {
//			e.printStackTrace();
//		}
//	}
}
