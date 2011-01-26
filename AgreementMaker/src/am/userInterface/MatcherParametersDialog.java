package am.userInterface;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JButton; 
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.ontology.profiling.ProfilerRegistry;
import am.userInterface.table.MatchersTablePanel;


/**
 * This dialog lets the user select the matching algorithm and its parameters.
 */

public class MatcherParametersDialog extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = 7150332604304262664L;

	private JLabel matcherLabel;
	private JComboBox matcherCombo;
	private JButton btnMatcherDetails;
	private JLabel lblPresets;
	private JComboBox cmbPresets;
	private JButton btnSavePresets;
	private JButton btnDeletePresets;
	
	private JLabel thresholdLabel;
	private JComboBox thresholdCombo;
	private JLabel sourceRelLabel;
	private JComboBox sourceRelCombo;
	private JLabel targetRelLabel;
	private JComboBox targetRelCombo;
	private JCheckBox completionBox;
	
	private JButton runButton;
	private JButton cancelButton;
	
	private JPanel topPanel;
	private JPanel generalPanel;
	//private JPanel settingsPanel;
	
	//private JTabbedPane dialogTabbedPane; // a tabbed pane
	
	private boolean showPresets = true;
	private boolean showGeneralSettings = true;
	
	private JScrollPane settingsScroll;
	
	//GroupLayout.ParallelGroup mainHorizontalGroup;
	//GroupLayout.SequentialGroup mainVerticalGroup;
	
	private boolean success = false;
	AbstractMatcherParametersPanel parametersPanel;
	
	AbstractParameters params;
	
	AbstractMatcher matcher;

	private boolean matcherDefined = false;
	
	ArrayList<AbstractMatcher> selectedMatchers;
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public MatcherParametersDialog(AbstractMatcher a, boolean showPresets, boolean showGeneralSettings) {
		super(Core.getUI().getUIFrame(), true);

		this.showPresets = showPresets;
		this.showGeneralSettings = showGeneralSettings;
		this.matcherDefined  = true;
		
		initComponents();  // initialize the components
		
		// select the matcher in the combobox
		ComboBoxModel model = matcherCombo.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			String curr = model.getElementAt(i).toString();
			if(curr.equals(a.getName().getMatcherName()))
				matcherCombo.setSelectedIndex(i);
		}
		
		matcherCombo.setSelectedItem(a.getName());		
		matcherCombo.setEnabled(false);  // user cannot change the matcher in this mode
		
		String name = a.getName().getMatcherName();
		setTitle(name+": additional parameters");
		
		//This is the specific panel defined by the developer to set additional parameters to the specific matcher implemented
		parametersPanel = a.getParametersPanel();

		settingsScroll = createMatcherSettingsScroll(parametersPanel);
		
		matcher = a;  // we want to use our own matcher.
		
		addInputMatchers(matcher);
		
		initLayout(showPresets, showGeneralSettings);
		
		getRootPane().setDefaultButton(runButton);
		
		setVisible(true);
	}
	
	private void addInputMatchers(AbstractMatcher currentMatcher) {
		//Set input matchers into the abstractmatcher VERY IMPORTANT to set them before invoking the parameter panel, in fact the parameter panel may need to work on inputMatchers also.
		int[] rowsIndex = Core.getInstance().getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
		for(int i = 0; i<rowsIndex.length && i< currentMatcher.getMaxInputMatchers(); i++) {
			AbstractMatcher input = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
			currentMatcher.addInputMatcher(input);
		}
		
	}

	public MatcherParametersDialog() {
		super(Core.getUI().getUIFrame(), true);
		
		initComponents();
		
		matcher = MatcherFactory.getMatcherInstance(
				MatcherFactory.getMatchersRegistryEntry(matcherCombo.getSelectedItem().toString()), Core.getInstance().getMatcherInstances().size());
		
		addInputMatchers(matcher);
		
		String name = matcher.getName().getMatcherName();
		setTitle(name+": additional parameters");
		//This is the specific panel defined by the developer to set additional parameters to the specific matcher implemented
		parametersPanel = matcher.getParametersPanel();
		
		settingsScroll = createMatcherSettingsScroll(parametersPanel);
		
		initLayout();

		getRootPane().setDefaultButton(runButton);
		
		// restore last selected matcher
		matcherCombo.setSelectedIndex( Core.getAppPreferences().getInt("MATCHERSPARAMETERSDIALOG_SELECTEDMATCHER") );
		
		setVisible(true);
	}

	
	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
	    JRootPane rootPane = new JRootPane();
	    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
	    Action actionListener = new AbstractAction() {
			private static final long serialVersionUID = 1774539460694983567L;
			public void actionPerformed(ActionEvent actionEvent) {
		        cancelButton.doClick();
		      }
	    };
	    InputMap inputMap = rootPane
	        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    inputMap.put(stroke, "ESCAPE");
	    rootPane.getActionMap().put("ESCAPE", actionListener);

	    return rootPane;
	  }
	
	private void initComponents() {
		matcherLabel = new JLabel("Matcher:");
		//String[] matcherList = MatcherFactory.getMatcherComboList();
		matcherCombo = new MatcherComboBox();
		matcherCombo.addActionListener(this);
		
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
		
		thresholdLabel = new JLabel("Threshold:");
		String[] thresholdList = Utility.getPercentStringList();
		thresholdCombo = new JComboBox(thresholdList);
		thresholdCombo.setSelectedItem("60%");
		
		sourceRelLabel = new JLabel("Source relations:");
		Object[] numRelList = Utility.getNumRelList();
		sourceRelCombo = new JComboBox(numRelList);
		sourceRelCombo.setSelectedItem(1);
		
		targetRelLabel = new JLabel("Target relations:");
		targetRelCombo = new JComboBox(numRelList);
		targetRelCombo.setSelectedItem(1);
		
		completionBox = new JCheckBox("Completion mode");
	
		// top panel
		topPanel = createTopPanel();

		// general panel
		generalPanel = createGeneralPanel();
		
		// cancel and run buttons
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
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
		
		topLayout.setVerticalGroup( topLayout.createParallelGroup()
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
				.addGroup(  generalLayout.createSequentialGroup()
						.addComponent(thresholdLabel)
						.addComponent(thresholdCombo)
			            .addComponent(sourceRelLabel)
			            .addComponent(sourceRelCombo)
			            .addComponent(targetRelLabel)
			            .addComponent(targetRelCombo)
				)
				.addComponent(completionBox)
				
		);
		
		
		generalLayout.setVerticalGroup( generalLayout.createSequentialGroup()
				.addGroup( generalLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(thresholdLabel)
						.addComponent(thresholdCombo)
			            .addComponent(sourceRelLabel)
			            .addComponent(sourceRelCombo)
			            .addComponent(targetRelLabel)
			            .addComponent(targetRelCombo)
				)
				.addComponent(completionBox)
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
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	private void initLayout() { initLayout(showPresets, showGeneralSettings); } // default settings
	
	private void initLayout(boolean showPresets, boolean showGeneralSettings) {
		//overall dialog layout
		
		
		// initialize the matcher panel.
		JPanel matcherPanel = new JPanel();
		
		GroupLayout matcherPanelLayout = new GroupLayout(matcherPanel);
		matcherPanelLayout.setAutoCreateContainerGaps(true);
		matcherPanelLayout.setAutoCreateGaps(true);
				
		// horizontal setup
		GroupLayout.ParallelGroup mainHorizontalGroup = matcherPanelLayout.createParallelGroup(Alignment.TRAILING);
		if( showPresets ) mainHorizontalGroup.addComponent(topPanel);
		if( showGeneralSettings ) mainHorizontalGroup.addComponent(generalPanel);
		mainHorizontalGroup.addComponent(settingsScroll);	
		matcherPanelLayout.setHorizontalGroup( mainHorizontalGroup );
		
		// vertical setup
		GroupLayout.SequentialGroup mainVerticalGroup = matcherPanelLayout.createSequentialGroup();
		if( showPresets ) mainVerticalGroup.addComponent(topPanel);
		if( showGeneralSettings ) mainVerticalGroup.addComponent(generalPanel);
		mainVerticalGroup.addComponent(settingsScroll);
		matcherPanelLayout.setVerticalGroup( mainVerticalGroup ); 

		matcherPanel.setLayout(matcherPanelLayout);	
		
		// initialize the ontology profiling panel.		
		JPanel profilingPanel = new JPanel();
		profilingPanel.setLayout(new BorderLayout());
		
		if( Core.getInstance().getOntologyProfiler() == null ) {
			// there is no ontology profiling algorithm defined.	
			profilingPanel.add(new JLabel("No ontology profiling algorithm selected."), BorderLayout.CENTER);
		} else if( Core.getInstance().getOntologyProfiler().getProfilerPanel(false) == null ){
			// the ontology profiler does not have a match time parameters panel
			ProfilerRegistry name = Core.getInstance().getOntologyProfiler().getName();
			profilingPanel.add(new JLabel( name.getProfilerName() + " has been selected." + 
					"\nThe profiling algorithm does not need parameters at match time."), BorderLayout.CENTER);
		} else {
			profilingPanel.add( Core.getInstance().getOntologyProfiler().getProfilerPanel(false), BorderLayout.CENTER);
		}
		
		// add the tabs to the JTabbedPane
		JTabbedPane dialogTabbedPane = new JTabbedPane();
		dialogTabbedPane.addTab("Matcher", matcherPanel);
		dialogTabbedPane.addTab("Ontology Profiling", profilingPanel);
		
		// put everything together.
				
		GroupLayout mainPanelLayout = new GroupLayout(getContentPane());
		mainPanelLayout.setAutoCreateContainerGaps(true);
		mainPanelLayout.setAutoCreateGaps(true);
		
		mainPanelLayout.setHorizontalGroup( mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(dialogTabbedPane)
				.addGroup( mainPanelLayout.createSequentialGroup()
						.addComponent(cancelButton)
						.addComponent(runButton)
				)
		);
		
		mainPanelLayout.setVerticalGroup( mainPanelLayout.createSequentialGroup()
				.addComponent(dialogTabbedPane)
				.addGroup( mainPanelLayout.createParallelGroup()
						.addComponent(cancelButton)
						.addComponent(runButton)
				)
		);
		
		getContentPane().setLayout(mainPanelLayout);
		
		if( getFont() != null && getFontMetrics(getFont()) != null ) {
			FontMetrics fm = getFontMetrics(getFont());
			// +100 to allow for icon and "x-out" button
			int width = fm.stringWidth(getTitle()) + 100;
			width = Math.max(width, getPreferredSize().width);
			setSize(new Dimension(width, getPreferredSize().height));
		}
		
		pack();  // make it smaller.
		setLocationRelativeTo(null); 	// center the window on the screen
		
	}	

	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if(ae.getSource() == matcherCombo && !matcherDefined){
			matcher = MatcherFactory.getMatcherInstance(
					MatcherFactory.getMatchersRegistryEntry(matcherCombo.getSelectedItem().toString()), 0);
			
			String name = matcher.getName().getMatcherName();
			setTitle(name+": additional parameters");
			
			addInputMatchers(matcher);
			
			setRootPane( createRootPane() );  // ignore the old root pane
			getRootPane().setDefaultButton(runButton);
			
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
			
			setDefaultCommonParameters(matcher);
			
			initLayout();
			pack();
			setLocationRelativeTo(null); 	// center the window on the screen
		}
		else if(obj == cancelButton){
			success = false;
			//setModal(false);
			setVisible(false);  // required
		}
		else if(obj == runButton){
			
			String check = null;
			if ( parametersPanel != null ) check = parametersPanel.checkParameters();
			if(check == null || check.equals("")) {
				success = true;
				//setModal(false);
				setVisible(false);  // required
			}
			else { 
				Utility.displayErrorPane(check, "Illegal Parameters" ); 
				return;
			}
			
			if( parametersPanel != null ) {
				params = parametersPanel.getParameters();
			} else {
				params = new AbstractParameters();
			}
			
			// fill in threshold
			params.threshold = Utility.getDoubleFromPercent((String)thresholdCombo.getSelectedItem());
			
			// fill in cardinality
			params.maxSourceAlign  = Utility.getIntFromNumRelString((String)sourceRelCombo.getSelectedItem());
			params.maxTargetAlign = Utility.getIntFromNumRelString((String)targetRelCombo.getSelectedItem());
			
			// fill in completion mode
			params.completionMode = completionBox.isSelected();
			
			
			matcher.setParam(params);
			
			// save selected index
			if( !matcherDefined ) Core.getAppPreferences().saveInt("MATCHERSPARAMETERSDIALOG_SELECTEDMATCHER", matcherCombo.getSelectedIndex());
		}
		else if( obj == btnMatcherDetails ) {
			Utility.displayMessagePane(matcher.getDetails(), "Matcher details");
		}
	}
	
	
	public boolean parametersSet() {
		return success;
	}
	
	public AbstractParameters getParameters() {
		return params;
	}
	
	public AbstractMatcher getMatcher() {
		return matcher;
	}
	
	public static void main(String[] args) {
		//AbstractMatcher matcher = MatcherFactory.getMatcherInstance(MatchersRegistry.IISM, 1);
		//new MatcherParametersDialog(matcher);
		
		new MatcherParametersDialog();

	}
	
	// TODO: Is this method still required?
	public void match(AbstractMatcher currentMatcher, boolean defaultParam) throws Exception{
		MatchersTablePanel matchersTablePanel = Core.getUI().getControlPanel().getMatchersTablePanel();
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows(); //indexes in the table correspond to the indexes of the matchers in the matcherInstances list in core class
		int selectedMatchers = rowsIndex.length;
		if(!Core.getInstance().ontologiesLoaded() ) {
			Utility.displayErrorPane("You have to load Source and Target ontologies before running any matcher\nClick on File Menu and select Open Ontology functions ", null);
		}
		else if(currentMatcher.getMinInputMatchers() > selectedMatchers ) {
			Utility.displayErrorPane("Select at least "+currentMatcher.getMinInputMatchers()+" matchings from the table to run this matcher.", null);
		}
		else {
			//Set input matchers into the abstractmatcher VERY IMPORTANT to set them before invoking the parameter panel, in fact the parameter panel may need to work on inputMatchers also.
			currentMatcher.setOptimized(completionBox.isSelected());//this method set maxInputMatcher to 1
			for(int i = 0; i<rowsIndex.length && i< currentMatcher.getMaxInputMatchers(); i++) {
				AbstractMatcher input = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
				currentMatcher.addInputMatcher(input);
			}
			boolean everythingOk = true;
			//Asking parameters before setting input matcher list, just because the user can still cancel the operation
			if(currentMatcher.needsParam()) {
				everythingOk = false;
				MatcherParametersDialog dialog = new MatcherParametersDialog(currentMatcher, true, true);
				if(dialog.parametersSet()) {
					currentMatcher.setParam(dialog.getParameters());
					everythingOk = true;
				}
				dialog.dispose();
			}
			if(everythingOk) {
				if(defaultParam) {
					currentMatcher.setThreshold(currentMatcher.getDefaultThreshold());
					currentMatcher.setMaxSourceAlign(currentMatcher.getDefaultMaxSourceRelations());
					currentMatcher.setMaxTargetAlign(currentMatcher.getDefaultMaxTargetRelations());
				}
				else {
					currentMatcher.setThreshold(Utility.getDoubleFromPercent((String)thresholdCombo.getSelectedItem()));
					currentMatcher.setMaxSourceAlign(Utility.getIntFromNumRelString((String)sourceRelCombo.getSelectedItem()));
					currentMatcher.setMaxTargetAlign(Utility.getIntFromNumRelString((String)targetRelCombo.getSelectedItem()));
				}
				
				
				// The dialog will start the matcher in a background thread, show progress as the matcher is running, and show the report at the end.
				new MatcherProgressDialog(currentMatcher);  // Program flow will not continue until the dialog is dismissed. (User presses Ok or Cancel)
				if(!currentMatcher.isCancelled()) {  // If the algorithm finished successfully, add it to the control panel.
					matchersTablePanel.addMatcher(currentMatcher);
					Core.getUI().redisplayCanvas();
				}	

				if( Core.DEBUG ) System.out.println("Matching Process Complete");
			}
		}
	}
	
	/**
	 * This method sets up the preset dropdown combo box.
	 * @param mr
	 */
	private void setupPresets(MatchersRegistry mr) {
		// check to see if the presets directory exists.
		String presetDir = System.getProperty("user.dir") + File.pathSeparator + "presets";
		
		File presetsDirectory = new File(presetDir);
		if( !presetsDirectory.exists() ) {
			// attempt to create the presets directory
			try {
				if( !presetsDirectory.mkdir() ) {
					// the directory doesn't exist and we cannot make it
					lblPresets.setEnabled(false);
					cmbPresets.setEnabled(false);
					btnSavePresets.setEnabled(false);
					btnDeletePresets.setEnabled(false);
					return;
				}
			} catch (SecurityException se) {
				se.printStackTrace();
				lblPresets.setEnabled(false);
				cmbPresets.setEnabled(false);
				btnSavePresets.setEnabled(false);
				btnDeletePresets.setEnabled(false);
				return;
			}
		}
		
		AbstractParameters ap;
		
		
	}
	
	private void setDefaultCommonParameters(AbstractMatcher a) {
		//String matcherName = (String) matcherCombo.getSelectedItem();
		//AbstractMatcher a = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), 0); //i'm just using a fake instance so the algorithm code is not important i put 0 but maybe anythings
		thresholdCombo.setSelectedItem(Utility.getNoDecimalPercentFromDouble(a.getDefaultThreshold()));
		sourceRelCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxSourceRelations()));
		targetRelCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxTargetRelations()));
	}
	
}
