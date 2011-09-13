package am.userInterface;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import am.GlobalStaticVariables;
import am.app.ontology.instance.InstanceDataset.DatasetType;
import am.app.ontology.instance.endpoint.EndpointRegistry;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.utility.AMFileChooser;
/**
 * This class represents the Open Ontologies dialog, combining the 
 * loading of both ontologies into one dialog. 
 *
 */
public class OpenOntologyFileDialogCombined extends JDialog implements ActionListener {

	private static final long serialVersionUID = -3570106790068421107L;

	private JButton cancel, btnProceed;	
	private UI ui;
	private JPanel buttonsPanel;

	private static final String PREF_LASTFILTER = "LAST_SELECTED_FILTER";
	private static final String PREF_LASTFILE = "LAST_SELECTED_FILE";

	OntologyDefinitionPanel sourceODP, targetODP;
	
	
	public OpenOntologyFileDialogCombined(UI userInterface){
		super(userInterface.getUIFrame());
		ui = userInterface;

		//frame = new JDialog(ui.getUIFrame(), true);
		this.setTitle("Open Ontologies");

		//initializeComponents();

		//create the layouts for the different panels
		/*filePanel = createFilePanel();
		sourcePanel = createSourcePanel();
		targetPanel = createTargetPanel();
		labelsPanel = createLabelsPanel(); */
		cancel = new JButton("Cancel");
		btnProceed = new JButton("Proceed");

		btnProceed.addActionListener(this);
		cancel.addActionListener(this);

		
		buttonsPanel = createButtonsPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel sourcePanel = new JPanel();
		sourceODP = new OntologyDefinitionPanel();
		GroupLayout sl = sourceODP.createLayout(sourcePanel);
		sourcePanel.setLayout(sl);
		
		JPanel targetPanel = new JPanel();
		targetODP = new OntologyDefinitionPanel();
		GroupLayout tl = targetODP.createLayout(targetPanel);
		targetPanel.setLayout(tl);
		
		tabbedPane.addTab("Source Ontology", sourcePanel);
		tabbedPane.addTab("Target Ontology", targetPanel);		
					
		tabbedPane.setPreferredSize(new Dimension(700,100));
		
		//add the components the the frame
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup( layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(tabbedPane)
				.addComponent(buttonsPanel)	
		);

		layout.setVerticalGroup( layout.createSequentialGroup()
				.addComponent(tabbedPane)
				.addGap(20)
				.addComponent(buttonsPanel)
		);
		// end of Layout Code

		//disable the file loading if there are ontologies loaded
/*		if(Core.getInstance().sourceIsLoaded()){
			filePaths[0].setText(Core.getInstance().getSourceOntology().getFilename());
			filePaths[0].setEnabled(false);
			browseButtons[0].setEnabled(false);
			ontLang[0].setEnabled(false);
			ontSyntax[0].setEnabled(false);
			inMem[0].setEnabled(false);
			onDisk[0].setEnabled(false);
			skip[0].setEnabled(false);
		}
		if(Core.getInstance().targetIsLoaded()){
			filePaths[1].setText(Core.getInstance().getTargetOntology().getFilename());
			filePaths[1].setEnabled(false);
			browseButtons[1].setEnabled(false);
			ontLang[1].setEnabled(false);
			ontSyntax[1].setEnabled(false);
			inMem[1].setEnabled(false);
			onDisk[1].setEnabled(false);
			skip[1].setEnabled(false);
		}
*/
		this.pack(); // automatically set the frame size
		this.setLocationRelativeTo(null); 	// center the window on the screen
		this.setResizable(true);

		this.getRootPane().setDefaultButton(btnProceed);  // make the default button work
		this.setModal(true);
		this.pack();

		this.setVisible(true);
	}

	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		Action actionListener = new AbstractAction() {
			private static final long serialVersionUID = 1774539460694983567L;
			public void actionPerformed(ActionEvent actionEvent) {
				cancel.doClick();
			}
		};
		InputMap inputMap = rootPane
		.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);

		return rootPane;
	}

	/** 
	 * @return A JPanel with "Proceed" and "Cancel" buttons.
	 */
	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();

		GroupLayout buttonsPanelLayout=new GroupLayout(buttonsPanel);
		buttonsPanel.setLayout(buttonsPanelLayout);

		buttonsPanelLayout.setAutoCreateGaps(true);
		buttonsPanelLayout.setAutoCreateContainerGaps(false);

		buttonsPanelLayout.setHorizontalGroup(
				buttonsPanelLayout.createSequentialGroup()
				//.addComponent(btnOnDiskSettings)
				.addComponent(cancel)
				.addComponent(btnProceed)
		);

		buttonsPanelLayout.setVerticalGroup(
				buttonsPanelLayout.createParallelGroup()
				//.addComponent(btnOnDiskSettings)
				.addComponent(cancel)
				.addComponent(btnProceed)
		);

		return buttonsPanel;
	}

	@Override
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();

		if(obj == cancel) {
			this.dispose();
		}
		
		else if(obj == btnProceed){
			
			
			OntologyDefinition sourceDefinition = sourceODP.getDefinition();
			sourceDefinition.sourceOrTarget = GlobalStaticVariables.SOURCENODE;
			OntologyDefinition targetDefinition = targetODP.getDefinition();
			targetDefinition.sourceOrTarget = GlobalStaticVariables.TARGETNODE;
			
			String sourceFilename = sourceDefinition.ontologyURI;
			String targetFilename = targetDefinition.ontologyURI;

			try{
				ui.openFile(sourceDefinition);
			}catch(Exception ex){
				JOptionPane.showConfirmDialog(this,"Can not parse the file '" + sourceFilename + "'. Please check the policy.\n\n"+ex.getMessage(),"Parser Error",JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}

			//frame.dispose();

			try{
				ui.openFile(targetDefinition);
			}catch(Exception ex){
				JOptionPane.showConfirmDialog(this,"Can not parse the file '" + targetFilename + "'. Please check the policy.\n\n"+ex.getMessage(),"Parser Error",JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}

			this.dispose();	

		}// end of obj == proceed
	}

	/**
	 * Helper class to organized the source and target ontology loading panels.
	 * 
	 * @author Cosmin
	 *
	 */
	private static class OntologyDefinitionPanel implements ActionListener {
			
		private JLabel[] labels; 
		private JComboBox[] comboboxes;
		private JCheckBox[] checkboxes;
		private JRadioButton[] radiobuttons;
		private JTextField[] textfields;
		private JButton[] buttons;
		private JSeparator[] separators;
		
		// Endpoint strings.		
		private static final String ENDPOINT_SPARQL = "SPARQL";
		private static final String ENDPOINT_FREEBASE = "FreeBase";
		private final String[] endpointStrings = { ENDPOINT_SPARQL, ENDPOINT_FREEBASE };
		
		private static final String DATASET_RDF = "RDF";
		private final String[] datasetStrings = { DATASET_RDF };
		
		private static final String MAPPING_OAEI = "OAEI Alignment";
		private final String[] alignmentStrings = { MAPPING_OAEI };
		
		public OntologyDefinitionPanel() {
			
			// Intialize the separators.
			
			separators = new JSeparator[2]; 
			
			separators[0] = new JSeparator(SwingConstants.HORIZONTAL);
			separators[1] = new JSeparator(SwingConstants.HORIZONTAL);
			
			// Initialize the labels.
			
			labels = new JLabel[11];
			
			labels[0] = new JLabel("Ontology File/URL:");
			labels[1] = new JLabel("Language:");
			labels[2] = new JLabel("Syntax:");
			labels[3] = new JLabel("Storage:");
			labels[4] = new JLabel("On Disk Directory:");
			labels[5] = new JLabel("Instances source:");
			labels[6] = new JLabel("File/URL:");
			labels[7] = new JLabel("File format:");
			labels[8] = new JLabel("Endpoint Type:");
			labels[9] = new JLabel("File/URL:");
			labels[10] = new JLabel("Alignment Format:");
			
			// Initialize the combo boxes.
			
			comboboxes = new JComboBox[5];
			
			comboboxes[0] = new JComboBox(GlobalStaticVariables.languageStrings);
			comboboxes[1] = new JComboBox(GlobalStaticVariables.syntaxStrings);
			comboboxes[2] = new JComboBox(datasetStrings);
			comboboxes[3] = new JComboBox(endpointStrings);
			comboboxes[4] = new JComboBox(alignmentStrings);
			
			// Initialize the checkboxes
			
			checkboxes = new JCheckBox[4];
			
			checkboxes[0] = new JCheckBox("Persistent");
			checkboxes[1] = new JCheckBox("Load Instances");
			checkboxes[2] = new JCheckBox("Load predefined types alignment (for instance sources without schemas)");
			checkboxes[3] = new JCheckBox("Load ontology");
			
			// Initialize the radio buttons.
			
			radiobuttons = new JRadioButton[5];
			
			radiobuttons[0] = new JRadioButton("In Memory");
			radiobuttons[1] = new JRadioButton("On Disk");
			radiobuttons[2] = new JRadioButton("Ontology");
			radiobuttons[3] = new JRadioButton("Separate File");
			radiobuttons[4] = new JRadioButton("Semantic Web Endpoint");
			
			// Initialize the text fields.
			
			textfields = new JTextField[4];
			
			textfields[0] = new JTextField(); // Ontology File/URL
			textfields[1] = new JTextField(); // On Disk Directory
			textfields[2] = new JTextField(); // File/URL (for instances)
			textfields[3] = new JTextField(); // File/URL (for alignment)
			
			// Initialize buttons.
			
			buttons = new JButton[4];
			
			File folderIcon = new File( System.getProperty("user.dir") + File.separator + 
					"images" + File.separator + "folder-14.png" );
			if( folderIcon.exists() ) {			
				buttons[0] = new JButton(new ImageIcon(folderIcon.getAbsolutePath())); // Ontology File/URL
				buttons[1] = new JButton(new ImageIcon(folderIcon.getAbsolutePath())); // On Disk Directory
				buttons[2] = new JButton(new ImageIcon(folderIcon.getAbsolutePath())); // File/URL (for instances)
				buttons[3] = new JButton(new ImageIcon(folderIcon.getAbsolutePath())); // File/URL (for alignment)
			} else {
				buttons[0] = new JButton("..."); // Ontology File/URL
				buttons[1] = new JButton("..."); // On Disk Directory
				buttons[2] = new JButton("..."); // File/URL (for instances)
				buttons[3] = new JButton("..."); // File/URL (for alignment)
			}
			
			buttons[0].setToolTipText("Browse for file");
			buttons[1].setToolTipText("Browse for file");
			buttons[2].setToolTipText("Browse for file");
			buttons[3].setToolTipText("Browse for file");
			
			
			// *************************************************************
			
			// Setup listeners/groups/etc...
			
			ButtonGroup group1 = new ButtonGroup();
			group1.add(radiobuttons[0]);
			group1.add(radiobuttons[1]);
			radiobuttons[0].setSelected(true);
			
			ButtonGroup group2 = new ButtonGroup();
			group2.add(radiobuttons[2]);
			group2.add(radiobuttons[3]);
			group2.add(radiobuttons[4]);
			radiobuttons[2].setSelected(true);
			
			
			// Action Listeners
			
			radiobuttons[0].addActionListener(this);
			radiobuttons[1].addActionListener(this);
			radiobuttons[2].addActionListener(this);
			radiobuttons[3].addActionListener(this);
			radiobuttons[4].addActionListener(this);
			
			buttons[0].addActionListener(this);
			buttons[1].addActionListener(this);
			buttons[2].addActionListener(this);
			buttons[3].addActionListener(this);
			
			checkboxes[1].addActionListener(this);
			checkboxes[2].addActionListener(this);
			checkboxes[3].addActionListener(this);
			
			// Initial state
			
			checkboxes[3].setSelected(true); // Load ontology from File/URL:
			
			checkboxes[0].setEnabled(false); // [] Persistent
			labels[4].setEnabled(false); // On Disk Directory:
			textfields[1].setEnabled(false); // [______]
			buttons[1].setEnabled(false); // [...]
			
			labels[5].setEnabled(false); // Instance source:
			radiobuttons[2].setEnabled(false); // Ontology
			radiobuttons[3].setEnabled(false); // Separate File
			radiobuttons[4].setEnabled(false); // Semantic Web Endpoint
			labels[6].setEnabled(false); // File/URL:
			textfields[2].setEnabled(false); // [___________]
			buttons[2].setEnabled(false); // [...]
			labels[7].setEnabled(false); // File format:
			comboboxes[2].setEnabled(false); // [_____\/]
			labels[8].setEnabled(false); // Endpoint Type:
			comboboxes[3].setEnabled(false); // [_____\/]
			
			checkboxes[2].setEnabled(false);  // Load predefined types alignment
			labels[9].setEnabled(false); // File/URL: (predefined alignment)
			textfields[3].setEditable(false); // [_____________]
			buttons[3].setEnabled(false); // [...]
			labels[10].setEnabled(false); // Format:
			comboboxes[4].setEnabled(false); // OAEI Alignment

			comboboxes[0].setSelectedIndex(1);
			
		}
		
		public GroupLayout createLayout( Container panel ) {
			GroupLayout lay = new GroupLayout(panel);
			
			lay.setAutoCreateGaps(true);
			lay.setAutoCreateContainerGaps(true);
			
			/* ******************* HORIZONTAL LAYOUT ********************* */
			
			lay.setHorizontalGroup( lay.createParallelGroup()
				
				.addComponent(checkboxes[3])
					
				// [] Ontology File/URL: [___________________________] [...]
				.addGroup( lay.createSequentialGroup()
						.addComponent(labels[0])
						.addComponent(textfields[0])
						.addComponent(buttons[0])
				)
				
				// Language: [__________\/]  Syntax: [_________\/]
				.addGroup( lay.createSequentialGroup()
						.addComponent(labels[1])
						.addComponent(comboboxes[0])
						.addComponent(labels[2])
						.addComponent(comboboxes[1])
				)
				
				// ---------------------------------------------------
				.addComponent( separators[0] )
					
				// Storage: () In Memory  () On Disk  [] Persistent
				.addGroup( lay.createSequentialGroup()
						.addComponent(labels[3])
						.addComponent(radiobuttons[0])
						.addComponent(radiobuttons[1])
						.addComponent(checkboxes[0])
				)
				
				// On Disk Directory: [___________________________] [...]
				.addGroup( lay.createSequentialGroup()
						.addComponent(labels[4])
						.addComponent(textfields[1])
						.addComponent(buttons[1])
				)
				
				//-----------------------------------------------------
				.addComponent( separators[1] )
				
				// [] Load Instances
				.addGroup( lay.createSequentialGroup()
						.addComponent(checkboxes[1])
				)
				
				// Instance source: () Ontology  () Separate File  () Semantic Web Endpoint
				.addGroup( lay.createSequentialGroup()
						.addComponent(labels[5])
						.addComponent(radiobuttons[2])
						.addComponent(radiobuttons[3])
						.addComponent(radiobuttons[4])
				)
				
				// File/URL: [____________________________________] [...]
				.addGroup( lay.createSequentialGroup()
						.addComponent(labels[6])
						.addComponent(textfields[2])
						.addComponent(buttons[2])
				)
				
				// File Format: [_______\/]  Endpoint Type: [_________\/]
				.addGroup( lay.createSequentialGroup()
						.addComponent(labels[7])
						.addComponent(comboboxes[2])
						.addComponent(labels[8])
						.addComponent(comboboxes[3])
				)
				
				// [] Load predefined types alignment
				.addComponent(checkboxes[2])
				
				// File/URL: [_________________________] [...]
				.addGroup( lay.createSequentialGroup() 
						.addComponent(labels[9])
						.addComponent(textfields[3])
						.addComponent(buttons[3])
				)
				
				// Format: [________\/]
				.addGroup( lay.createSequentialGroup() 
						.addComponent(labels[10])
						.addComponent(comboboxes[4])
				)
				
			);
			
			
			/* ******************* VERTICAL LAYOUT ********************* */
			
			lay.setVerticalGroup( lay.createSequentialGroup()
				
				.addComponent(checkboxes[3])
					
				// Ontology File/URL: [___________________________] [...]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addComponent(labels[0])
						.addComponent(textfields[0])
						.addComponent(buttons[0])
				)
				
				// Language: [__________\/]  Syntax: [_________\/]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addComponent(labels[1])
						.addComponent(comboboxes[0])
						.addComponent(labels[2])
						.addComponent(comboboxes[1])
				)
				
				// ---------------------------------------------------
				.addGap(15)
				.addComponent( separators[0] )
				.addGap(10)
				
				// Storage: () In Memory  () On Disk  [] Persistent
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addComponent(labels[3])
						.addComponent(radiobuttons[0])
						.addComponent(radiobuttons[1])
						.addComponent(checkboxes[0])
				)
				
				// On Disk Directory: [___________________________] [...]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addComponent(labels[4])
						.addComponent(textfields[1])
						.addComponent(buttons[1])
				)
				
				//-----------------------------------------------------
				.addGap(15)
				.addComponent( separators[1] )
				.addGap(10)
				
				// [] Load Instances
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addComponent(checkboxes[1])
				)
				
				// Instance source: () Ontology  () Separate File  () Semantic Web Endpoint
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addComponent(labels[5])
						.addComponent(radiobuttons[2])
						.addComponent(radiobuttons[3])
						.addComponent(radiobuttons[4])
				)
				
				// File/URL: [____________________________________] [...]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addComponent(labels[6])
						.addComponent(textfields[2])
						.addComponent(buttons[2])
				)
				
				// File Format: [_______\/]  Endpoint Type: [_________\/]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false)
						.addComponent(labels[7])
						.addComponent(comboboxes[2])
						.addComponent(labels[8])
						.addComponent(comboboxes[3])
				)
				
				.addGap(15)
				
				// [] Load predefined types alignment
				.addComponent(checkboxes[2])
				
				// File/URL: [_________________________] [...]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false) 
						.addComponent(labels[9])
						.addComponent(textfields[3])
						.addComponent(buttons[3])
				)
				
				// Format: [________\/]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER, false) 
						.addComponent(labels[10])
						.addComponent(comboboxes[4])
				)
				
			);
			
			
			return lay;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
				
			if( e.getSource() == buttons[0] ) { chooseFile( textfields[0] ); }
			if( e.getSource() == buttons[1] ) { chooseFile( textfields[1] ); }
			if( e.getSource() == buttons[2] ) { chooseFile( textfields[2] ); }
			if( e.getSource() == buttons[3] ) {	chooseFile( textfields[3] ); }
			
			if( e.getSource() == checkboxes[3] ) { // load ontology
				if( checkboxes[3].isSelected() ) {
					labels[0].setEnabled(true); // Ontology File/URL:
					textfields[0].setEnabled(true); // [___________________]
					buttons[0].setEnabled(true); // [...]
					labels[1].setEnabled(true); // Language:
					comboboxes[0].setEnabled(true); // [_______\/]
					labels[2].setEnabled(true); // Syntax:
					comboboxes[1].setEnabled(true); // [________\/]
					
					labels[3].setEnabled(true); // Storage:
					radiobuttons[0].setEnabled(true); // In Memory
					radiobuttons[0].setSelected(true); // In Memory
					radiobuttons[1].setEnabled(true); // On Disk
					checkboxes[0].setEnabled(false); // [] Persistent
					labels[4].setEnabled(false); // On Disk Directory:
					textfields[1].setEnabled(false); // [______]
					buttons[1].setEnabled(false); // [...]
					
					if( checkboxes[1].isSelected() && !radiobuttons[2].isEnabled() ) {
						radiobuttons[2].setEnabled(true);
					}
				}
				else {
					labels[0].setEnabled(false); // Ontology File/URL:
					textfields[0].setEnabled(false); // [___________________]
					buttons[0].setEnabled(false); // [...]
					labels[1].setEnabled(false); // Language:
					comboboxes[0].setEnabled(false); // [_______\/]
					labels[2].setEnabled(false); // Syntax:
					comboboxes[1].setEnabled(false); // [________\/]
					
					labels[3].setEnabled(false); // Storage:
					radiobuttons[0].setEnabled(false); // In Memory
					radiobuttons[1].setEnabled(false); // On Disk
					checkboxes[0].setEnabled(false); // [] Persistent
					labels[4].setEnabled(false); // On Disk Directory:
					textfields[1].setEnabled(false); // [______]
					buttons[1].setEnabled(false); // [...]
					
					if( checkboxes[1].isSelected() && radiobuttons[2].isSelected() ) {  // () Ontology
						radiobuttons[3].setSelected(true);  // () Separate file.
						
						labels[6].setEnabled(true); // File/URL:
						textfields[2].setEnabled(true); // [___________]
						buttons[2].setEnabled(true); // [...]
						labels[7].setEnabled(true); // File format:
						comboboxes[2].setEnabled(true); // [_____\/]
						labels[8].setEnabled(false); // Endpoint Type:
						comboboxes[3].setEnabled(false); // [_____\/]

						
						radiobuttons[2].setEnabled(false);
					}
					else if( checkboxes[1].isSelected() ) {
						radiobuttons[2].setEnabled(false);
					}
				}
			}
			
			if( e.getSource() == radiobuttons[0] ||
				e.getSource() == radiobuttons[1] ) {
				
				boolean toggle;
				if( radiobuttons[0].isSelected() ) { toggle = false; } 
				else { toggle = true; }
				
				checkboxes[0].setEnabled(toggle); // [] Persistent
				labels[4].setEnabled(toggle); // On Disk Directory:
				textfields[1].setEnabled(toggle); // [______]
				buttons[1].setEnabled(toggle); // [...]
			}
			
			if( e.getSource() == checkboxes[1] ) { // [] Load instances
				
				if( checkboxes[1].isSelected() ) {					
					labels[5].setEnabled(true); // Instance source:
					if( !checkboxes[3].isSelected() ) { // Load ontology
						radiobuttons[2].setEnabled(false); // Ontology
						radiobuttons[3].setSelected(true); // Separate file
						
						labels[6].setEnabled(true); // File/URL:
						textfields[2].setEnabled(true); // [___________]
						buttons[2].setEnabled(true); // [...]
						labels[7].setEnabled(true); // File format:
						comboboxes[2].setEnabled(true); // [_____\/]
						labels[8].setEnabled(false); // Endpoint Type:
						comboboxes[3].setEnabled(false); // [_____\/]

					}
					else {
						radiobuttons[2].setEnabled(true); // Ontology
					}
					radiobuttons[3].setEnabled(true); // Separate File
					radiobuttons[4].setEnabled(true); // Semantic Web Endpoint
					
					checkboxes[2].setEnabled(true);  // Load predefined types alignment
				}
				else {
					radiobuttons[2].setSelected(true);
					checkboxes[2].setSelected(false);
					labels[5].setEnabled(false); // Instance source:
					radiobuttons[2].setEnabled(false); // Ontology
					radiobuttons[3].setEnabled(false); // Separate File
					radiobuttons[4].setEnabled(false); // Semantic Web Endpoint
					labels[6].setEnabled(false); // File/URL:
					textfields[2].setEnabled(false); // [___________]
					buttons[2].setEnabled(false); // [...]
					labels[7].setEnabled(false); // File format:
					comboboxes[2].setEnabled(false); // [_____\/]
					labels[8].setEnabled(false); // Endpoint Type:
					comboboxes[3].setEnabled(false); // [_____\/]
					
					checkboxes[2].setEnabled(false);  // Load predefined types alignment
					labels[9].setEnabled(false); // File/URL: (predefined alignment)
					textfields[3].setEditable(false); // [_____________]
					buttons[3].setEnabled(false); // [...]
					labels[10].setEnabled(false); // Format:
					comboboxes[4].setEnabled(false); // OAEI Alignment
				}
				
				
			}
			
			if( checkboxes[1].isSelected() ) {  // Load Instances is Selected?
				if( e.getSource() == radiobuttons[2] ) { // Instance source: Ontology
					labels[6].setEnabled(false); // File/URL:
					textfields[2].setEnabled(false); // [___________]
					buttons[2].setEnabled(false); // [...]
					labels[7].setEnabled(false); // File format:
					comboboxes[2].setEnabled(false); // [_____\/]
					labels[8].setEnabled(false); // Endpoint Type:
					comboboxes[3].setEnabled(false); // [_____\/]
				}
				else if( e.getSource() == radiobuttons[3] ) {
					labels[6].setEnabled(true); // File/URL:
					textfields[2].setEnabled(true); // [___________]
					buttons[2].setEnabled(true); // [...]
					labels[7].setEnabled(true); // File format:
					comboboxes[2].setEnabled(true); // [_____\/]
					labels[8].setEnabled(false); // Endpoint Type:
					comboboxes[3].setEnabled(false); // [_____\/]
				}
				else if( e.getSource() == radiobuttons[4] ) {
					labels[6].setEnabled(true); // File/URL:
					textfields[2].setEnabled(true); // [___________]
					buttons[2].setEnabled(false); // [...]
					labels[7].setEnabled(false); // File format:
					comboboxes[2].setEnabled(false); // [_____\/]
					labels[8].setEnabled(true); // Endpoint Type:
					comboboxes[3].setEnabled(true); // [_____\/]
				}
				
				if( e.getSource() == checkboxes[2] ) { // [] Load predefined types alignment
					if( checkboxes[2].isSelected() ) {
						labels[9].setEnabled(true); // File/URL: (predefined alignment)
						textfields[3].setEditable(true); // [_____________]
						buttons[3].setEnabled(true); // [...]
						labels[10].setEnabled(true); // Format:
						comboboxes[4].setEnabled(true); // OAEI Alignment
					} else {
						labels[9].setEnabled(false); // File/URL: (predefined alignment)
						textfields[3].setEditable(false); // [_____________]
						buttons[3].setEnabled(false); // [...]
						labels[10].setEnabled(false); // Format:
						comboboxes[4].setEnabled(false); // OAEI Alignment
					}
				}
			}
			
		}
		
		/**
		 * This method gets called when a user clicks the browse button to choose a file.
		 * @param sourceOrTarget 0 = source file, 1 = target file.
		 */
		private void chooseFile( JTextField textField ) {
			Preferences localPrefs = Preferences.userNodeForPackage(this.getClass());
			
			File lastSelectedFile = new File( localPrefs.get(PREF_LASTFILE , "."));
			int lastSelectedFilter = localPrefs.getInt(PREF_LASTFILTER, -1);
			AMFileChooser fc = new AMFileChooser(lastSelectedFile, lastSelectedFilter);

			int retVal = fc.showOpenDialog(textField);
			
			if( retVal == JFileChooser.APPROVE_OPTION ) {
				File selectedFile = fc.getSelectedFile();

				// ok, now that we know what file the user selected
				// let's save it for future use (for the chooser)
				localPrefs.put(PREF_LASTFILE, selectedFile.getAbsolutePath());
				localPrefs.putInt(PREF_LASTFILTER, fc.getFileFilterIndex());
				textField.setText(selectedFile.getPath());
			}
		}
		
		public OntologyDefinition getDefinition() {
			
			OntologyDefinition def = new OntologyDefinition();
			
			def.loadOntology = checkboxes[3].isSelected();
			def.ontologyURI = textfields[0].getText();
			def.ontologyLanguage = comboboxes[0].getSelectedIndex();
			def.ontologySyntax = comboboxes[1].getSelectedIndex();
			
			if( radiobuttons[1].isSelected() ) {
				// on disk
				def.onDiskStorage = true;
				def.onDiskDirectory = textfields[1].getText();
				def.onDiskPersistent = checkboxes[0].isSelected();
			}
			
			// load instances
			if( checkboxes[1].isSelected() ) {
				def.loadInstances = true;
				
				if( radiobuttons[2].isSelected() ) def.instanceSource = DatasetType.ONTOLOGY;
				if( radiobuttons[3].isSelected() ) def.instanceSource = DatasetType.DATASET;
				if( radiobuttons[4].isSelected() ) def.instanceSource = DatasetType.ENDPOINT;
				
				if( def.instanceSource == DatasetType.DATASET ) {
					def.instanceSourceFormat = comboboxes[2].getSelectedIndex();
					def.instanceSourceFile = textfields[2].getText();
				}
				else if( def.instanceSource == DatasetType.ENDPOINT ) {
					if( comboboxes[3].getSelectedIndex() == 0 ) def.instanceEndpointType = EndpointRegistry.SPARQL;
					if( comboboxes[3].getSelectedIndex() == 1 ) def.instanceEndpointType = EndpointRegistry.FREEBASE;
				}
				
				// load predefined alignment
				if( checkboxes[2].isSelected() ) {
					def.loadSchemaAlignment = true;
					def.schemaAlignmentURI = textfields[3].getText();
					def.schemaAlignmentFormat = comboboxes[4].getSelectedIndex();
				}
			}
			
			return def;
		}
		
	}
	
	// This main just tests out how the OntologyDefinitionPanel looks.
	public static void main(String[] args) {
		OntologyDefinitionPanel odp = new OntologyDefinitionPanel();
		
		JDialog jd = new JDialog();
		
		GroupLayout l = odp.createLayout(jd.getContentPane());
		jd.getContentPane().setLayout(l);
		
		jd.addWindowListener(new WindowListener() {
			@Override public void windowOpened(WindowEvent e) {}
			@Override public void windowIconified(WindowEvent e) {}
			@Override public void windowDeiconified(WindowEvent e) {}
			@Override public void windowDeactivated(WindowEvent e) {}
			@Override public void windowClosing(WindowEvent e) {}
			@Override public void windowClosed(WindowEvent e) { System.exit(0); } // <--- exit on close
			@Override public void windowActivated(WindowEvent e) {}
		});
		
		jd.pack();
		jd.setLocationRelativeTo(null);
		jd.setVisible(true);
	}
}
