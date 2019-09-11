package am.extension.userfeedback.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.EventListenerList;

import am.extension.userfeedback.UFLRegistry;
import am.extension.userfeedback.UFLRegistry.CSEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.ExperimentRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackAggregationRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.LoopInizializationRegistry;
import am.extension.userfeedback.UFLRegistry.PropagationEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.UserValidationRegistry;

public class UFLControlGUI_InitialSettingsPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 2611843585071837042L;

	// Start Screen
	public JButton btn_start;
	public JComboBox<ExperimentRegistry> cmbExperiment;
	public JComboBox<InitialMatcherRegistry> cmbMatcher;
	public JComboBox<LoopInizializationRegistry> cmbInizialization;
	public JComboBox<CandidateSelectionRegistry> cmbCandidate;
	public JComboBox<CSEvaluationRegistry> cmbCSEvaluation;
	public JComboBox<FeedbackAggregationRegistry> cmbAgregation;
	public JComboBox<FeedbackPropagationRegistry> cmbPropagation;
	public JComboBox<PropagationEvaluationRegistry> cmbPropagationEvaluation;
	public JComboBox<UserValidationRegistry> cmbUserFeedback;

	EventListenerList listeners;  // list of listeners for this class
	
	public UFLControlGUI_InitialSettingsPanel()
	{
		listeners = new EventListenerList();
		
		ExperimentRegistry[] 			experimentEntries 				= UFLRegistry.ExperimentRegistry.values();
		InitialMatcherRegistry[] 		InitialMatchersEntries 			= UFLRegistry.InitialMatcherRegistry.values();
		LoopInizializationRegistry[]	LoopInitializationEntries		= UFLRegistry.LoopInizializationRegistry.values();
		CandidateSelectionRegistry[] 	CandidateSelectionEntries 		= UFLRegistry.CandidateSelectionRegistry.values();
		CSEvaluationRegistry[] 			CSEvaluationEntries 			= UFLRegistry.CSEvaluationRegistry.values();
		UserValidationRegistry[] 		UserValidationEntries 			= UFLRegistry.UserValidationRegistry.values();
		FeedbackAggregationRegistry[] 	FeedbackAgregationEntries 		= UFLRegistry.FeedbackAggregationRegistry.values();
		FeedbackPropagationRegistry[] 	FeedbackPropagationEntries 		= UFLRegistry.FeedbackPropagationRegistry.values();
		PropagationEvaluationRegistry[] PropagationEvaluationEntries 	= UFLRegistry.PropagationEvaluationRegistry.values();
		
		//populate the combo boxes here
		cmbExperiment = new JComboBox<ExperimentRegistry>(experimentEntries);
		cmbExperiment.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_cmbExperiment.name() );
		cmbExperiment.addActionListener(this);
		
		cmbMatcher = new JComboBox<InitialMatcherRegistry>(InitialMatchersEntries);
		cmbMatcher.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_cmbMatcher.name() );
		cmbMatcher.addActionListener(this);
		
		cmbInizialization = new JComboBox<LoopInizializationRegistry>(LoopInitializationEntries);
		cmbInizialization.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_cmbInizialization.name() );
		cmbInizialization.addActionListener(this);
		
		cmbCandidate = new JComboBox<CandidateSelectionRegistry>(CandidateSelectionEntries);
		cmbCandidate.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_cmbCandidate.name() );
		cmbCandidate.addActionListener(this);
		
		cmbCSEvaluation = new JComboBox<CSEvaluationRegistry>(CSEvaluationEntries);
		cmbCSEvaluation.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_cmbCSEvaluation.name() );
		cmbCSEvaluation.addActionListener(this);

		cmbUserFeedback = new JComboBox<UserValidationRegistry>(UserValidationEntries);
		cmbUserFeedback.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_cmbUserFeedback.name() );
		cmbUserFeedback.addActionListener(this);
		
		cmbAgregation = new JComboBox<FeedbackAggregationRegistry>(FeedbackAgregationEntries);
		cmbAgregation.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_cmbAgregation.name() );
		cmbAgregation.addActionListener(this);
		
		cmbPropagation = new JComboBox<FeedbackPropagationRegistry>(FeedbackPropagationEntries);
		cmbPropagation.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_cmbPropagation.name() );
		cmbPropagation.addActionListener(this);
		
		cmbPropagationEvaluation = new JComboBox<PropagationEvaluationRegistry>(PropagationEvaluationEntries);
		cmbPropagationEvaluation.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_cmbPropagationEvaluation.name() );
		cmbPropagationEvaluation.addActionListener(this);
		
		
		//init buttons here
		btn_start = new JButton("Start");
		btn_start.setActionCommand( UFLControlGUI.ActionCommands.INITSCREEN_btnStart.name() );
		btn_start.addActionListener(this);
		
		JLabel lblExperiment				= new JLabel("Experiment");
		JLabel lblInitialMatcher 			= new JLabel("Automatic Initial Matcher:");
		JLabel lblLoopInizialization 		= new JLabel("Loop Inizialization:");
		JLabel lblCandidateSelection 		= new JLabel("Candidate Selection:");
		JLabel lblCSEvaluation 				= new JLabel("CS Evaluation:");
		JLabel lblUserFeedback 				= new JLabel("User Validation:");
		JLabel lblPropagation 				= new JLabel("Feedback Propagation:");
		JLabel lblPropagationEvaluation 	= new JLabel("Propagation Evaluation:");
		
		//LAYOUT
		JPanel centralContainer  = new JPanel();
		GroupLayout groupLayout = new GroupLayout(centralContainer);
		centralContainer.setLayout( groupLayout );
		centralContainer.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "User Feedback Loop: Experiment Setup"));
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addGroup(groupLayout.createSequentialGroup()
				//ALL LABELS IN THE FIRST COLUMN
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(lblExperiment)
					.addComponent(lblInitialMatcher)
					.addComponent(lblLoopInizialization)
					.addComponent(lblCandidateSelection)
					.addComponent(lblCSEvaluation)
					.addComponent(lblPropagation)
					.addComponent(lblPropagationEvaluation)
					.addComponent(lblUserFeedback)
				)
				//ALL COMPONENTS IN THE SECOND COLUMNS
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(cmbExperiment)
					.addComponent(cmbMatcher)
					.addComponent(cmbInizialization)
					.addComponent(cmbCandidate)
					.addComponent(cmbCSEvaluation)
					.addComponent(cmbPropagation) 
					.addComponent(cmbPropagationEvaluation) 	
					.addComponent(cmbUserFeedback) 		
				)
			)
			//sequentialGroup for buttons
			.addGroup(groupLayout.createParallelGroup()
					.addComponent(btn_start)
			)
		);
			
			// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblExperiment)
					.addComponent(cmbExperiment)
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblInitialMatcher)
					.addComponent(cmbMatcher)
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblLoopInizialization)
					.addComponent(cmbInizialization)
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblCandidateSelection)
					.addComponent(cmbCandidate)
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblCSEvaluation)
					.addComponent(cmbCSEvaluation)
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblUserFeedback)
					.addComponent(cmbUserFeedback) 	
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblPropagation)
					.addComponent(cmbPropagation) 
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblPropagationEvaluation)
					.addComponent(cmbPropagationEvaluation)
				)
				.addGap(20)
				.addGroup(groupLayout.createParallelGroup()
					.addComponent(btn_start)
				)	
			);
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(centralContainer);
		
