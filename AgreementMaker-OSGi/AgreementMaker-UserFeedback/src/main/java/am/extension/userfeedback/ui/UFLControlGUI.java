package am.extension.userfeedback.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import am.Utility;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UFLExperimentSetup;
import am.extension.userfeedback.UFLRegistry.CSEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.ExperimentRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.PropagationEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.UserValidationRegistry;
import am.extension.userfeedback.experiments.UFLControlLogic;
import am.ui.UI;
import am.ui.api.impl.AMTabSupportPanel;

public class UFLControlGUI extends AMTabSupportPanel implements ActionListener {

	private static final long serialVersionUID = -967696425990716259L;
	
	public final static String UNLIMITED 			= "Unlimited";
	public final static String A_MAPPING_CORRECT 	= "Validate selected candidate mapping";
	public final static String A_ALL_MAPPING_WRONG 	= "Unvalidate all candidate mappings";
	public final static String A_CONCEPT_WRONG 		= "Unvalidate selected candidate concept";
	public final static String A_ALL_CONCEPT_WRONG 	= "Unvalidate all candidate concepts";
	    
    public enum ActionCommands {
    	INITSCREEN_cmbExperiment,
    	INITSCREEN_cmbMatcher, 
    	INITSCREEN_cmbCandidate,
    	INITSCREEN_cmbCSEvaluation,
    	INITSCREEN_cmbUserFeedback,
    	INITSCREEN_cmbPropagationEvaluation,  
    	INITSCREEN_cmbPropagation,
    	INITSCREEN_btnStart,
    	
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
		super("User Feedback Loop");
		ui = u;
	}

	//****************UI Functions************************
	
	/**
	 * This is the screen that gets displayed when the UFL GUI is first shown to the user.
	 */
	public void displayInitialScreen() {
		removeAll();
		panel = new UFLControlGUI_InitialSettingsPanel();
		panel.addActionListener(this);
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(panel);
		
		repaint();
	}
	
	public void displayPanel( JPanel panel ) {
		removeAll();		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(panel);
		repaint();
	}
	
	
	/* actionPerformed.  Almost all the real work is done here. */
	public void actionPerformed(ActionEvent e) {
		
		try{
	
			if( e.getActionCommand() == ActionCommands.INITSCREEN_btnStart.name() ) {
				
				ExperimentRegistry experimentRegistryEntry = (ExperimentRegistry) panel.cmbExperiment.getSelectedItem();
				final UFLExperiment newExperiment = experimentRegistryEntry.getEntryClass().newInstance();
				newExperiment.gui = this;
				
				newExperiment.setup = new UFLExperimentSetup();
				newExperiment.setup.im = (InitialMatcherRegistry) panel.cmbMatcher.getSelectedItem();
				newExperiment.setup.cs = (CandidateSelectionRegistry) panel.cmbCandidate.getSelectedItem();
				newExperiment.setup.cse = (CSEvaluationRegistry) panel.cmbCSEvaluation.getSelectedItem();
				newExperiment.setup.uv = (UserValidationRegistry) panel.cmbUserFeedback.getSelectedItem();
				newExperiment.setup.fp = (FeedbackPropagationRegistry) panel.cmbPropagation.getSelectedItem();
				newExperiment.setup.pe = (PropagationEvaluationRegistry) panel.cmbPropagationEvaluation.getSelectedItem();
				
				// the experiment is starting, or we have just completed an iteration of the loop (assuming the propagation evaluation is done last)

				// Step 1.  experiment is starting.  Initialize the experiment setup.
				
				final UFLControlLogic logic = newExperiment.getControlLogic();
				
				Thread thread = new Thread(new Runnable(){

					@Override
					public void run() {
						logic.runExperiment(newExperiment);
					}
					
				});
				
				thread.start();
				
				return;
			}
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR + "\n\n" + ex.getMessage(), Utility.UNEXPECTED_ERROR_TITLE);
		}
	}
}
