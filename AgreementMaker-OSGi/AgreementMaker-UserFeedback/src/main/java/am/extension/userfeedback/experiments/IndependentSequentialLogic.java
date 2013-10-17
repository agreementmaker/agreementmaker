package am.extension.userfeedback.experiments;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import am.Utility;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.ui.UFLControlGUI.ActionCommands;

public class IndependentSequentialLogic extends UFLControlLogic implements ActionListener {

	UFLExperiment experimentSetup;
	
	@Override
	public void runExperiment(UFLExperiment exp) {
		this.experimentSetup = exp;
	}

	/* actionPerformed.  Almost all the real work is done here. */
	public void actionPerformed(ActionEvent e) {
		
		System.out.println(e.getActionCommand());  // TODO: Remove this.
		
		if( experimentSetup != null && experimentSetup.experimentHasCompleted() ) return; // check stop condition
		
		try{
	
			if( e.getActionCommand() == ActionCommands.INITSCREEN_btnStart.name() ) {
				// the experiment is starting, or we have just completed an iteration of the loop (assuming the propagation evaluation is done last)

				// Step 1.  experiment is starting.  Initialize the experiment setup.
				//ExperimentRegistry experimentRegistryEntry = (ExperimentRegistry) panel.cmbExperiment.getSelectedItem();
				//experimentSetup = experimentRegistryEntry.getEntryClass().newInstance();
				//experimentSetup.gui = this;
				
				// Step 2.  Run the initial matchers.

				experimentSetup.initialMatcher = experimentSetup.setup.im.getEntryClass().newInstance();

				experimentSetup.initialMatcher.addActionListener(this);

				// separate thread for large work
				Thread initialMatchersThread = new Thread() {
					@Override
					public void run() {
						experimentSetup.initialMatcher.run(experimentSetup);	
					}
				};
				
				initialMatchersThread.start();
				
				return;
			}
			
			if( e.getActionCommand() == ActionCommands.EXECUTION_SEMANTICS_DONE.name() ||
					e.getActionCommand() == ActionCommands.PROPAGATION_EVALUATION_DONE.name() ) {
				// the initial matchers have finished running or we are running another loop of the experiment.
				if( e.getActionCommand() == ActionCommands.PROPAGATION_EVALUATION_DONE.name() ) 
				{
					// this is a new iteration of the user feedback loop experiment.
					experimentSetup.newIteration();
				}
				
				// now run the candidate selection.
				experimentSetup.candidateSelection = experimentSetup.setup.cs.getEntryClass().newInstance();

				
				experimentSetup.candidateSelection.addActionListener(this);
				
				// heavy work in separate thread
				Thread candidateSelection = new Thread() {
					@Override
					public void run() {
						experimentSetup.candidateSelection.rank(experimentSetup);	
					}
				};
				
				candidateSelection.start();
				
				return;
			}
			
			if( e.getActionCommand() == ActionCommands.CANDIDATE_SELECTION_DONE.name() ) {
				// the candidate selection is done
				
				// we must first evalute the candidate selection
				experimentSetup.csEvaluation = experimentSetup.setup.cse.getEntryClass().newInstance(); 
				
				experimentSetup.csEvaluation.addActionListener(this);
				
				// separate thread
				Thread csEvaluationThread = new Thread() {
					@Override
					public void run() {
						experimentSetup.csEvaluation.evaluate(experimentSetup);
					}
				};
				
				csEvaluationThread.start();
				
				return;
			}

			if( e.getActionCommand() == ActionCommands.CS_EVALUATION_DONE.name() ) {
				// the evaluation of the candidate selection is done
				
				// have the user validate the candidate mapping
				experimentSetup.userFeedback = experimentSetup.setup.uv.getEntryClass().newInstance();
				
				experimentSetup.userFeedback.addActionListener(this);
				
				// separate thread
				Thread userFeedbackThread = new Thread() {
					@Override
					public void run() {
						experimentSetup.userFeedback.validate(experimentSetup);
					}
				};
				
				userFeedbackThread.start();
				
				return;
			}
			
			if( e.getActionCommand() == ActionCommands.USER_FEEDBACK_DONE.name() ) {
				// the user has validated our candidate mapping(s)
				
				// propagate!
				experimentSetup.feedbackPropagation = experimentSetup.setup.fp.getEntryClass().newInstance();
				
				experimentSetup.feedbackPropagation.addActionListener(this);
				
				// separate thread
				Thread fbThread = new Thread() {
					@Override
					public void run() {
						experimentSetup.feedbackPropagation.propagate(experimentSetup);
					}
				};
				
				fbThread.start();
				return;
			}
			
			if( e.getActionCommand() == ActionCommands.PROPAGATION_DONE.name() ) {
				// we have propagated the user's feedback
				
				// evaluate the propagation!
				experimentSetup.propagationEvaluation = experimentSetup.setup.pe.getEntryClass().newInstance();
				
				experimentSetup.propagationEvaluation.addActionListener(this);
				
				// separate thread
				Thread propEvalThread = new Thread() {
					@Override
					public void run() {
						experimentSetup.propagationEvaluation.evaluate(experimentSetup);
					}
				};
				
				propEvalThread.start();
				return;
			}
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR, null);
		}
	}
	
}
