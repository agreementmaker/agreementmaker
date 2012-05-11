package am.userInterface;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatcherFeature;
import am.app.mappingEngine.MatchersRegistry;
import am.app.ontology.profiling.OntologyProfilerPanel;
import am.app.ontology.profiling.ProfilerRegistry;
import am.app.osgi.MatcherNotFoundException;
import am.userInterface.table.MatchersTablePanel;


/**
 * This dialog lets the user select the matching algorithm and its parameters.
 */

public class MatcherParametersDialog extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = 7150332604304262664L;

	/* UI Components */
	private JLabel matcherLabel, lblPresets, thresholdLabel, sourceRelLabel, targetRelLabel;
	private JComboBox matcherCombo, cmbPresets, thresholdCombo, sourceRelCombo, targetRelCombo;
	private JButton btnMatcherDetails, btnSavePresets, btnDeletePresets, runButton, cancelButton;
	private JCheckBox completionBox, provenanceBox, chkCustomName, chkThreadedMode, chkThreadedOverlap;
	private JPanel topPanel, generalPanel;
	private JScrollPane settingsScroll;
	private JTextField txtCustomName;
	
	/* State variables */
	private boolean showPresets = true;
	private boolean showGeneralSettings = true;	
	private boolean success = false;
	private boolean matcherDefined = false;
	
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
			if(curr.equals(a.getRegistryEntry().getMatcherName()))
				matcherCombo.setSelectedIndex(i);
		}
		
		matcherCombo.setSelectedItem(a.getName());		
		matcherCombo.setEnabled(false);  // user cannot change the matcher in this mode
		
		String name = a.getRegistryEntry().getMatcherName();
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
		int[] rowsIndex = Core.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
		if( rowsIndex.length > currentMatcher.getMaxInputMatchers() ) {
			System.err.println("You have selected more than " + 
					currentMatcher.getMaxInputMatchers() + " input matcher(s).  Using the top " + 
					currentMatcher.getMaxInputMatchers() + " matcher(s).");
		}
		for(int i = 0; i < rowsIndex.length && i < currentMatcher.getMaxInputMatchers(); i++) {
			AbstractMatcher input = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
			currentMatcher.addInputMatcher(input);
		}
		
	}

	public MatcherParametersDialog() {
		super(Core.getUI().getUIFrame(), true);
		
		initComponents();
		
		try {
			matcher = Core.getInstance().getFramework().getRegistry().getMatcherByName(matcherCombo.getSelectedItem().toString());
		} catch (MatcherNotFoundException e) {
			e.printStackTrace();
			matcher = null;
		}
		
		
		addInputMatchers(matcher);
		
		String name = matcher.getName();
		setTitle(name+": additional parameters");
		//This is the specific panel defined by the developer to set additional parameters to the specific matcher implemented
		if(matcher.needsParam() && matcher.getParametersPanel() != null){  
			parametersPanel = matcher.getParametersPanel(); 
			settingsScroll = new JScrollPane(parametersPanel);
		}
		else { 
			parametersPanel = null; 
			settingsScroll = new JScrollPane();
		}
		
		
		initLayout();

		getRootPane().setDefaultButton(runButton);
		
		// restore last selected matcher
		matcherCombo.setSelectedIndex( 
				Core.getAppPreferences().getInt("MATCHERSPARAMETERSDIALOG_SELECTEDMATCHER") >= matcherCombo.getItemCount() ? 
						matcherCombo.getItemCount() - 1 : 
						Core.getAppPreferences().getInt("MATCHERSPARAMETERSDIALOG_SELECTEDMATCHER")  
		);
		
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
	
	/**
	 * A hack to make the maximum size actually work.
	 */
	@Override
	public void paint(Graphics g) {
		Dimension d = getSize();
		Dimension m = getMaximumSize();
		boolean resize = d.width > m.width || d.height > m.height;
		d.width = Math.min(m.width, d.width);
		d.height = Math.min(m.height, d.height);
		if (resize) {
			//setVisible(false);
			setSize(d);
			setLocationRelativeTo(null);
			//setVisible(true);
		}
		super.paint(g);
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
		provenanceBox = new JCheckBox("Save mapping provenance");
	
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
				.addGroup(  generalLayout.createSequentialGroup()
						.addComponent(thresholdLabel)
						.addComponent(thresholdCombo)
			            .addComponent(sourceRelLabel)
			            .addComponent(sourceRelCombo)
			            .addComponent(targetRelLabel)
			            .addComponent(targetRelCombo)
				)
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
				.addGroup( generalLayout.createParallelGroup(Alignment.CENTER, false)
						.addComponent(thresholdLabel)
						.addComponent(thresholdCombo)
			            .addComponent(sourceRelLabel)
			            .addComponent(sourceRelCombo)
			            .addComponent(targetRelLabel)
			            .addComponent(targetRelCombo)
				)
				.addGap(5)
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
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	private void initLayout() { initLayout(showPresets, showGeneralSettings); } // default settings
	
	private void initLayout(boolean showPresets, boolean showGeneralSettings) {
		
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
		if( showPresets ) mainHorizontalGroup.addComponent(topPanel);
		if( showGeneralSettings ) mainHorizontalGroup.addComponent(generalPanel);
		if( settingsScroll != null ) mainHorizontalGroup.addComponent(settingsScroll);	
		matcherPanelLayout.setHorizontalGroup( mainHorizontalGroup );
		
		// vertical setup
		GroupLayout.SequentialGroup mainVerticalGroup = matcherPanelLayout.createSequentialGroup();
		if( showPresets ) mainVerticalGroup.addComponent(topPanel);
		if( showGeneralSettings ) mainVerticalGroup.addComponent(generalPanel);
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
		
		//overall dialog layout
		Dimension maxSize = Toolkit.getDefaultToolkit().getScreenSize();
		maxSize.height = maxSize.height - 40; // leave 20 pixels on top and bottom
		maxSize.width = maxSize.width - 40; // and 20 pixels on left and right
		setMaximumSize(maxSize);
		
		if( getFont() != null && getFontMetrics(getFont()) != null ) {
			FontMetrics fm = getFontMetrics(getFont());
			// +100 to allow for icon and "x-out" button
			int width = fm.stringWidth(getTitle()) + 100;
			width = Math.max(width, getPreferredSize().width);
			if( width > maxSize.width ) width = maxSize.width;
			if( getPreferredSize().height <= maxSize.height ) setSize(new Dimension(width, getPreferredSize().height));
			else setSize( new Dimension( width, maxSize.height ) );
		}
		
		pack();  // make it smaller.
		setLocationRelativeTo(null); 	// center the window on the screen
		
	}	

	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if( ae.getSource() == chkThreadedMode ) {
			chkThreadedOverlap.setEnabled( chkThreadedMode.isSelected() );
			return;
		}
		
		if(ae.getSource() == matcherCombo && !matcherDefined){
			try {
				matcher=Core.getInstance().getFramework().getRegistry().getMatcherByName(matcherCombo.getSelectedItem().toString());
			} catch (MatcherNotFoundException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				e.printStackTrace();
				return;
			}
			
			//matcher = MatcherFactory.getMatcherInstance(
			//		MatcherFactory.getMatchersRegistryEntry(matcherCombo.getSelectedItem().toString()), 0);

			String name = matcher.getName();//matcher.getRegistryEntry().getMatcherName();
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
			
			if( !chkCustomName.isSelected() ) txtCustomName.setText(matcher.getName());
			
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
				params = new DefaultMatcherParameters();
			}
			
			// fill in threshold
			params.threshold = Utility.getDoubleFromPercent((String)thresholdCombo.getSelectedItem());
			
			// fill in cardinality
			params.maxSourceAlign  = Utility.getIntFromNumRelString((String)sourceRelCombo.getSelectedItem());
			params.maxTargetAlign = Utility.getIntFromNumRelString((String)targetRelCombo.getSelectedItem());
			
			// fill in completion mode
			params.completionMode = completionBox.isSelected();
			params.storeProvenance = provenanceBox.isSelected();
			params.threadedExecution = chkThreadedMode.isSelected();
			params.threadedOverlap = params.threadedExecution && chkThreadedOverlap.isSelected();
			
			matcher.setParam(params);
			
			if( txtCustomName.isEnabled() ) matcher.setName(txtCustomName.getText());
			
			// set the ontology profiling parameters.
			if( matchTimeProfilingPanel != null ) {
				Core.getInstance().getOntologyProfiler().setMatchTimeParams(matchTimeProfilingPanel.getParameters());
			}
			
			// save selected index
			if( !matcherDefined ) Core.getAppPreferences().saveInt("MATCHERSPARAMETERSDIALOG_SELECTEDMATCHER", matcherCombo.getSelectedIndex());
		}
		else if( obj == btnMatcherDetails ) {
			Utility.displayMessagePane(matcher.getDetails(), "Matcher details");
		}
		else if( obj == chkCustomName ) {
			txtCustomName.setEnabled(chkCustomName.isSelected());
		}
	}
	
	
	public boolean parametersSet() {
		return success;
	}
	
	public DefaultMatcherParameters getParameters() {
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
					Core.getInstance().addMatcherResult(currentMatcher);
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
		
		DefaultMatcherParameters ap;
		
		
	}
	
	private void setDefaultCommonParameters(AbstractMatcher a) {
		//String matcherName = (String) matcherCombo.getSelectedItem();
		//AbstractMatcher a = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), 0); //i'm just using a fake instance so the algorithm code is not important i put 0 but maybe anythings
		thresholdCombo.setSelectedItem(Utility.getNoDecimalPercentFromDouble(a.getDefaultThreshold()));
		sourceRelCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxSourceRelations()));
		targetRelCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxTargetRelations()));
		provenanceBox.setSelected(false);
	}
	
}
