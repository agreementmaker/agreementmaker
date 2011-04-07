package am.userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.userInterface.UI.WindowEventHandler;

public class OpenOntologyFileDialogCombined implements ActionListener, ListSelectionListener{

	private JLabel[] fileBrowseLabel;
	private JTextField[] filePaths;
	private JButton[] browseButtons;
	
	private JLabel[] sourceTargetLabel;
	private JLabel[] otherLabels;
	private JComboBox[] ontLang;
	private JComboBox[] ontSyntax;
	private JRadioButton[] inMem;
	private JRadioButton[] inDB;
	private JCheckBox[] skip;
	
	private JButton DBSettings, cancel, proceed;
	
	private UI ui;
	private JDialog frame;
	
	private JPanel sourcePanel, targetPanel, filePanel, buttonsPanel, labelsPanel;
	
	public OpenOntologyFileDialogCombined(UI userInterface){
		
		ui = userInterface;
		
		AppPreferences prefs = Core.getAppPreferences();
		
		frame = new JDialog(ui.getUIFrame(), true);
		frame.setTitle("Open Ontology Files");
		
		//init all the fields.  [0] refers to source while [1] refers to target
		fileBrowseLabel = new JLabel[2];
		fileBrowseLabel[0]= new JLabel("Source File:");
		fileBrowseLabel[1]= new JLabel("Target File:");
		
		filePaths=new JTextField[2];
		filePaths[0]=new JTextField(0);
		filePaths[1]=new JTextField(0);
		
		browseButtons=new JButton[2];
		browseButtons[0]=new JButton("...");
		browseButtons[1]=new JButton("...");
		
		sourceTargetLabel=new JLabel[2];
		sourceTargetLabel[0]=new JLabel("Source");
		sourceTargetLabel[1]=new JLabel("Target");
		
		otherLabels=new JLabel[5];
		otherLabels[0]=new JLabel("Ontology Language");
		otherLabels[1]=new JLabel("Ontology Syntax");
		otherLabels[2]=new JLabel("Storage");
		otherLabels[3]=new JLabel("Skip concepts with");
		otherLabels[4]=new JLabel("different namespace");
		
		
		String[] languageStrings = GlobalStaticVariables.languageStrings;
		String[] syntaxStrings = GlobalStaticVariables.syntaxStrings;
		
		ontLang=new JComboBox[2];
		ontLang[0]=new JComboBox(languageStrings);
		ontLang[1]=new JComboBox(languageStrings);
		ontLang[0].addComponentListener(null);
		ontLang[1].setPreferredSize(ontLang[1].getPreferredSize());
		
		ontSyntax=new JComboBox[2];
		ontSyntax[0]=new JComboBox(syntaxStrings);
		ontSyntax[1]=new JComboBox(syntaxStrings);
		ontSyntax[0].setPreferredSize(ontSyntax[0].getPreferredSize());
		ontSyntax[1].setPreferredSize(ontSyntax[1].getPreferredSize());
		
		inMem=new JRadioButton[2];
		inMem[0]=new JRadioButton("In Memory");
		inMem[0].setSelected(true);
		inMem[1]=new JRadioButton("In Memory");
		inMem[1].setSelected(true);
		
		inDB=new JRadioButton[2];
		inDB[0]=new JRadioButton("In Database");
		inDB[1]=new JRadioButton("In Database");
		
		
		inDB[0].addActionListener(this);
		inDB[1].addActionListener(this);
		inMem[0].addActionListener(this);
		inMem[1].addActionListener(this);
		
		ButtonGroup source=new ButtonGroup();
		source.add(inMem[0]);
		source.add(inDB[0]);
		
		ButtonGroup target=new ButtonGroup();
		target.add(inMem[1]);
		target.add(inDB[1]);
		
		skip=new JCheckBox[2];
		skip[0]=new JCheckBox();
		skip[1]=new JCheckBox();
		
		DBSettings=new JButton("Database Settings");
		DBSettings.setEnabled(false);
		cancel=new JButton("Cancel");
		proceed=new JButton("Proceed");
		
		
		browseButtons[0].addActionListener(this);
		browseButtons[1].addActionListener(this);
		proceed.addActionListener(this);
		cancel.addActionListener(this);
		DBSettings.addActionListener(this);
		
		//instance the panel for the objects on screen
		filePanel=new JPanel();
		sourcePanel=new JPanel();
		targetPanel=new JPanel();
		sourcePanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Source"));
		targetPanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Target"));
		labelsPanel=new JPanel();
		buttonsPanel=new JPanel();
		
		//create the layouts for the different panels
		//filepanel
		GroupLayout filePanelLayout=new GroupLayout(filePanel);
		filePanel.setLayout(filePanelLayout);
		
		filePanelLayout.setAutoCreateGaps(true);
		filePanelLayout.setAutoCreateContainerGaps(true);
		
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
		
		//source Panel
		GroupLayout sourcePanellayout = new GroupLayout(sourcePanel);
		sourcePanel.setLayout(sourcePanellayout);
		
		sourcePanellayout.setAutoCreateGaps(true);
		sourcePanellayout.setAutoCreateContainerGaps(true);
		
		sourcePanellayout.setHorizontalGroup(
				sourcePanellayout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
					.addComponent(ontLang[0])
					.addComponent(ontSyntax[0])
					.addComponent(inMem[0])
					.addComponent(inDB[0])
					.addComponent(skip[0])
		);
		
		sourcePanellayout.setVerticalGroup(
				sourcePanellayout.createSequentialGroup()
					.addComponent(ontLang[0], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
					.addComponent(ontSyntax[0], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
					.addComponent(inMem[0])
					.addComponent(inDB[0])
					.addComponent(skip[0])
		);
		
		//target Panel
		GroupLayout targetPanellayout = new GroupLayout(targetPanel);
		targetPanel.setLayout(targetPanellayout);
		
		targetPanellayout.setAutoCreateGaps(true);
		targetPanellayout.setAutoCreateContainerGaps(true);
		
		targetPanellayout.setHorizontalGroup(
				targetPanellayout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
					.addComponent(ontLang[1])
					.addComponent(ontSyntax[1])
					.addComponent(inMem[1])
					.addComponent(inDB[1])
					.addComponent(skip[1])
		);
		
		targetPanellayout.setVerticalGroup(
				targetPanellayout.createSequentialGroup()
					.addComponent(ontLang[1], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
					.addComponent(ontSyntax[1], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
					.addComponent(inMem[1])
					.addComponent(inDB[1])
					.addComponent(skip[1])
		);
		
		//labels panel
		GroupLayout labelsPanellayout = new GroupLayout(labelsPanel);
		labelsPanel.setLayout(labelsPanellayout);
		
		labelsPanellayout.setAutoCreateGaps(true);
		labelsPanellayout.setAutoCreateContainerGaps(true);
		
		labelsPanellayout.setHorizontalGroup(
				labelsPanellayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(otherLabels[0])
					.addComponent(otherLabels[1])
					.addComponent(otherLabels[2])
					.addComponent(otherLabels[3])
					.addComponent(otherLabels[4])
		);
		
		labelsPanellayout.setVerticalGroup(
				labelsPanellayout.createSequentialGroup()
					.addGap(35)
					.addComponent(otherLabels[0])
					.addGap(15)
					.addComponent(otherLabels[1])
					.addGap(10)
					.addComponent(otherLabels[2])
					.addGap(30)
					.addComponent(otherLabels[3])
					.addComponent(otherLabels[4])
		);
		
		//buttons panel
		GroupLayout buttonsPanelLayout=new GroupLayout(buttonsPanel);
		buttonsPanel.setLayout(buttonsPanelLayout);
		
		buttonsPanelLayout.setAutoCreateGaps(true);
		buttonsPanelLayout.setAutoCreateContainerGaps(true);
		
		buttonsPanelLayout.setHorizontalGroup(
				buttonsPanelLayout.createSequentialGroup()
					.addComponent(DBSettings)
					.addComponent(cancel)
					.addComponent(proceed)
			);
		
		buttonsPanelLayout.setVerticalGroup(
				buttonsPanelLayout.createParallelGroup()
					.addComponent(DBSettings)
					.addComponent(cancel)
					.addComponent(proceed)
		);
		
		
		//add the componants the the frame
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(filePanel)
					.addGroup(layout.createSequentialGroup()
							.addComponent(labelsPanel)
							.addComponent(sourcePanel)
							.addComponent(targetPanel)
					)
					.addComponent(buttonsPanel)
		);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(filePanel)
					.addGroup(layout.createParallelGroup()
							.addComponent(labelsPanel)
							.addComponent(sourcePanel)
							.addComponent(targetPanel)
					)
					.addGap(30)
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
			inDB[0].setEnabled(false);
			skip[0].setEnabled(false);
		}
		if(Core.getInstance().targetIsLoaded()){
			filePaths[1].setText(Core.getInstance().getTargetOntology().getFilename());
			filePaths[1].setEnabled(false);
			browseButtons[1].setEnabled(false);
			ontLang[1].setEnabled(false);
			ontSyntax[1].setEnabled(false);
			inMem[1].setEnabled(false);
			inDB[1].setEnabled(false);
			skip[1].setEnabled(false);
		}
		
		frame.addWindowListener(ui.new WindowEventHandler());
		frame.pack(); // automatically set the frame size
		frame.setLocationRelativeTo(null); 	// center the window on the screen
		
		frame.setVisible(true);
	}
	
	@Override
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		JFileChooser fc;
		AppPreferences prefs = Core.getAppPreferences(); // Class interface to Application Preferences
		
		if(obj == cancel){
			frame.dispose();
		}else if(obj== inDB[0] || obj==inDB[1] || obj== inMem[0] || obj==inMem[1]){//setting the database button
			if(inDB[0].isSelected() || inDB[1].isSelected())
				DBSettings.setEnabled(true);
			else
				DBSettings.setEnabled(false);
		}else if(obj==DBSettings){
			//open a new dialog that has fields for the database connection settings
			JDialog dSettings=new DatabaseSettingsDialog(frame,inDB[0].isSelected(),inDB[1].isSelected());
			Preferences p=Preferences.userNodeForPackage(DatabaseSettingsDialog.class);
		}else if(obj == browseButtons[0]){//browse for source
			// if the directory we received from our preferences exists, use that as the 
			// starting directory for the chooser
			if( prefs.getLastDir().exists() ) {
				fc = new JFileChooser(prefs.getLastDir());
			} else { fc = new JFileChooser(); } 
			
			int returnVal = fc.showOpenDialog(frame);

			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				File selectedfile = fc.getSelectedFile();
				
				// ok, now that we know what file the user selected
				// let's save it for future use (for the chooser)
				prefs.saveLastDir(selectedfile); 
				filePaths[0].setText(selectedfile.getPath());
				
			}
		}else if(obj == browseButtons[1]){//browse for target
			// if the directory we received from our preferences exists, use that as the 
			// starting directory for the chooser
			if( prefs.getLastDir().exists() ) {
				fc = new JFileChooser(prefs.getLastDir());
			} else { fc = new JFileChooser(); } 
			
			int returnVal = fc.showOpenDialog(frame);

			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				File selectedfile = fc.getSelectedFile();
				
				// ok, now that we know what file the user selected
				// let's save it for future use (for the chooser)
				prefs.saveLastDir(selectedfile); 
				filePaths[1].setText(selectedfile.getPath());
			}
				
		}else if(obj == proceed){
				String sourceFilename = filePaths[0].getText();
				String targetFilename = filePaths[1].getText();

				try{
					if(sourceFilename.equals(""))
						JOptionPane.showMessageDialog(frame, "No source ontology will be loaded.", "Source Filename is empty"
								, JOptionPane.ERROR_MESSAGE);
					else if(!Core.getInstance().sourceIsLoaded()){
						ui.openFile(sourceFilename, GlobalStaticVariables.SOURCENODE, ontSyntax[0].getSelectedIndex(), ontLang[0].getSelectedIndex(), skip[0].isSelected()
								, false,inDB[0].isSelected());
					// once we are done, let's save the syntax and language selection that was made by the user
					// and save the file used to the recent file list, and also what syntax and language it is
					//prefs.saveOpenDialogListSelection(ontSyntax[0].getSelectedIndex() , ontLang[0].getSelectedIndex(), skip[0].isSelected());
					//prefs.saveRecentFile(filePaths[0].getText(), ontoType, ontSyntax[0].getSelectedIndex(), ontLang[0].getSelectedIndex(), skip[0].isSelected());
					ui.getUIMenu().refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.
					}
				}catch(Exception ex){
					JOptionPane.showConfirmDialog(frame,"Can not parse the file '" + sourceFilename + "'. Please check the policy.","Parser Error",JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}

				//frame.dispose();
				
				try{
					if(targetFilename.equals(""))
						JOptionPane.showMessageDialog(frame, "No target ontology will be loaded.", "Target Filename is empty"
								, JOptionPane.ERROR_MESSAGE);
					else if(!Core.getInstance().targetIsLoaded()){
						ui.openFile(targetFilename, GlobalStaticVariables.TARGETNODE, ontSyntax[1].getSelectedIndex(), ontLang[1].getSelectedIndex()
								, skip[1].isSelected(), false,inDB[1].isSelected());
						// once we are done, let's save the syntax and language selection that was made by the user
						// and save the file used to the recent file list, and also what syntax and language it is
						//prefs.saveOpenDialogListSelection(ontSyntax[0].getSelectedIndex() , ontLang[0].getSelectedIndex(), skip[0].isSelected());
						//prefs.saveRecentFile(filePaths[0].getText(), ontoType, ontSyntax[0].getSelectedIndex(), ontLang[0].getSelectedIndex(), skip[0].isSelected());
						ui.getUIMenu().refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.
					}
				}catch(Exception ex){
					JOptionPane.showConfirmDialog(frame,"Can not parse the file '" + targetFilename + "'. Please check the policy.","Parser Error",JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}

				frame.dispose();	
			
		}// end of obj == proceed
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
