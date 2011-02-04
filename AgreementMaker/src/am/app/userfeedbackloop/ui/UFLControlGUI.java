package am.app.userfeedbackloop.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import am.Utility;
import am.app.userfeedbackloop.UFLExperiment;
import am.app.userfeedbackloop.UFLRegistry.CSEvaluationRegistry;
import am.app.userfeedbackloop.UFLRegistry.CandidateSelectionRegistry;
import am.app.userfeedbackloop.UFLRegistry.ExperimentRegistry;
import am.app.userfeedbackloop.UFLRegistry.FeedbackPropagationRegistry;
import am.app.userfeedbackloop.UFLRegistry.InitialMatcherRegistry;
import am.app.userfeedbackloop.UFLRegistry.PropagationEvaluationRegistry;
import am.app.userfeedbackloop.UFLRegistry.UserValidationRegistry;
import am.userInterface.UI;

public class UFLControlGUI extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -967696425990716259L;
	
	public final static String UNLIMITED 			= "Unlimited";
	public final static String A_MAPPING_CORRECT 	= "Validate selected candidate mapping";
	public final static String A_ALL_MAPPING_WRONG 	= "Unvalidate all candidate mappings";
	public final static String A_CONCEPT_WRONG 		= "Unvalidate selected candidate concept";
	public final static String A_ALL_CONCEPT_WRONG 	= "Unvalidate all candidate concepts";
	

    // the parts of the experiment
    private UFLExperiment				experimentSetup;
    
    public enum ActionCommands {
    	INITSCREEN_cmbExperiment,
    	INITSCREEN_cmbMatcher, 
    	INITSCREEN_btnStart, 
    	INITSCREEN_cmbCandidate,
    	INITSCREEN_cmbCSEvaluation,
    	INITSCREEN_cmbUserFeedback,
    	INITSCREEN_cmbPropagationEvaluation,  
    	INITSCREEN_cmbPropagation,
    	
    	EXECUTION_SEMANTICS_DONE, 
    	CANDIDATE_SELECTION_DONE, 
    	CS_EVALUATION_DONE, 
    	USER_FEEDBACK_DONE, 
    	PROPAGATION_DONE, 
    	PROPAGATION_EVALUATION_DONE,
    	;
    }
    
    
	private UFLControlGUI_InitialSettingsPanel panel;

	UI ui;
	
	
	public UFLControlGUI(UI u) {
		ui = u;
	}


	
	//****************UI Functions************************
	
	/**
	 * This is the screen that gets displayed when the UFL GUI is first shown to the user.
	 */
	public void displayInitialScreen() {
		
		removeAll();
		panel=new UFLControlGUI_InitialSettingsPanel();
		panel.addActionListener(this);
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(panel);
		
		repaint();
	}
	
	
	/* actionPerformed.  Almost all the real work is done here. */
	public void actionPerformed(ActionEvent e) {
		
		System.out.println(e.getActionCommand());  // TODO: Remove this.
		
		if( experimentSetup != null && experimentSetup.isDone() ) return; // check stop condition
		
		try{
	
			if( e.getActionCommand() == ActionCommands.INITSCREEN_btnStart.name() ) {
				// the experiment is starting, or we have just completed an iteration of the loop (assuming the propagation evaluation is done last)

				// Step 1.  experiment is starting.  Initialize the experiment setup.
				ExperimentRegistry experimentRegistryEntry = (ExperimentRegistry) panel.cmbExperiment.getSelectedItem();
				experimentSetup = experimentRegistryEntry.getEntryClass().newInstance();
				
				// Step 2.  Run the initial matchers.

				InitialMatcherRegistry initialMatcherRegistryEntry = (InitialMatcherRegistry) panel.cmbMatcher.getSelectedItem();
				experimentSetup.initialMatcher = initialMatcherRegistryEntry.getEntryClass().newInstance();

				experimentSetup.initialMatcher.addActionListener(this);

				// separate thread for large work
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						experimentSetup.initialMatcher.run(experimentSetup);	
					}
				});
				
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
				CandidateSelectionRegistry candidateSelectionRegistryEntry = (CandidateSelectionRegistry) panel.cmbCandidate.getSelectedItem();
				experimentSetup.candidateSelection = candidateSelectionRegistryEntry.getEntryClass().newInstance();

				
				experimentSetup.candidateSelection.addActionListener(this);
				
				// heavy work in separate thread
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						experimentSetup.candidateSelection.rank(experimentSetup);	
					}
				});
				
				return;
			}
			
			if( e.getActionCommand() == ActionCommands.CANDIDATE_SELECTION_DONE.name() ) {
				// the candidate selection is done
				
				// we must first evalute the candidate selection
				CSEvaluationRegistry candidateSelectionEvaluationRegistryEntry = (CSEvaluationRegistry) panel.cmbCSEvaluation.getSelectedItem();
				experimentSetup.csEvaluation = candidateSelectionEvaluationRegistryEntry.getEntryClass().newInstance(); 
				
				experimentSetup.csEvaluation.addActionListener(this);
				
				// separate thread
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						experimentSetup.csEvaluation.evaluate(experimentSetup);
					}
				});
				
				return;
			}

			if( e.getActionCommand() == ActionCommands.CS_EVALUATION_DONE.name() ) {
				// the evaluation of the candidate selection is done
				
				// have the user validate the candidate mapping
				UserValidationRegistry userFeedbackRegistryEntry = (UserValidationRegistry) panel.cmbUserFeedback.getSelectedItem();
				experimentSetup.userFeedback = userFeedbackRegistryEntry.getEntryClass().newInstance();
				
				experimentSetup.userFeedback.addActionListener(this);
				
				// separate thread
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						experimentSetup.userFeedback.validate(experimentSetup);
					}
				});
				
				return;
			}
			
			if( e.getActionCommand() == ActionCommands.USER_FEEDBACK_DONE.name() ) {
				// the user has validated our candidate mapping(s)
				
				// propagate!
				FeedbackPropagationRegistry userFeedbackRegistryEntry = (FeedbackPropagationRegistry) panel.cmbPropagation.getSelectedItem();
				experimentSetup.feedbackPropagation = userFeedbackRegistryEntry.getEntryClass().newInstance();
				
				experimentSetup.feedbackPropagation.addActionListener(this);
				
				// separate thread
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						experimentSetup.feedbackPropagation.propagate(experimentSetup);
					}
				});
				
				return;
			}
			
			if( e.getActionCommand() == ActionCommands.PROPAGATION_DONE.name() ) {
				// we have propagated the user's feedback
				
				// evaluate the propagation!
				PropagationEvaluationRegistry propagationEvaluationRegistryEntry = (PropagationEvaluationRegistry) panel.cmbPropagationEvaluation.getSelectedItem();
				experimentSetup.propagationEvaluation = propagationEvaluationRegistryEntry.getEntryClass().newInstance();
				
				experimentSetup.propagationEvaluation.addActionListener(this);
				
				// separate thread
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						experimentSetup.propagationEvaluation.evaluate(experimentSetup);
					}
				});
				
				return;
			}
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR, null);
		}
	}
	
}
