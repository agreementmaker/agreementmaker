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
		
		LOG.trace(e.getActionCommand());
		
		if (e.getActionCommand().equals(ActionCommands.INITIAL_MATCHERS_DONE.name())) {
			runInizialization();
		} else if (e.getActionCommand().equals(ActionCommands.LOOP_INIZIALIZATION_DONE.name())) {
			if (experiment.canBeginIteration()) {
				experiment.beginIteration();
				runCandidateSelection();
			}
		} else if (e.getActionCommand().equals(ActionCommands.CANDIDATE_SELECTION_DONE.name())) {
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
			experiment.endIteration();
			if (experiment.canBeginIteration()) {
				experiment.beginIteration();
				runCandidateSelection(); // back to top /\
			} else {
				//runSaveFeedback();
				System.out.println("Experiment has completed.  Ignoring further actions.");
				runStatistic();
			}
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