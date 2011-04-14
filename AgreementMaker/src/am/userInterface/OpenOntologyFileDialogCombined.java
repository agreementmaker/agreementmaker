package am.userInterface;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.hp.hpl.jena.tdb.index.Index;

import am.GlobalStaticVariables;
import am.app.Core;
import am.utility.AMFileChooser;
/**
 * This class represents the Open Ontologies dialog, combining the 
 * loading of both ontologies into one dialog. 
 *
 */
public class OpenOntologyFileDialogCombined extends JDialog implements ActionListener, ListSelectionListener {

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

	private JButton btnOnDiskSettings, cancel, btnProceed;	
	private UI ui;
	private JPanel sourcePanel, targetPanel, filePanel, buttonsPanel, labelsPanel;

	private static final String PREF_LASTFILTER = "LAST_SELECTED_FILTER";
	private static final String PREF_LASTFILE = "LAST_SELECTED_FILE";

	public OpenOntologyFileDialogCombined(UI userInterface){
		super(userInterface.getUIFrame());
		ui = userInterface;

		//frame = new JDialog(ui.getUIFrame(), true);
		this.setTitle("Open Ontologies");

		initializeComponents();

		//create the layouts for the different panels
		filePanel = createFilePanel();
		sourcePanel = createSourcePanel();
		targetPanel = createTargetPanel();
		labelsPanel = createLabelsPanel();
		buttonsPanel = createButtonsPanel();

		//add the components the the frame
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup( layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(filePanel)
				.addGroup(layout.createSequentialGroup()
						.addComponent(labelsPanel)
						.addComponent(sourcePanel)
						.addComponent(targetPanel)
				)
				.addComponent(buttonsPanel)	
		);

		layout.setVerticalGroup( layout.createSequentialGroup()
				.addComponent(filePanel)
				.addGap(20)
				.addGroup(layout.createParallelGroup()
						.addComponent(labelsPanel)
						.addComponent(sourcePanel)
						.addComponent(targetPanel)
				)
				.addGap(20)
				.addComponent(buttonsPanel)
		);
		// end of Layout Code

		//disable the file loading if there are ontologies loaded
		if(Core.getInstance().sourceIsLoaded()){
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

		this.addWindowListener(ui.new WindowEventHandler());
		this.pack(); // automatically set the frame size
		this.setLocationRelativeTo(null); 	// center the window on the screen
		this.setResizable(false);

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


	private void initializeComponents() {
		//init all the components.  Array index [0] refers to source while [1] refers to target.
		fileBrowseLabel = new JLabel[2];
		fileBrowseLabel[0]= new JLabel("Source File:");
		fileBrowseLabel[1]= new JLabel("Target File:");

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
	}

	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();

		GroupLayout buttonsPanelLayout=new GroupLayout(buttonsPanel);
		buttonsPanel.setLayout(buttonsPanelLayout);

		buttonsPanelLayout.setAutoCreateGaps(true);
		buttonsPanelLayout.setAutoCreateContainerGaps(false);

		buttonsPanelLayout.setHorizontalGroup(
				buttonsPanelLayout.createSequentialGroup()
				.addComponent(btnOnDiskSettings)
				.addComponent(cancel)
				.addComponent(btnProceed)
		);

		buttonsPanelLayout.setVerticalGroup(
				buttonsPanelLayout.createParallelGroup()
				.addComponent(btnOnDiskSettings)
				.addComponent(cancel)
				.addComponent(btnProceed)
		);

		return buttonsPanel;
	}

	private JPanel createLabelsPanel() {
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
	}

	private JPanel createTargetPanel() {
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
	}

	private JPanel createSourcePanel() {
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
	}

	private JPanel createFilePanel() {
		JPanel filePanel = new JPanel();

		GroupLayout filePanelLayout=new GroupLayout(filePanel);
		filePanel.setLayout(filePanelLayout);

		filePanelLayout.setAutoCreateGaps(true);
		filePanelLayout.setAutoCreateContainerGaps(false);

		filePanelLayout.setHorizontalGroup(
				filePanelLayout.createParallelGroup()
				.addGroup(filePanelLayout.createSequentialGroup()
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
		else if(obj== onDisk[0] || obj==onDisk[1] || obj== inMem[0] || obj==inMem[1]) {
			//setting the database button
			if(onDisk[0].isSelected() || onDisk[1].isSelected()) btnOnDiskSettings.setEnabled(true);
			else btnOnDiskSettings.setEnabled(false);
		} 
		else if(obj==btnOnDiskSettings){
			//open a new dialog that has fields for the database connection settings
			new OnDiskLocationDialog(this,onDisk[0].isSelected(),onDisk[1].isSelected());

		} 
		else if(obj == browseButtons[0]) { 
			chooseFile(0); 
		} 
		else if(obj == browseButtons[1]) {
			//browse for target
			chooseFile(1);				
		}
		else if(obj == btnProceed){
			String sourceFilename = filePaths[0].getText();
			String targetFilename = filePaths[1].getText();

			try{
				if(sourceFilename.equals(""))
					JOptionPane.showMessageDialog(this, "No source ontology will be loaded.", "Source Filename is empty"
							, JOptionPane.ERROR_MESSAGE);
				else if(!Core.getInstance().sourceIsLoaded()){
					Preferences onDiskPrefs = Preferences.userNodeForPackage(OnDiskLocationDialog.class);
					String onDiskDirectory = onDiskPrefs.get(OnDiskLocationDialog.TDB_LAST_SOURCE_DIRECTORY, "");
					boolean onDiskPersistent = onDiskPrefs.getBoolean(OnDiskLocationDialog.TDB_LAST_SOURCE_PERSISTENT, false);

					boolean loadSuccess = ui.openFile(sourceFilename, 
								GlobalStaticVariables.SOURCENODE, 
								ontSyntax[0].getSelectedIndex(), 
								ontLang[0].getSelectedIndex(), 
								skip[0].isSelected(), 
								false,
								onDisk[0].isSelected(),
								onDiskDirectory,
								onDiskPersistent);
					if( loadSuccess ) {
						// once we are done, let's save the syntax and language selection that was made by the user
						// and save the file used to the recent file list, and also what syntax and language it is
						Core.getAppPreferences().saveOpenDialogListSelection(ontSyntax[0].getSelectedIndex() , 
																			ontLang[0].getSelectedIndex(), 
																			skip[0].isSelected(),
																			false, // use reasoner
																			onDisk[0].isSelected(), 
																			onDiskDirectory, 
																			onDiskPersistent);
						Core.getAppPreferences().saveRecentFile(filePaths[0].getText(), 
																GlobalStaticVariables.SOURCENODE, 
																ontSyntax[0].getSelectedIndex(), 
																ontLang[0].getSelectedIndex(), 
																skip[0].isSelected(),
																false,
																onDisk[0].isSelected(), 
																onDiskDirectory, 
																onDiskPersistent);
						ui.getUIMenu().refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.
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
					Preferences onDiskPrefs = Preferences.userNodeForPackage(OnDiskLocationDialog.class);
					String onDiskDirectory = onDiskPrefs.get(OnDiskLocationDialog.TDB_LAST_TARGET_DIRECTORY, "");
					boolean onDiskPersistent = onDiskPrefs.getBoolean(OnDiskLocationDialog.TDB_LAST_TARGET_PERSISTENT, false);

					boolean loadSuccess = ui.openFile(targetFilename, 
								GlobalStaticVariables.TARGETNODE, 
								ontSyntax[1].getSelectedIndex(), 
								ontLang[1].getSelectedIndex(), 
								skip[1].isSelected(), 
								false,
								onDisk[1].isSelected(),
								onDiskDirectory,
								onDiskPersistent);
					
					if( loadSuccess ) {
						// once we are done, let's save the syntax and language selection that was made by the user
						// and save the file used to the recent file list, and also what syntax and language it is
						Core.getAppPreferences().saveOpenDialogListSelection(ontSyntax[1].getSelectedIndex() , 
																			ontLang[1].getSelectedIndex(), 
																			skip[1].isSelected(),
																			false, // use reasoner
																			onDisk[1].isSelected(), 
																			onDiskDirectory, 
																			onDiskPersistent);
						Core.getAppPreferences().saveRecentFile(filePaths[1].getText(), 
																GlobalStaticVariables.TARGETNODE, 
																ontSyntax[1].getSelectedIndex(), 
																ontLang[1].getSelectedIndex(), 
																skip[1].isSelected(),
																false,
																onDisk[1].isSelected(), 
																onDiskDirectory, 
																onDiskPersistent);
						ui.getUIMenu().refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.
					}
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
	private void chooseFile(int sourceOrTarget) {
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
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
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
	}
	//public static void main(String[] args)
	//{
	//	OpenOntologyFileDialogCombined n=new OpenOntologyFileDialogCombined(new UI());
	//}
}
