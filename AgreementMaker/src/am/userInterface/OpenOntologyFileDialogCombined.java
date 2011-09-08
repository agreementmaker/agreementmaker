package am.userInterface;

import java.awt.Container;
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
import javax.swing.event.ListSelectionListener;

import am.GlobalStaticVariables;
import am.app.Core;
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

	private JLabel[] fileBrowseLabel;
	private JTextField[] filePaths;
	private JButton[] browseButtons;
	private JLabel[] sourceTargetLabel;
	private JLabel[] otherLabels;
	private JComboBox[] ontLang;
	private JComboBox[] ontSyntax;
	private JRadioButton[] inMem;
	private JRadioButton[] onDisk;
	private JCheckBox[] skip;
	private JCheckBox fileBrowseSourceInstances, fileBrowseTargetInstances;

	private JButton btnOnDiskSettings, cancel, btnProceed;	
	private UI ui;
	private JPanel sourcePanel, targetPanel, filePanel, buttonsPanel, labelsPanel;

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
		cancel=new JButton("Cancel");
		btnProceed=new JButton("Proceed");

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


	/*private void initializeComponents() {
		//init all the components.  Array index [0] refers to source while [1] refers to target.
		fileBrowseLabel = new JLabel[2];
		fileBrowseLabel[0]= new JLabel("Source File:");
		fileBrowseLabel[1]= new JLabel("Target File:");
		
		fileBrowseSourceInstances = new JCheckBox("Source Instances:");
		fileBrowseTargetInstances = new JCheckBox("Target Instances:");
		

		filePaths=new JTextField[2];
		filePaths[0]=new JTextField(0);
		filePaths[1]=new JTextField(0);
		filePaths[0].setPreferredSize(new Dimension(300, filePaths[0].getHeight()));
		filePaths[1].setPreferredSize(new Dimension(300, filePaths[1].getHeight()));

		browseButtons=new JButton[2];
		browseButtons[0]=new JButton("...");
		browseButtons[1]=new JButton("...");

		sourceTargetLabel=new JLabel[2];
		sourceTargetLabel[0]=new JLabel("Source");
		sourceTargetLabel[1]=new JLabel("Target");

		otherLabels=new JLabel[3];
		otherLabels[0]=new JLabel("Language");
		otherLabels[1]=new JLabel("Syntax");
		otherLabels[2]=new JLabel("Storage");

		ontLang=new JComboBox[2];
		ontLang[0]=new JComboBox(GlobalStaticVariables.languageStrings);
		ontLang[1]=new JComboBox(GlobalStaticVariables.languageStrings);
		ontLang[0].addComponentListener(null);
		ontLang[1].setPreferredSize(ontLang[1].getPreferredSize());

		ontSyntax=new JComboBox[2];
		ontSyntax[0]=new JComboBox(GlobalStaticVariables.syntaxStrings);
		ontSyntax[1]=new JComboBox(GlobalStaticVariables.syntaxStrings);
		ontSyntax[0].setPreferredSize(ontSyntax[0].getPreferredSize());
		ontSyntax[1].setPreferredSize(ontSyntax[1].getPreferredSize());

		inMem=new JRadioButton[2];
		inMem[0]=new JRadioButton("In Memory");
		inMem[0].setSelected(true);
		inMem[1]=new JRadioButton("In Memory");
		inMem[1].setSelected(true);

		onDisk=new JRadioButton[2];
		onDisk[0]=new JRadioButton("On Disk");
		onDisk[1]=new JRadioButton("On Disk");


		onDisk[0].addActionListener(this);
		onDisk[1].addActionListener(this);
		inMem[0].addActionListener(this);
		inMem[1].addActionListener(this);

		ButtonGroup source=new ButtonGroup();
		source.add(inMem[0]);
		source.add(onDisk[0]);

		ButtonGroup target=new ButtonGroup();
		target.add(inMem[1]);
		target.add(onDisk[1]);

		skip=new JCheckBox[2];
		skip[0]=new JCheckBox("<html>Skip concepts with<p>different namespace.</html>");
		skip[1]=new JCheckBox("<html>Skip concepts with<p>different namespace.</html>");

		btnOnDiskSettings=new JButton("On Disk Settings");
		btnOnDiskSettings.setEnabled(false);
		cancel=new JButton("Cancel");
		btnProceed=new JButton("Proceed");


		browseButtons[0].addActionListener(this);
		browseButtons[1].addActionListener(this);
		btnProceed.addActionListener(this);
		cancel.addActionListener(this);
		btnOnDiskSettings.addActionListener(this);
	}*/

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

	/*private JPanel createLabelsPanel() {
		JPanel labelsPanel = new JPanel();

		GroupLayout labelsPanellayout = new GroupLayout(labelsPanel);
		labelsPanel.setLayout(labelsPanellayout);

		labelsPanellayout.setAutoCreateGaps(true);
		labelsPanellayout.setAutoCreateContainerGaps(false);

		labelsPanellayout.setHorizontalGroup(
				labelsPanellayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(otherLabels[0])
				.addComponent(otherLabels[1])
				.addComponent(otherLabels[2])
				//.addComponent(otherLabels[3])
				//.addComponent(otherLabels[4])
		);

		labelsPanellayout.setVerticalGroup(
				labelsPanellayout.createSequentialGroup()
				.addGap(35)
				.addComponent(otherLabels[0])
				.addGap(15)
				.addComponent(otherLabels[1])
				.addGap(10)
				.addComponent(otherLabels[2])
				//.addGap(30)
				//.addComponent(otherLabels[3])
				//.addComponent(otherLabels[4])
		);

		return labelsPanel;
	}*/

	/*private JPanel createTargetPanel() {
		JPanel targetPanel = new JPanel();
		GroupLayout targetPanellayout = new GroupLayout(targetPanel);
		targetPanel.setLayout(targetPanellayout);

		targetPanellayout.setAutoCreateGaps(true);
		targetPanellayout.setAutoCreateContainerGaps(true);

		targetPanellayout.setHorizontalGroup(
				targetPanellayout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
				.addComponent(ontLang[1])
				.addComponent(ontSyntax[1])
				.addComponent(inMem[1])
				.addComponent(onDisk[1])
				.addComponent(skip[1])
		);

		targetPanellayout.setVerticalGroup(
				targetPanellayout.createSequentialGroup()
				.addComponent(ontLang[1], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
				.addComponent(ontSyntax[1], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
				.addComponent(inMem[1])
				.addComponent(onDisk[1])
				.addComponent(skip[1])
		);

		targetPanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Target"));

		return targetPanel;
	}*/

	/*private JPanel createSourcePanel() {
		JPanel sourcePanel = new JPanel();

		GroupLayout sourcePanellayout = new GroupLayout(sourcePanel);
		sourcePanel.setLayout(sourcePanellayout);

		sourcePanellayout.setAutoCreateGaps(true);
		sourcePanellayout.setAutoCreateContainerGaps(true);

		sourcePanellayout.setHorizontalGroup(
				sourcePanellayout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
				.addComponent(ontLang[0])
				.addComponent(ontSyntax[0])
				.addComponent(inMem[0])
				.addComponent(onDisk[0])
				.addComponent(skip[0])
		);

		sourcePanellayout.setVerticalGroup(
				sourcePanellayout.createSequentialGroup()
				.addComponent(ontLang[0], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
				.addComponent(ontSyntax[0], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
				.addComponent(inMem[0])
				.addComponent(onDisk[0])
				.addComponent(skip[0])
		);

		sourcePanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Source"));

		return sourcePanel;
	}*/

	/*private JPanel createFilePanel() {
		JPanel filePanel = new JPanel();

		GroupLayout filePanelLayout=new GroupLayout(filePanel);
		filePanel.setLayout(filePanelLayout);

		filePanelLayout.setAutoCreateGaps(true);
		filePanelLayout.setAutoCreateContainerGaps(false);

		filePanelLayout.setHorizontalGroup(
				filePanelLayout.createParallelGroup()
				.addGroup( filePanelLayout.createSequentialGroup()
						.addComponent(fileBrowseLabel[0])
						.addComponent(filePaths[0], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
						.addComponent(browseButtons[0])
				)
				.addGroup(filePanelLayout.createSequentialGroup()
						.addComponent(fileBrowseLabel[1])
						.addComponent(filePaths[1], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
						.addComponent(browseButtons[1])
				)
		);

		filePanelLayout.setVerticalGroup(
				filePanelLayout.createSequentialGroup()
				.addGroup(filePanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER,false)
						.addComponent(fileBrowseLabel[0], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
						.addComponent(filePaths[0], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
						.addComponent(browseButtons[0])
				)
				.addGroup(filePanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER,false)
						.addComponent(fileBrowseLabel[1], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
						.addComponent(filePaths[1], GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
						.addComponent(browseButtons[1])
				)
		);


		return filePanel;
	}*/

	@Override
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();

		if(obj == cancel) {
			this.dispose();
		}
		/*else if(obj== onDisk[0] || obj==onDisk[1] || obj== inMem[0] || obj==inMem[1]) {
			//setting the database button
			if(onDisk[0].isSelected() || onDisk[1].isSelected()) btnOnDiskSettings.setEnabled(true);
			else btnOnDiskSettings.setEnabled(false);
		} 
		else if(obj==btnOnDiskSettings){
			//open a new dialog that has fields for the database connection settings
			new OnDiskLocationDialog(this,onDisk[0].isSelected(),onDisk[1].isSelected());

		}*/ 
		/*else if(obj == browseButtons[0]) { 
			chooseFile(0); 
		} 
		else if(obj == browseButtons[1]) {
			//browse for target
			chooseFile(1);				
		}*/
		else if(obj == btnProceed){
			
			
			OntologyDefinition sourceDefinition = sourceODP.getDefinition();
			OntologyDefinition targetDefinition = targetODP.getDefinition();
			
			String sourceFilename = sourceDefinition.ontologyURI;
			String targetFilename = targetDefinition.ontologyURI;

			try{
				if(sourceFilename.equals(""))
					JOptionPane.showMessageDialog(this, "No source ontology will be loaded.", "Source Filename is empty"
							, JOptionPane.ERROR_MESSAGE);
				else if(!Core.getInstance().sourceIsLoaded()){
					
					
					
					boolean loadSuccess = ui.openFile(sourceDefinition);
					
					if( loadSuccess ) {
						// once we are done, let's save the syntax and language selection that was made by the user
						// and save the file used to the recent file list, and also what syntax and language it is
					}
				}
			}catch(Exception ex){
				JOptionPane.showConfirmDialog(this,"Can not parse the file '" + sourceFilename + "'. Please check the policy.\n\n"+ex.getMessage(),"Parser Error",JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}

			//frame.dispose();

			try{
				if(targetFilename.equals(""))
					JOptionPane.showMessageDialog(this, "No target ontology will be loaded.", "Target Filename is empty"
							, JOptionPane.ERROR_MESSAGE);
				else if(!Core.getInstance().targetIsLoaded()){
					
					
					boolean loadSuccess = ui.openFile(targetDefinition);
				}
			}catch(Exception ex){
				JOptionPane.showConfirmDialog(this,"Can not parse the file '" + targetFilename + "'. Please check the policy.\n\n"+ex.getMessage(),"Parser Error",JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}

			this.dispose();	

		}// end of obj == proceed
	}

	/**
	 * This method gets called when a user clicks the browse button to choose a file.
	 * @param sourceOrTarget 0 = source file, 1 = target file.
	 */
/*	private void chooseFile(int sourceOrTarget) {
		Preferences localPrefs = Preferences.userNodeForPackage(this.getClass());

		// if the directory we received from our preferences exists, use that as the 
		// starting directory for the chooser			 
		File lastSelectedFile = new File( localPrefs.get(PREF_LASTFILE , "."));
		int lastSelectedFilter = localPrefs.getInt(PREF_LASTFILTER, -1);
		AMFileChooser fc = new AMFileChooser(lastSelectedFile, lastSelectedFilter);

		int retVal = fc.showOpenDialog(this);

		if( retVal == JFileChooser.APPROVE_OPTION ) {
			File selectedFile = fc.getSelectedFile();

			// ok, now that we know what file the user selected
			// let's save it for future use (for the chooser)
			localPrefs.put(PREF_LASTFILE, selectedFile.getAbsolutePath());
			localPrefs.putInt(PREF_LASTFILTER, fc.getFileFilterIndex());
			filePaths[sourceOrTarget].setText(selectedFile.getPath());

			switch( fc.getFileFilterIndex() ) {
			case AMFileChooser.OWL_FILTER:
				for( int i = 0; i < ontLang[sourceOrTarget].getItemCount(); i++ ) {
					if( ontLang[sourceOrTarget].getItemAt(i).equals(GlobalStaticVariables.LANG_OWL) ) {
						ontLang[sourceOrTarget].setSelectedIndex(i);
						break;
					}
				}
				break;

			case AMFileChooser.RDFS_FILTER:
				for( int i = 0; i < ontLang[sourceOrTarget].getItemCount(); i++ ) {
					if( ontLang[sourceOrTarget].getItemAt(i).equals(GlobalStaticVariables.LANG_RDFS) ) {
						ontLang[sourceOrTarget].setSelectedIndex(i);
						break;
					}
				}
				break;

			case AMFileChooser.XML_FILTER:
				for( int i = 0; i < ontLang[sourceOrTarget].getItemCount(); i++ ) {
					if( ontLang[sourceOrTarget].getItemAt(i).equals(GlobalStaticVariables.LANG_XML) ) {
						ontLang[sourceOrTarget].setSelectedIndex(i);
						break;
					}
				}
				break;

			}
		}
	}*/

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
/*	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource() == ontLang[0]){
			if(ontLang[0].getSelectedIndex() == 2)//for XML files selection
				ontSyntax[0].setEnabled(false);
			else
				ontSyntax[0].setEnabled(true);
		}

		if(e.getSource() == ontLang[1]){
			if(ontLang[1].getSelectedIndex() == 2)//for XML files selection
				ontSyntax[1].setEnabled(false);
			else
				ontSyntax[1].setEnabled(true);
		}
	}*/
	//public static void main(String[] args)
	//{
	//	OpenOntologyFileDialogCombined n=new OpenOntologyFileDialogCombined(new UI());
	//}
	
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
			
			checkboxes = new JCheckBox[3];
			
			checkboxes[0] = new JCheckBox("Persistent");
			checkboxes[1] = new JCheckBox("Load Instances");
			checkboxes[2] = new JCheckBox("Load predefined types alignment (for instance sources w/o schemas)");
			
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
			
			buttons[0] = new JButton("..."); // Ontology File/URL
			buttons[1] = new JButton("..."); // On Disk Directory
			buttons[2] = new JButton("..."); // File/URL (for instances)
			buttons[3] = new JButton("..."); // File/URL (for alignment)
			
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
			
			// Initial state
			
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

		}
		
		public GroupLayout createLayout( Container panel ) {
			GroupLayout lay = new GroupLayout(panel);
			
			lay.setAutoCreateGaps(true);
			lay.setAutoCreateContainerGaps(true);
			
			/* ******************* HORIZONTAL LAYOUT ********************* */
			
			lay.setHorizontalGroup( lay.createParallelGroup()
				
				// Ontology File/URL: [___________________________] [...]
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
				
				// Ontology File/URL: [___________________________] [...]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(labels[0])
						.addComponent(textfields[0])
						.addComponent(buttons[0])
				)
				
				// Language: [__________\/]  Syntax: [_________\/]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER)
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
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(labels[3])
						.addComponent(radiobuttons[0])
						.addComponent(radiobuttons[1])
						.addComponent(checkboxes[0])
				)
				
				// On Disk Directory: [___________________________] [...]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(labels[4])
						.addComponent(textfields[1])
						.addComponent(buttons[1])
				)
				
				//-----------------------------------------------------
				.addGap(15)
				.addComponent( separators[1] )
				.addGap(10)
				
				// [] Load Instances
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(checkboxes[1])
				)
				
				// Instance source: () Ontology  () Separate File  () Semantic Web Endpoint
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(labels[5])
						.addComponent(radiobuttons[2])
						.addComponent(radiobuttons[3])
						.addComponent(radiobuttons[4])
				)
				
				// File/URL: [____________________________________] [...]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(labels[6])
						.addComponent(textfields[2])
						.addComponent(buttons[2])
				)
				
				// File Format: [_______\/]  Endpoint Type: [_________\/]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(labels[7])
						.addComponent(comboboxes[2])
						.addComponent(labels[8])
						.addComponent(comboboxes[3])
				)
				
				.addGap(15)
				
				// [] Load predefined types alignment
				.addComponent(checkboxes[2])
				
				// File/URL: [_________________________] [...]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER) 
						.addComponent(labels[9])
						.addComponent(textfields[3])
						.addComponent(buttons[3])
				)
				
				// Format: [________\/]
				.addGroup( lay.createParallelGroup(GroupLayout.Alignment.CENTER) 
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
					radiobuttons[2].setEnabled(true); // Ontology
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
					if( comboboxes[4].getSelectedIndex() == 0 ) def.instanceEndpointType = EndpointRegistry.SPARQL;
					if( comboboxes[4].getSelectedIndex() == 1 ) def.instanceEndpointType = EndpointRegistry.FREEBASE;
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
