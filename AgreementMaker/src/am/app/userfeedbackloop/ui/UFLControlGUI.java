package am.app.userfeedbackloop.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import am.Utility;
import am.app.feedback.FeedbackLoop;
import am.app.mappingEngine.AbstractMatcher;
import am.app.userfeedbackloop.UFLExperiment;
import am.app.userfeedbackloop.UFLRegistry.CSEvaluationRegistry;
import am.app.userfeedbackloop.UFLRegistry.CandidateSelectionRegistry;
import am.app.userfeedbackloop.UFLRegistry.ExperimentRegistry;
import am.app.userfeedbackloop.UFLRegistry.FeedbackPropagationRegistry;
import am.app.userfeedbackloop.UFLRegistry.InitialMatcherRegistry;
import am.app.userfeedbackloop.UFLRegistry.PropagationEvaluationRegistry;
import am.app.userfeedbackloop.UFLRegistry.UserValidationRegistry;
import am.userInterface.MatchingProgressDisplay;
import am.userInterface.UI;

public class UFLControlGUI extends JPanel implements MatchingProgressDisplay, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -967696425990716259L;
	private FeedbackLoop ufl = null; // pointer to the user feedback loop
	
	public final static String UNLIMITED 			= "Unlimited";
	public final static String A_MAPPING_CORRECT 	= "Validate selected candidate mapping";
	public final static String A_ALL_MAPPING_WRONG 	= "Unvalidate all candidate mappings";
	public final static String A_CONCEPT_WRONG 		= "Unvalidate selected candidate concept";
	public final static String A_ALL_CONCEPT_WRONG 	= "Unvalidate all candidate concepts";
	
	JComboBox cmbActions;
	
	// Automatic Progress screen.
	JProgressBar progressBar;
    private JTextArea matcherReport;
    private JScrollPane scrollingArea;
    private JButton okButton;
    private JButton cancelButton;
    private JButton stopButton;

	
    // the parts of the experiment
    private UFLExperiment				experimentSetup;
    
    public enum ActionCommands {
    	INITSCREEN_cmbExperiment,
    	INITSCREEN_cmbMatcher, 
    	INITSCREEN_btnStart, 
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
		matcherReport = new JTextArea(8, 35);
	}

	/*********** Matcher Progress Display Methods ***********************/
	public void appendToReport(String report) {
		if(!ufl.isCancelled()){
			if(matcherReport!= null){
				matcherReport.append("\n"+report);
				revalidate();
			}
		}
	}

	@Override
	public void matchingStarted(AbstractMatcher m) {	}
	
	// gets called when a matcher finishes
	@Override
	public void matchingComplete() {
		//if( ufl.isStage( FeedbackLoop.executionStage.runningInitialMatchers ) ) {
		//	appendToReport( "Initial Matchers finished...");
		//}
		progressBar.setIndeterminate(false);
		progressBar.setValue(100);
		matcherReport.append("\n"+ ufl.getReport() );
		cancelButton.setEnabled(false);
		stopButton.setEnabled(false);
		okButton.setEnabled(true);
		revalidate();
	}


	/**
	 * Function that is called when the progress of the matchers
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) { }


	
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
	
	

	/*private void displayProgressScreen() {
		
		removeAll();
	    
	    this.setLayout(new BorderLayout());
	    JPanel textPanel = new JPanel(new BorderLayout());

	    progressBar = new JProgressBar(0, 100);
	    progressBar.setSize(10, 4);
	    progressBar.setValue(0);
	    progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
	
	    this.add(progressBar, BorderLayout.PAGE_START);
	    this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	    
	    JPanel buttonPanel = new JPanel(new FlowLayout());
	    okButton = new JButton("Ok");
	    okButton.setEnabled(false);
	    okButton.addActionListener(this);
	    okButton.setActionCommand("btn_ok");
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("screen2_cancel");
		cancelButton.addActionListener(this);
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("btn_stop");
		stopButton.addActionListener(this);
	    buttonPanel.add(okButton);
	    buttonPanel.add(stopButton);
	    buttonPanel.add(cancelButton);
	    
	    scrollingArea = new JScrollPane(matcherReport);
	    textPanel.add(scrollingArea);
	    textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
	    
	    this.add(textPanel);
	    this.add(buttonPanel, BorderLayout.PAGE_END);
	    
	    
		//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		repaint();
	}*/
	
	
	/*public void displayMappings( ArrayList<CandidateConcept> topConceptsAndAlignments) {
		
		removeAll();
		selectedMapping = null;
		selectedConcept = null;
		candidateMappings = topConceptsAndAlignments;
		
		cmbActions = new JComboBox(new String[]{A_MAPPING_CORRECT,A_ALL_MAPPING_WRONG, A_CONCEPT_WRONG, A_ALL_CONCEPT_WRONG});
		
		JButton btn_continue = new JButton("Continue");
		btn_continue.setActionCommand("btn_continue");
		btn_continue.addActionListener(this);
		
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("btn_stop");
		stopButton.addActionListener(this);
		
		JButton btn_display_m = new JButton("Display selected candidate mapping");
		btn_display_m.setActionCommand("btn_display_m");
		btn_display_m.addActionListener(this);
		
		JButton btn_display_c = new JButton("Display selected candidate concept's mappings");
		btn_display_c.setActionCommand("btn_display_c");
		btn_display_c.addActionListener(this);
		

		
		//Division of mappings into groups and rows, each group contains multiple rows
		//the first two arraylists were only needed for the GroupLayout,
		//instead using the JTable we only need the list of CandidadatesTableRows
		radios = new ButtonGroup();
		ArrayList<CandidatesTableRow> rows = new ArrayList<CandidatesTableRow>();
		for(int i=0; i< topConceptsAndAlignments.size(); i++){
			CandidateConcept c = topConceptsAndAlignments.get(i);
			ArrayList<Mapping> candidateMappings = c.getCandidateMappings();
			if(candidateMappings!= null){
				for(int j = 0; j < candidateMappings.size(); j++){
					Mapping m = candidateMappings.get(j);
					rows.add(createTableRow(c,m, i, j));
				}
			}
			
			
		}

		//Table of candidates
        CandidatesTableModel mt = new CandidatesTableModel(rows);
        table = new  CandidatesTable(mt);
        table.initColumnSizes();
        //the height of a row is 16 on MAC at least.
        table.setPreferredScrollableViewportSize(new Dimension(table.calculateRealWidth(), Math.min( 16*25 , 16*rows.size() )));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); 
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);   
		
		JPanel topPanel = new JPanel();
		FlowLayout topPanelLayout = new FlowLayout();
		topPanelLayout.setAlignment(FlowLayout.CENTER);
		topPanel.setLayout(topPanelLayout);
		topPanel.add(new JLabel("Action: "));
		topPanel.add(cmbActions);
		topPanel.add(btn_continue);
		topPanel.add(stopButton);
		
		JPanel bottomPanel = new JPanel();
		FlowLayout bottomPanelLayout = new FlowLayout();
		bottomPanelLayout.setAlignment(FlowLayout.CENTER);
		bottomPanel.setLayout(bottomPanelLayout);
		bottomPanel.add(new JLabel("Visualization: "));
		bottomPanel.add(btn_display_m);
		bottomPanel.add(btn_display_c);
		
		JPanel centralPanel = new JPanel();
		//centralPanel.setLayout(new GridLayout());
		centralPanel.setOpaque(true); //content panes must be opaque
//        centralPanel.add(scrollPane);
        centralPanel.add(bottomPanel);
        

        
		BorderLayout thisLayout = new BorderLayout();
		this.setLayout(thisLayout);
		this.add(topPanel, BorderLayout.PAGE_START);
		this.add(centralPanel, BorderLayout.CENTER);
		//REPAINT DOESN'T WORK WELL HERE I DON'T KNOW WHY
		//repaint();
		revalidate();
		
		//THIS layout has been changed
		//the grouplayout contains all the item,but without the horizBox, the group is placed on the left of the screen.
		//this is needed to put the Group in the center.
			//centralContainer.setLayout(groupLayout);
		//Box horizontalBox = Box.createHorizontalBox();
		//horizontalBox.add(Box.createHorizontalGlue());
		//horizontalBox.add(centralContainer);
		//horizontalBox.add(Box.createHorizontalGlue());

		//this.setLayout(new GridLayout(1,1));
		//this.setLayout(new FlowLayout());
		//this.add(horizontalBox);
		
	}*/


	@Override
	public void scrollToEndOfReport() {
		SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	if( scrollingArea != null && matcherReport != null ) {
	        		// a complete hack to make the JScrollPane move to the bottom of the JTextArea
	        		Document d = matcherReport.getDocument();
	        		matcherReport.setCaretPosition(d.getLength());
	        	}
	        }
		});
		
	}

	@Override public void clearReport() {	matcherReport.setText(""); }
	@Override public void ignoreComplete(boolean ignore) { /* TODO figure out if we need this	*/ }
	@Override public void setProgressLabel(String label) { progressBar.setString(label); }
	
	
	
	/* actionPerformed.  Almost all the real work is done here. */
	public void actionPerformed(ActionEvent e) {
		
		System.out.println(e.getActionCommand());  // TODO: Remove this.
		
		if( experimentSetup != null && experimentSetup.isDone() ) return; // check stop condition
		
		try{
	
			if( e.getActionCommand() == ActionCommands.INITSCREEN_btnStart.name() ||
				e.getActionCommand() == ActionCommands.PROPAGATION_EVALUATION_DONE.name() ) {
				// the experiment is starting, or we have just completed an iteration of the loop (assuming the propagation evaluation is done last)
				
				// Step 1.  Check if we have had a previous iteration.
				
				if( e.getActionCommand() == ActionCommands.INITSCREEN_btnStart.name() ) 
				{
					// experiment is starting.  Initialize the experiment setup.
					ExperimentRegistry experimentRegistryEntry = (ExperimentRegistry) panel.cmbExperiment.getSelectedItem();
					experimentSetup = experimentRegistryEntry.getEntryClass().newInstance();
				} 
				else if( e.getActionCommand() == ActionCommands.PROPAGATION_EVALUATION_DONE.name() ) 
				{
					// this is a new iteration of the user feedback loop experiment.
					experimentSetup.newIteration();
				}
				
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
			
			if( e.getActionCommand() == ActionCommands.EXECUTION_SEMANTICS_DONE.name() ) {
				// the initial matchers have finished running.
				
				// now run the candidate selection.
				CandidateSelectionRegistry candidateSelectionRegistryEntry = (CandidateSelectionRegistry) panel.cmbCandidate.getSelectedItem();
				experimentSetup.candidateSelection = candidateSelectionRegistryEntry.getEntryClass().newInstance();
				
				experimentSetup.candidateSelection.addActionListener(this);
				
				// heavy work in separate thread
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						experimentSetup.candidateSelection.rank(experimentSetup.initialMatcher);	
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
						experimentSetup.csEvaluation.evaluate();
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
						experimentSetup.userFeedback.validate(experimentSetup.candidateSelection);
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
						experimentSetup.feedbackPropagation.propagate(experimentSetup.userFeedback);
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
						experimentSetup.propagationEvaluation.evaluate(experimentSetup.feedbackPropagation);
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
