package am.app.userfeedbackloop.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import am.app.userfeedbackloop.UFLRegistry;
import am.app.userfeedbackloop.UFLRegistry.CSEvaluationRegistry;
import am.app.userfeedbackloop.UFLRegistry.CandidateSelectionRegistry;
import am.app.userfeedbackloop.UFLRegistry.FeedbackPropagationRegistry;
import am.app.userfeedbackloop.UFLRegistry.InitialMatcherRegistry;
import am.app.userfeedbackloop.UFLRegistry.PropagationEvaluationRegistry;
import am.app.userfeedbackloop.UFLRegistry.UserValidationRegistry;

public class UFLControl_InitialSettingsPanel extends JPanel {
	
	// Start Screen
	JButton btn_start;
	JComboBox cmbMatcher;
	JComboBox cmbCandidate;
	JComboBox cmbCS;
	JComboBox cmbFeedback;
	JComboBox cmbPropagation;
	JComboBox cmbUser;

	public static void main(String[] args)
	{
		 JDialog newFrame = new JDialog(); 
		 newFrame.setLayout(new BorderLayout());
		 newFrame.add(new UFLControl_InitialSettingsPanel(), BorderLayout.CENTER);
		 newFrame.pack();  newFrame.setLocationRelativeTo(null); newFrame.setVisible(true);
		//UFLControl_InitialSettingsPanel p=new UFLControl_InitialSettingsPanel();
		
	}
	public UFLControl_InitialSettingsPanel()
	{
		
		InitialMatcherRegistry[] InitialMatchersEntries = UFLRegistry.InitialMatcherRegistry.values();
		CandidateSelectionRegistry[] CandidateSelectionEntries = UFLRegistry.CandidateSelectionRegistry.values();
		CSEvaluationRegistry[] CSEvaluationEntries = UFLRegistry.CSEvaluationRegistry.values();
		FeedbackPropagationRegistry[] FeedbackPropagationEntries = UFLRegistry.FeedbackPropagationRegistry.values();
		PropagationEvaluationRegistry[] PropagationEvaluationEntries = UFLRegistry.PropagationEvaluationRegistry.values();
		UserValidationRegistry[] UserValidationEntries = UFLRegistry.UserValidationRegistry.values();
		
		//populate the combo boxes here
		cmbMatcher = new JComboBox(InitialMatchersEntries);
		cmbCandidate = new JComboBox(CandidateSelectionEntries);
		cmbCS = new JComboBox(CSEvaluationEntries);
		cmbFeedback = new JComboBox(FeedbackPropagationEntries);
		cmbPropagation = new JComboBox(PropagationEvaluationEntries);
		cmbUser = new JComboBox(UserValidationEntries);
		
		//init buttons here
		btn_start = new JButton("Start");
		
		//all other component are initialized in the initScreenStartComponents() method
		//because we want to init them in the constructor only once.
		//this way the parameters FeedbackPropagationremains set when the user click cancel
		JLabel lblMatcher = new JLabel("Automatic Initial Matcher:");
		JLabel lblCandidate = new JLabel("Candidate Selection:");
		JLabel lblCS = new JLabel("CS Evaluation:");
		JLabel lblFeedback = new JLabel("Feedback Propagation:");
		JLabel lblPropagation = new JLabel("Propagation Evaluation:");
		JLabel lblUser = new JLabel("User Validation:");
		
		//LAYOUT
		JPanel centralContainer  = new JPanel();
		GroupLayout groupLayout = new GroupLayout(centralContainer);
		centralContainer.setLayout( groupLayout );
		centralContainer.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "User Feedback Loop parameters"));
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addGroup(groupLayout.createSequentialGroup()
				//ALL LABELS IN THE FIRST COLUMN
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(lblMatcher)
					.addComponent(lblCandidate)
					.addComponent(lblCS)
					.addComponent(lblFeedback)
					.addComponent(lblPropagation)
					.addComponent(lblUser)
				)
				//ALL COMPONENTS IN THE SECOND COLUMNS
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(cmbMatcher)
					.addComponent(cmbCandidate)
					.addComponent(cmbCS)
					.addComponent(cmbFeedback) 
					.addComponent(cmbPropagation) 	
					.addComponent(cmbUser) 		
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
					.addComponent(lblMatcher)
					.addComponent(cmbMatcher,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
					)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblCandidate)
					.addComponent(cmbCandidate,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
					)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblCS)
					.addComponent(cmbCS,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
					)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblFeedback)
					.addComponent(cmbFeedback,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 
					)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblPropagation)
					.addComponent(cmbPropagation,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
					)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(lblUser)
					.addComponent(cmbUser,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
				.addGap(20)
				.addGroup(groupLayout.createParallelGroup()
					.addComponent(btn_start)
				)	
			);
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(centralContainer);
		System.out.println("before");
		repaint();
		System.out.println("before");
	}
	public JComboBox getCmbMatcher()
	{
		return cmbMatcher;
	}
	public JComboBox getCmbCandidate()
	{
		return cmbCandidate;
	}
	public JComboBox getCmbCS()
	{
		return cmbCS;
	}
	public JComboBox getCmbFeedback()
	{
		return cmbFeedback;
	}
	public JComboBox getCmbPropagation()
	{
		return cmbPropagation;
	}
	public JComboBox getCmbUser()
	{
		return cmbUser;
	}
}
