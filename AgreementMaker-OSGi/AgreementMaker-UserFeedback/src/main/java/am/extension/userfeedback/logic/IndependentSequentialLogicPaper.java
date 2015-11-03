package am.extension.userfeedback.logic;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.SelectionResult;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.ui.UFLControlGUI.ActionCommands;


public class IndependentSequentialLogicPaper extends NonPersistentUFLControlLogic<MUExperiment> {
	
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
			//runSaveFeedback();
			System.out.println("Experiment has completed.  Ignoring further actions.");
			
			try {				
				experiment.logFile.close();
				//am.Utility.displayMessagePane("<html><p>Your logfile has been saved to:</p><p>"+ experiment.logFileFile.getAbsolutePath() + "</p></html>", "LOG File Location");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}
		
		if( e.getActionCommand() == ActionCommands.INITIAL_MATCHERS_DONE.name() ) {
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
			if( experiment.userFeedback.getUserFeedback() == Validation.CORRECT || 
				experiment.userFeedback.getUserFeedback() == Validation.INCORRECT ) {
				experiment.feedbackCount++;
			}
			
			runFeedbackAggregation();
		}
		
		if( e.getActionCommand() == ActionCommands.FEEDBACK_AGREGATION_DONE.name() ) {
			runFeedbackPropagation();
		}
		
		if( e.getActionCommand() == ActionCommands.PROPAGATION_DONE.name() ) {
			runPropagationEvaluation();
		}
		
		if( e.getActionCommand() == ActionCommands.PROPAGATION_EVALUATION_DONE.name() ) {
			experiment.beginIteration();
			runCandidateSelection();
		}
		
	}
	
	@Override
	protected void runInitialMatchers() {
		// Run the initial matchers in a separate thread.
		try {
			experiment.initialMatcher = experiment.setup.im.getEntryClass().newInstance();
			experiment.initialMatcher.addActionListener(this);
			startThread(new Runnable(){
				@Override public void run() {
					experiment.initialMatcher.run(experiment);
					final AbstractMatcher finalMatcher = experiment.initialMatcher.getFinalMatcher();
					MatchingTask task = new MatchingTask(finalMatcher, finalMatcher.getParam(), new MwbmSelection(), new DefaultSelectionParameters());
					
					SelectionResult selResult = new SelectionResult(task);
					selResult.sourceOntologyID = finalMatcher.getSourceOntology().getID();
					selResult.targetOntologyID = finalMatcher.getTargetOntology().getID();
					selResult.classesAlignment = finalMatcher.getClassAlignmentSet();
					selResult.propertiesAlignment = finalMatcher.getPropertyAlignmentSet();
					
					task.matcherResult = finalMatcher.getResult();
					task.selectionResult = selResult;
					Core.getInstance().addMatchingTask(task);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
