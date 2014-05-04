package am.extension.userfeedback.experiments;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.SelectionResult;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.ui.UFLControlGUI.ActionCommands;
import am.ui.MatcherProgressDialog;
import am.ui.UICore;

public class IndependentSequentialLogicMultiUser extends UFLControlLogic<MLFExperiment> {
	
	private static Logger LOG = Logger.getLogger(IndependentSequentialLogicMultiUser.class);
	
	@Override
	public void runExperiment(MLFExperiment exp) {
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
				am.Utility.displayMessagePane("<html><p>Your logfile has been saved to:</p><p>"+ experiment.logFileFile.getAbsolutePath() + "</p></html>", "LOG File Location");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// close the UFL tab
			//UICore.getUI().removeTab(UICore.getUI().getCurrentTab());
			Core.getInstance().shutdown();
			
			return;
		}
		
		if( e.getActionCommand() == ActionCommands.EXECUTION_SEMANTICS_DONE.name() ) {
			runInizialization();
		}
		
		if( e.getActionCommand() == ActionCommands.LOOP_INIZIALIZATION_DONE.name() ) {
			runCandidateSelection();
		}
		
		if( e.getActionCommand() == ActionCommands.CANDIDATE_SELECTION_DONE.name() ) {
			runUserValidation();
		}

		if( e.getActionCommand() == ActionCommands.USER_FEEDBACK_DONE.name() ) {
			if( experiment.userFeedback.getUserFeedback() == Validation.CORRECT || 
				experiment.userFeedback.getUserFeedback() == Validation.INCORRECT ) {
				experiment.feedbackCount++;
			}
			
			runFeedbackPropagation();
		}
		
		if( e.getActionCommand() == ActionCommands.PROPAGATION_DONE.name() ) {
			experiment.newIteration();
			runCandidateSelection(); // back to top /\
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