//		cmbExperiment				.setEnabled(false);
//		cmbMatcher					.setEnabled(false);
//		cmbInizialization			.setEnabled(false);
//		cmbCandidate				.setEnabled(false);
//		cmbCSEvaluation				.setEnabled(false);
//		cmbUserFeedback				.setEnabled(false);
//		cmbPropagation				.setEnabled(false);
//		cmbPropagationEvaluation	.setEnabled(false);
	}
	
	
	/* Event listeners */
	
	@Override
	public void actionPerformed(ActionEvent e) { fireEvent(e); /* pass it on. */ }
	
	public void addActionListener( ActionListener l ) {
		listeners.add(ActionListener.class, l);
	}
	
	/**
	 * This method fires an action event.
	 * @param e Represents the action that was performed.
	 */
	protected void fireEvent( ActionEvent e ) {
		ActionListener[] actionListeners = listeners.getListeners(ActionListener.class);
		
		for( int i = actionListeners.length-1; i >= 0; i-- ) {
			actionListeners[i].actionPerformed(e);
		}
	}

	/* Test entrypoint */
	public static void main(String[] args)
	{
		JDialog newFrame = new JDialog();
		newFrame.setModal(true); // stop execution on setVisible(true) until the dialog is closed.
		newFrame.setLayout(new BorderLayout());
		newFrame.add(new UFLControlGUI_InitialSettingsPanel(), BorderLayout.CENTER);
		newFrame.pack();  newFrame.setLocationRelativeTo(null); newFrame.setVisible(true);
		
		System.exit(0);
	}
	
}
