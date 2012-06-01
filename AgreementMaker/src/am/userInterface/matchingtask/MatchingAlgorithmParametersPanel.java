package am.userInterface.matchingtask;

import java.awt.BorderLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.MatcherFeature;
import am.app.mappingEngine.MatcherResult;
import am.app.ontology.profiling.OntologyProfilerPanel;
import am.app.ontology.profiling.ProfilerRegistry;
import am.app.osgi.MatcherNotFoundException;

public class MatchingAlgorithmParametersPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -1141452251567415606L;

	/* UI Components */
	private JLabel matcherLabel, lblPresets;
	private JComboBox matcherCombo, cmbPresets;
	private JButton btnMatcherDetails, btnSavePresets, btnDeletePresets;
	private JCheckBox completionBox, provenanceBox, chkCustomName, chkThreadedMode, chkThreadedOverlap;
	private JPanel topPanel, generalPanel;
	private JScrollPane settingsScroll;
	private JTextField txtCustomName;
	
	/* State variables */
	//private boolean showPresets = true;
	//private boolean showGeneralSettings = true;	
	//private boolean success = false;
	//private boolean matcherDefined = false;
	
	/* Abstract Matcher variables */
	private AbstractMatcherParametersPanel parametersPanel;
	private DefaultMatcherParameters params;
	private AbstractMatcher matcher;
	//private ArrayList<AbstractMatcher> selectedMatchers;
	
	/* Ontology profiling panel */
	private OntologyProfilerPanel matchTimeProfilingPanel = null;
	
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public MatchingAlgorithmParametersPanel() {
		super();

		//this.showPresets = showPresets;
		//this.showGeneralSettings = showGeneralSettings;
		//this.matcherDefined  = true;
		
		initComponents();  // initialize the components
		
		//This is the specific panel defined by the developer to set additional parameters to the specific matcher implemented
		String matcherName = matcherCombo.getSelectedItem().toString();
		matcher = Core.getInstance().getMatchingAlgorithm(matcherName);
		
		if( matcher != null ) 
			parametersPanel = matcher.getParametersPanel();

		settingsScroll = createMatcherSettingsScroll(parametersPanel);
		
		checkInputMatchers(matcher);
		
		initLayout();
	}
	
	
	private void initLayout() {
		
		// update the provenanceBox
		if( matcher != null && matcher.supportsFeature(MatcherFeature.MAPPING_PROVENANCE)) provenanceBox.setEnabled(true);
		else provenanceBox.setEnabled(false);
		
		// initialize the matcher panel.
		JPanel matcherPanel = new JPanel();
		
		GroupLayout matcherPanelLayout = new GroupLayout(matcherPanel);
		matcherPanelLayout.setAutoCreateContainerGaps(true);
		matcherPanelLayout.setAutoCreateGaps(true);
				
		// horizontal setup
		GroupLayout.ParallelGroup mainHorizontalGroup = matcherPanelLayout.createParallelGroup(Alignment.TRAILING, false);
		mainHorizontalGroup.addComponent(topPanel);
		mainHorizontalGroup.addComponent(generalPanel);
		if( settingsScroll != null ) mainHorizontalGroup.addComponent(settingsScroll);	
		matcherPanelLayout.setHorizontalGroup( mainHorizontalGroup );
		
		// vertical setup
		GroupLayout.SequentialGroup mainVerticalGroup = matcherPanelLayout.createSequentialGroup();
		mainVerticalGroup.addComponent(topPanel);
		mainVerticalGroup.addComponent(generalPanel);
		if( settingsScroll != null ) mainVerticalGroup.addComponent(settingsScroll);
		matcherPanelLayout.setVerticalGroup( mainVerticalGroup ); 

		matcherPanel.setLayout(matcherPanelLayout);	
		
		// initialize the ontology profiling panel.		
		JPanel profilingPanel = new JPanel();
		profilingPanel.setLayout(new BorderLayout());
		
		
		if( Core.getInstance().getOntologyProfiler() == null ) {
			// there is no ontology profiling algorithm defined.	
			profilingPanel.add(new JLabel("No ontology profiling algorithm selected."), BorderLayout.CENTER);
		} else if( !matcher.supportsFeature( MatcherFeature.ONTOLOGY_PROFILING ) ) {
			profilingPanel.add(new JLabel("This matcher does not support ontology profiling."), BorderLayout.CENTER);
		} else if( Core.getInstance().getOntologyProfiler().getProfilerPanel(false) == null ){
			// the ontology profiler does not have a match time parameters panel
			ProfilerRegistry name = Core.getInstance().getOntologyProfiler().getName();
			profilingPanel.add(new JLabel( name.getProfilerName() + " has been selected." + 
					"\nThe profiling algorithm does not need parameters at match time."), BorderLayout.CENTER);
		} else {
			if( matchTimeProfilingPanel == null ) {
				matchTimeProfilingPanel = Core.getInstance().getOntologyProfiler().getProfilerPanel(false);
			}
			JScrollPane profilingScroll = new JScrollPane(matchTimeProfilingPanel);
			profilingScroll.getVerticalScrollBar().setUnitIncrement(20);
			profilingPanel.add( profilingScroll, BorderLayout.CENTER);
		}
		
		// add the tabs to the JTabbedPane
		JTabbedPane dialogTabbedPane = new JTabbedPane();
		dialogTabbedPane.addTab("Matcher", matcherPanel);
		dialogTabbedPane.addTab("Annotation Profiling", profilingPanel);
		
		// put everything together.
				
		GroupLayout mainPanelLayout = new GroupLayout(this);
		mainPanelLayout.setAutoCreateContainerGaps(true);
		mainPanelLayout.setAutoCreateGaps(true);
		
		mainPanelLayout.setHorizontalGroup( mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(dialogTabbedPane)
		);
		
		mainPanelLayout.setVerticalGroup( mainPanelLayout.createSequentialGroup()
				.addComponent(dialogTabbedPane)
		);
		
		this.setLayout(mainPanelLayout);
	}
	
	private void initComponents() {
		matcherLabel = new JLabel("Matcher:");
		//String[] matcherList = MatcherFactory.getMatcherComboList();
		matcherCombo = new MatcherComboBox();
		matcherCombo.addActionListener(this);
		
		chkCustomName = new JCheckBox("Custom name:");
		chkCustomName.addActionListener(this);
		txtCustomName = new JTextField();
		txtCustomName.setEnabled(false);
		
		chkThreadedMode = new JCheckBox("Threaded mode");
		chkThreadedMode.addActionListener(this);
		chkThreadedOverlap = new JCheckBox("Threaded overlap");
		chkThreadedOverlap.setEnabled(false);
		
		btnMatcherDetails = new JButton("Explanation");
		btnMatcherDetails.addActionListener(this);
		
		lblPresets = new JLabel("Presets:");
		cmbPresets = new JComboBox();
		btnSavePresets = new JButton("Save");
		btnDeletePresets = new JButton("Delete");
		
		lblPresets.setVisible(false);
		cmbPresets.setVisible(false);
		btnSavePresets.setVisible(false);
		btnDeletePresets.setVisible(false);
		
		completionBox = new JCheckBox("Completion mode");
		provenanceBox = new JCheckBox("Save mapping provenance");
	
		// top panel
		topPanel = createTopPanel();

		// general panel
		generalPanel = createGeneralPanel();
	}
	
	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		
		GroupLayout topLayout = new GroupLayout(topPanel);
		topLayout.setAutoCreateContainerGaps(true);
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
	
	private JPanel createGeneralPanel() {
		
		JPanel generalPanel = new JPanel();
		generalPanel.setBorder(new TitledBorder("General Settings"));
		
		//set generalPanel
		GroupLayout generalLayout = new GroupLayout(generalPanel);
		generalLayout.setAutoCreateContainerGaps(true);
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
						.addComponent(chkCustomName)
						.addComponent(txtCustomName)
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
						.addComponent(chkCustomName)
						.addComponent(txtCustomName)
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
		List<MatcherResult> selectedResults = Core.getUI().getSelectedResults();
		if( selectedResults.size() > currentMatcher.getMaxInputMatchers() ) {
			System.err.println("You have selected more than " + 
					currentMatcher.getMaxInputMatchers() + " input matcher(s).  Using the top " + 
					currentMatcher.getMaxInputMatchers() + " matcher(s).");
		}
	}
	
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if( ae.getSource() == chkThreadedMode ) {
			chkThreadedOverlap.setEnabled( chkThreadedMode.isSelected() );
			return;
		}
		
		if(ae.getSource() == matcherCombo && matcher != null){
			try {
				matcher=Core.getInstance().getFramework().getRegistry().getMatcherByName(matcherCombo.getSelectedItem().toString());
			} catch (MatcherNotFoundException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				e.printStackTrace();
				return;
			}
			
			//matcher = MatcherFactory.getMatcherInstance(
			//		MatcherFactory.getMatchersRegistryEntry(matcherCombo.getSelectedItem().toString()), 0);
			
			checkInputMatchers(matcher);
			
			if(matcher.needsParam() && matcher.getParametersPanel() != null){  
				parametersPanel = matcher.getParametersPanel(); 
				settingsScroll = new JScrollPane(parametersPanel);
			}
			else { 
				parametersPanel = null; 
				settingsScroll = new JScrollPane();
			}
			//settingsScroll = new JScrollPane(parametersPanel);
			settingsScroll.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Matcher Specific Settings"));
			
			if( !chkCustomName.isSelected() ) txtCustomName.setText(matcher.getName());
			
			initLayout();
		}
		else if( obj == btnMatcherDetails ) {
			Utility.displayMessagePane(matcher.getDetails(), "Matcher details");
		}
		else if( obj == chkCustomName ) {
			txtCustomName.setEnabled(chkCustomName.isSelected());
		}
	}
}
