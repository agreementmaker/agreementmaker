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
import am.app.Core;


/**
 * @author Nalin
 *
 */
public class OpenOntologyFileDialog implements ActionListener, ListSelectionListener{
	
	/**
	 * @param args
	 */
	
	private JButton browse, cancel, proceed, databaseSettings;
	private JRadioButton memoryRadio, databaseRadio;
	private JComboBox syntaxCombo, langCombo;
	private JLabel syntaxLbl, langLbl;
	private JTextField filePath;
	private JLabel fileType;
	private JDialog frame;
	private int ontoType;
	
	private JPanel filePanel, optionsPanel, cancelProceedPanel, checkboxPanel;
	private Preferences prefs;
	
	//private JList syntaxList, langList;	
	private JCheckBox skipCheck;
	private JLabel skipLabel;
	private JCheckBox noReasonerCheck;
	private JLabel noReasonerLabel;
	private UI ui;
	
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public OpenOntologyFileDialog(int ontoType, UI userInterface) {
		

		//	Setting the Look and Feel of the application to that of Windows
		//try { javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		//catch (Exception e) { System.out.println(e); }
		
		//typeOfFile = fileType;  
		ui = userInterface;
		this.ontoType = ontoType;
		
		AppPreferences prefs = Core.getAppPreferences(); // Class interface to Application Preferences
		
		frame = new JDialog(ui.getUIFrame(), true);
		if(ontoType == GlobalStaticVariables.SOURCENODE)
			frame.setTitle("Open Source Ontology File...");
		else if(ontoType == GlobalStaticVariables.TARGETNODE)
			frame.setTitle("Open Target Ontology File...");
		
		
		//Container contentPane = frame.getContentPane();
		//frame.setResizable(false);

		
		fileType = new JLabel("File:");
		
		filePath = new JTextField(0);
		
		browse = new JButton("Browse...");
		cancel = new JButton("Cancel");
		proceed = new JButton("Proceed");
		databaseSettings=new JButton("Database Settings");
		browse.addActionListener(this);
		proceed.addActionListener(this);
		cancel.addActionListener(this);
		databaseSettings.addActionListener(this);
		databaseSettings.setEnabled(false);

		
		String[] languageStrings = GlobalStaticVariables.languageStrings;
		String[] syntaxStrings = GlobalStaticVariables.syntaxStrings;
		
		syntaxCombo=new JComboBox(syntaxStrings);
		langCombo=new JComboBox(languageStrings);
		
		syntaxLbl=new JLabel("Ontololgy Syntax");
		langLbl=new JLabel("Ontology Language");
		
		memoryRadio=new JRadioButton("In Memory");
		memoryRadio.setSelected(true);
		databaseRadio=new JRadioButton("In Database");
		
		memoryRadio.addActionListener(this);
		databaseRadio.addActionListener(this);
		
		ButtonGroup g=new ButtonGroup();
		g.add(memoryRadio);
		g.add(databaseRadio);
		
		
		skipCheck = new JCheckBox();
		skipCheck.setSelected(prefs.getLastSkipNamespace());
		skipLabel = new JLabel("Skip concepts with different namespace");
		
		noReasonerCheck = new JCheckBox();
		noReasonerCheck.setSelected( prefs.getLastNoReasoner() );
		noReasonerLabel = new JLabel("Do not use checkboxPanel reasoner");
		
		//create the layout for the entire frame
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		//instance the panel for the objects on screen
		optionsPanel=new JPanel();
		filePanel=new JPanel();
		cancelProceedPanel=new JPanel();
		checkboxPanel=new JPanel();
		
		//create the layout for the file choosing label, txt area, and browse button
		GroupLayout filePanelLayout=new GroupLayout(filePanel);
		filePanel.setLayout(filePanelLayout);
		
		filePanelLayout.setAutoCreateGaps(true);
		filePanelLayout.setAutoCreateContainerGaps(true);
		
		filePanelLayout.setHorizontalGroup(
				filePanelLayout.createSequentialGroup()
					.addComponent(fileType)
					.addComponent(filePath, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
					.addComponent(browse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
		);
		filePanelLayout.setVerticalGroup(
				filePanelLayout.createParallelGroup()
					.addComponent(fileType)
					.addComponent(filePath, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
					.addComponent(browse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
		);
		
		//create the layout for the combo boxes, labels, the radio buttons, and database config button
		GroupLayout optionsPanelLayout=new GroupLayout(optionsPanel);
		optionsPanel.setLayout(optionsPanelLayout);
		
		optionsPanelLayout.setAutoCreateGaps(true);
		optionsPanelLayout.setAutoCreateContainerGaps(true);
		
		optionsPanelLayout.setHorizontalGroup(
				optionsPanelLayout.createSequentialGroup()
					.addGroup(optionsPanelLayout.createParallelGroup()
						.addComponent(langLbl)
						.addComponent(syntaxLbl)
					)
					.addGroup(optionsPanelLayout.createParallelGroup()
						.addComponent(langCombo)
						.addComponent(syntaxCombo)
					)
					.addGroup(optionsPanelLayout.createParallelGroup()
						.addComponent(memoryRadio)
						.addComponent(databaseRadio)
						.addComponent(databaseSettings)
					)		
		);
		
		optionsPanelLayout.setVerticalGroup(
				optionsPanelLayout.createParallelGroup()
					.addGroup(optionsPanelLayout.createSequentialGroup()
							.addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
									.addComponent(langLbl)
									.addComponent(langCombo)
							)
							.addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
									.addComponent(syntaxLbl)
									.addComponent(syntaxCombo)
							)
					)
					.addGroup( optionsPanelLayout.createSequentialGroup() 
							.addComponent(memoryRadio)
							.addComponent(databaseRadio)
							.addComponent(databaseSettings)
					)
		);
		
		//layout for the skip checkbox/label and reasoner checkbox/label
		GroupLayout checkPanelLayout=new GroupLayout(checkboxPanel);
		checkboxPanel.setLayout(checkPanelLayout);
		
		checkPanelLayout.setAutoCreateGaps(true);
		checkPanelLayout.setAutoCreateContainerGaps(true);
		
		checkPanelLayout.setHorizontalGroup(
				checkPanelLayout.createSequentialGroup()
					.addGroup(checkPanelLayout.createParallelGroup()
							.addComponent(filePanel)
							.addComponent(optionsPanel)
							.addGroup(checkPanelLayout.createSequentialGroup()
									.addComponent(skipCheck)
									.addComponent(skipLabel)
							)
							.addGroup(checkPanelLayout.createSequentialGroup()
									.addComponent(noReasonerCheck)
									.addComponent(noReasonerLabel)
							)
					)
		);
		
		checkPanelLayout.setVerticalGroup(
				checkPanelLayout.createParallelGroup()
				.addGroup(checkPanelLayout.createSequentialGroup()
						.addComponent(filePanel)
						.addComponent(optionsPanel)
						.addGroup(checkPanelLayout.createParallelGroup()
								.addComponent(skipCheck)
								.addComponent(skipLabel)
						)
						.addGroup(checkPanelLayout.createParallelGroup()
								.addComponent(noReasonerCheck)
								.addComponent(noReasonerLabel)
						)
				)
		);
		
		
		//layout for the canel and proceed buttons
		GroupLayout canelProceedPanelLayout=new GroupLayout(cancelProceedPanel);
		cancelProceedPanel.setLayout(canelProceedPanelLayout);
		
		canelProceedPanelLayout.setAutoCreateGaps(true);
		canelProceedPanelLayout.setAutoCreateContainerGaps(true);
		
		canelProceedPanelLayout.setHorizontalGroup(
				canelProceedPanelLayout.createSequentialGroup()
					.addComponent(cancel)
					.addComponent(proceed)
		);
		
		canelProceedPanelLayout.setVerticalGroup(
				canelProceedPanelLayout.createParallelGroup()
				.addGroup(canelProceedPanelLayout.createSequentialGroup()
						.addComponent(cancel)
				)
				.addGroup(canelProceedPanelLayout.createSequentialGroup()
						.addComponent(proceed)
				)
		);
		
		//add all the panels to the frame layout, use trailing so that the cancel/proceed buttons are on the right side of the frame
		layout.setHorizontalGroup(	layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(checkboxPanel)
						.addGroup(layout.createSequentialGroup()
								.addComponent(cancelProceedPanel)
						)
				)
					
		);
		
		layout.setVerticalGroup( layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(checkboxPanel)
						.addGroup(layout.createParallelGroup()
									.addComponent(cancelProceedPanel)
						)
				)
		);
		// end of Layout Code
		
		frame.addWindowListener(ui.new WindowEventHandler());
		frame.pack(); // automatically set the frame size
		frame.setLocationRelativeTo(null); 	// center the window on the screen
		
		frame.setVisible(true);
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		JFileChooser fc;
		AppPreferences prefs = Core.getAppPreferences(); // Class interface to Application Preferences
		
		if(obj == cancel){
			frame.dispose();
		}else if(obj==databaseRadio){
			databaseSettings.setEnabled(true);
		}else if(obj==memoryRadio){
			databaseSettings.setEnabled(false);
		}else if(obj==databaseSettings){
			//open a new dialog that has fields for the database connection settings
			JDialog dSettings=new DatabaseSettingsDialog(frame,true,true);
			Preferences p=Preferences.userNodeForPackage(DatabaseSettingsDialog.class);
		}else if(obj == browse){
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
				filePath.setText(selectedfile.getPath());
				
			}
		}else if(obj == proceed){
			String filename = filePath.getText();
			if(filename.equals("")){
				JOptionPane.showMessageDialog(frame, "Load an ontology file to proceed.", "Filename is empty", JOptionPane.ERROR_MESSAGE);
			}else{
				try{
					ui.openFile(filename, ontoType, syntaxCombo.getSelectedIndex(), langCombo.getSelectedIndex(), skipCheck.isSelected(), noReasonerCheck.isSelected(),false);
					// once we are done, let's save the syntax and language selection that was made by the user
					// and save the file used to the recent file list, and also what syntax and language it is
					prefs.saveOpenDialogListSelection(syntaxCombo.getSelectedIndex() , langCombo.getSelectedIndex(), skipCheck.isSelected(), noReasonerCheck.isSelected());
					prefs.saveRecentFile(filePath.getText(), ontoType, syntaxCombo.getSelectedIndex(), langCombo.getSelectedIndex(), skipCheck.isSelected(), noReasonerCheck.isSelected());
					ui.getUIMenu().refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.
				
				}catch(Exception ex){
					JOptionPane.showConfirmDialog(frame,"Can not parse the file '" + filename + "'. Please check the policy.","Parser Error",JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}

				frame.dispose();
			}
		}// end of obj == proceed
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource() == langCombo){
			if(langCombo.getSelectedIndex() == 2)//for XML files selection
				syntaxCombo.setEnabled(false);
			else
				syntaxCombo.setEnabled(true);
		}
	}
	public static void main(String[] args)
	{
		OpenOntologyFileDialog n=new OpenOntologyFileDialog(0, new UI());
	}
	
}
