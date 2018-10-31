package am.ui.matchingtask;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.MatcherFeature;
import am.app.mappingEngine.MatchingTask;
import am.ui.UICore;
import am.ui.matchingtask.MatchingTaskCreatorDialog.MatchingTaskCreatorDialogMessages;
import am.utility.messagesending.MessageDispatch;
import am.utility.messagesending.SimpleMessage;

public class MatchingAlgorithmParametersPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -1141452251567415606L;

	/* UI Components */
	private JLabel matcherLabel = new JLabel("Matcher:");
	private JLabel lblPresets = new JLabel("Presets:");
	private MatcherComboBox matcherCombo = new MatcherComboBox();
	private JComboBox cmbPresets = new JComboBox();
	private JButton btnMatcherDetails = new JButton("Explanation");
	private JButton btnSavePresets = new JButton("Save"); 
	private JButton btnDeletePresets = new JButton("Delete");
	private JCheckBox completionBox = new JCheckBox("Completion mode");
	private JCheckBox provenanceBox = new JCheckBox("Save mapping provenance");;
	private JCheckBox chkThreadedOverlap = new JCheckBox("Threaded overlap");
	private JCheckBox chkThreadedMode = new JCheckBox("Threaded mode");
	private JCheckBox chkCustomLabel = new JCheckBox("Custom label:");
	private JPanel topPanel, generalPanel;
	private JScrollPane mainScroll;
	private JTextField txtCustomLabel = new JTextField();;
	
	/* State variables */
	//private boolean showPresets = true;
	//private boolean showGeneralSettings = true;	
	//private boolean success = false;
	//private boolean matcherDefined = false;
	
	/* Abstract Matcher variables */
	private AbstractMatcherParametersPanel parametersPanel;
	private DefaultMatcherParameters params;
	private AbstractMatcher matcher;
	
	private MessageDispatch<Object> dispatch;
	
	public MatchingAlgorithmParametersPanel(MessageDispatch<Object> dispatch) {
		super();

		this.dispatch = dispatch;
		
		//this.showPresets = showPresets;
		//this.showGeneralSettings = showGeneralSettings;
		//this.matcherDefined  = true;
		
		initComponents();  // initialize the components
		
		//This is the specific panel defined by the developer to set additional parameters to the specific matcher implemented
		if( matcherCombo.getSelectedItem() != null ) {
			String matcherName = matcherCombo.getSelectedItem().toString();
			matcher = Core.getInstance().getMatchingAlgorithm(matcherName);
			
			dispatch.publish(new SimpleMessage<Object>(
					MatchingTaskCreatorDialogMessages.SELECT_MATCHING_ALGORITHM.name(), (Object)matcher));
		}
		
		if( matcher != null ) { 
			try {
				parametersPanel = matcher.getParametersPanel();
			}
			catch( RuntimeException ex ) {
				parametersPanel = null;
			}
		}

		checkInputMatchers(matcher);
		
		initLayout();
	}
	
	/**
	 * Initialize the layout of this panel.
	 */
	private void initLayout() {
		
		removeAll();
		setLayout(null);
		
		// update the provenanceBox
		if( matcher != null && matcher.supportsFeature(MatcherFeature.MAPPING_PROVENANCE)) { 
			provenanceBox.setEnabled(true);
		} else { 
			provenanceBox.setEnabled(false);
		}
		
		// initialize the matcher panel.
		JPanel matcherPanel = new JPanel();
		
		GroupLayout matcherPanelLayout = new GroupLayout(matcherPanel);
		matcherPanelLayout.setAutoCreateContainerGaps(true);
		matcherPanelLayout.setAutoCreateGaps(true);
				
		// horizontal setup
		GroupLayout.ParallelGroup mainHorizontalGroup = matcherPanelLayout.createParallelGroup(Alignment.CENTER, false);
		mainHorizontalGroup.addComponent(topPanel);
		mainHorizontalGroup.addComponent(generalPanel);
		if( parametersPanel != null ) mainHorizontalGroup.addComponent(parametersPanel);	
		matcherPanelLayout.setHorizontalGroup( mainHorizontalGroup );
		
		// vertical setup
		GroupLayout.SequentialGroup mainVerticalGroup = matcherPanelLayout.createSequentialGroup();
		mainVerticalGroup.addComponent(topPanel);
		mainVerticalGroup.addGap(10);
		mainVerticalGroup.addComponent(generalPanel);
		mainVerticalGroup.addGap(10);
		if( parametersPanel != null ) mainVerticalGroup.addComponent(parametersPanel);
		matcherPanelLayout.setVerticalGroup( mainVerticalGroup ); 

		matcherPanel.setLayout(matcherPanelLayout);	
				
		mainScroll = new JScrollPane();
		mainScroll.getVerticalScrollBar().setUnitIncrement(20);
		mainScroll.setViewportView(matcherPanel);
	
		// put everything together.
				
		GroupLayout mainPanelLayout = new GroupLayout(this);
		mainPanelLayout.setAutoCreateContainerGaps(false);
		mainPanelLayout.setAutoCreateGaps(false);
		
		mainPanelLayout.setHorizontalGroup( mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(mainScroll)
		);
		
		mainPanelLayout.setVerticalGroup( mainPanelLayout.createSequentialGroup()
				.addComponent(mainScroll)
		);
		
		this.setLayout(mainPanelLayout);
	}
	
	private void initComponents() {
		
		// Action Listeners
		matcherCombo.addActionListener(this);
		chkCustomLabel.addActionListener(this);
		chkThreadedMode.addActionListener(this);
		btnMatcherDetails.addActionListener(this);
				
		txtCustomLabel.setEnabled(false);
		chkThreadedOverlap.setEnabled(false);

		lblPresets.setVisible(false);
		cmbPresets.setVisible(false);
		btnSavePresets.setVisible(false);
		btnDeletePresets.setVisible(false);
	
		// top panel
		topPanel = createTopPanel();

		// general panel
		generalPanel = createGeneralPanel();
	}
	
	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		
		GroupLayout topLayout = new GroupLayout(topPanel);
		topLayout.setAutoCreateContainerGaps(false);
		topLayout.setAutoCreateGaps(true);
		
		
		topLayout.setHorizontalGroup( topLayout.createSequentialGroup()
				.addComponent(matcherLabel)
				.addComponent(matcherCombo)
				.addComponent(btnMatcherDetails)
	            .addComponent(lblPresets)
	            .addComponent(cmbPresets)
	            .addComponent(btnSavePresets)
	            .addComponent(btnDeletePresets)
		);
		
		topLayout.setVerticalGroup( topLayout.createParallelGroup(Alignment.CENTER, false)
				.addComponent(matcherLabel)
				.addComponent(matcherCombo)
				.addComponent(btnMatcherDetails)
	            .addComponent(lblPresets)
	            .addComponent(cmbPresets)
	            .addComponent(btnSavePresets)
	            .addComponent(btnDeletePresets)
		);

		topPanel.setLayout(topLayout);
		
		return topPanel;
	}
	
	/**
	 * @return A panel that lays out all the general setting components.
	 */
	private JPanel createGeneralPanel() {
		
		JPanel generalPanel = new JPanel();
		//generalPanel.setBorder(new TitledBorder("General Settings"));
		
		//set generalPanel
		GroupLayout generalLayout = new GroupLayout(generalPanel);
		generalLayout.setAutoCreateContainerGaps(false);
		generalLayout.setAutoCreateGaps(true);
		
		generalLayout.setHorizontalGroup( generalLayout.createParallelGroup()
				.addGroup( generalLayout.createSequentialGroup()
						.addComponent(completionBox)
						.addGap(10)
						.addComponent(provenanceBox)
						.addGap(10)
						.addComponent(chkThreadedMode)
						.addComponent(chkThreadedOverlap)
				)
				.addGroup( generalLayout.createSequentialGroup()
						.addComponent(chkCustomLabel)
						.addComponent(txtCustomLabel)
				)
				
				
		);
		
		
		generalLayout.setVerticalGroup( generalLayout.createSequentialGroup()
				.addGroup( generalLayout.createParallelGroup(Alignment.CENTER)
						.addComponent(completionBox)
						.addComponent(provenanceBox)
						.addComponent(chkThreadedMode)
						.addComponent(chkThreadedOverlap)
				)
				.addGap(5)
				.addGroup( generalLayout.createParallelGroup(Alignment.CENTER, false)
						.addComponent(chkCustomLabel)
						.addComponent(txtCustomLabel)
				)
				
		);
		
		generalPanel.setLayout(generalLayout);
		
		return generalPanel;
	}
	
	private JScrollPane createMatcherSettingsScroll(JPanel parametersPanel) {
		JScrollPane settingsScroll = new JScrollPane(parametersPanel);
		settingsScroll.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Matcher Specific Settings"));
		
		return settingsScroll;
	}
	
	private void checkInputMatchers(AbstractMatcher currentMatcher) {
		if( currentMatcher == null ) return;
		//Set input matchers into the abstractmatcher VERY IMPORTANT to set them before invoking the parameter panel, in fact the parameter panel may need to work on inputMatchers also.
		List<MatchingTask> selectedResults = UICore.getUI().getSelectedTasks();
		
		// Check the maximum number of inputs matchers.
		if( selectedResults.size() > currentMatcher.getMaxInputMatchers() ) {
			System.err.println("You have selected more than " + 
					currentMatcher.getMaxInputMatchers() + " input matcher(s).  Using the top " + 
					currentMatcher.getMaxInputMatchers() + " matcher(s).");
		}
		
		// TODO: Check the minimum number of input matchers too. -- Cosmin.
	}
	
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if( ae.getSource() == chkThreadedMode ) {
			chkThreadedOverlap.setEnabled( chkThreadedMode.isSelected() );
			return;
		}
		
		if(ae.getSource() == matcherCombo){
			matcher = Core.getInstance().getMatchingAlgorithm(matcherCombo.getSelectedItem().toString());
			if( matcher == null ) return;
			
			checkInputMatchers(matcher);
			
			if(matcher.needsParam() && matcher.getParametersPanel() != null){  
				parametersPanel = matcher.getParametersPanel(); 
				parametersPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Matcher Specific Settings"));
			}
			else { 
				parametersPanel = null; 
			}
			
			if( !chkCustomLabel.isSelected() ) txtCustomLabel.setText(matcher.getName());
			
			initLayout();
			
			dispatch.publish(new SimpleMessage<Object>(
					MatchingTaskCreatorDialogMessages.SELECT_MATCHING_ALGORITHM.name(), (Object)matcher));
		}
		else if( obj == btnMatcherDetails ) {
			if( matcher != null ) {
				Utility.displayMessagePane(matcher.getDetails(), "Matcher details");
			}
		}
		else if( obj == chkCustomLabel ) {
			txtCustomLabel.setEnabled(chkCustomLabel.isSelected());
		}
	}
	
	public AbstractMatcher getMatcher() {
		return matcher;
	}
	
	public DefaultMatcherParameters getMatcherParameters() {
		if( parametersPanel == null ) {
			return new DefaultMatcherParameters();
		}
		else {
			return parametersPanel.getParameters();
		}
	}
}
