package am.app.mappingEngine.referenceAlignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.userInterface.AppPreferences;

public class ReferenceAlignmentParametersPanel extends AbstractMatcherParametersPanel implements ActionListener {

	/**
	 * The Parameters Panel
	 * @author Flavio
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private ReferenceAlignmentParameters parameters;
	
	private JButton browse;
	public JTextField filePath;
	private JLabel fileType;
	public JList formatList;
	public JCheckBox equivalenceCheck, chkSkipClasses, chkSkipProperties;

	private static final String PREF_EQUIVALENCE = "EQUIVALENCE";
	private static final String PREF_SKIPCLASSES = "SKIPCLASSES";
	private static final String PREF_SKIPPROPERTIES = "SKIPPROPERTIES";
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public ReferenceAlignmentParametersPanel() {
		super();
		AppPreferences prefs = Core.getAppPreferences(); // Class interface to Application Preferences
		
		
		fileType = new JLabel("Select File");
		filePath = new JTextField(0);
		//the system suggests the last file opened
		if( prefs.getLastDirReference().exists() ) {
			filePath.setText(prefs.getLastDirReference().getPath());
		}
		
		browse = new JButton("Browse...");
		browse.addActionListener(this);
		
		//Formats are fixed, the development.ReferenceEvaluation class contains definitions.
		String[] format_list = {ReferenceAlignmentMatcher.REF5, ReferenceAlignmentMatcher.OAEI,ReferenceAlignmentMatcher.OLD_OAEI,ReferenceAlignmentMatcher.REF2a, ReferenceAlignmentMatcher.REF2b,ReferenceAlignmentMatcher.REF2c, ReferenceAlignmentMatcher.REF3};
		formatList = new JList(format_list);
		formatList.setPrototypeCellValue("012345678901234567890123456789012345678901234567890123456789"); // this string sets the width of the list
		formatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		formatList.setVisibleRowCount(3);
		formatList.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "File format"));
		formatList.setSelectedIndex(prefs.getFileFormatReference());  // select the last thing selected

		equivalenceCheck = new JCheckBox("Consider only mappings with EQUIVALENCE relations");
		equivalenceCheck.setSelected(prefs.getPanelBool(PREF_EQUIVALENCE, false));

		chkSkipClasses = new JCheckBox("Skip classes");
		chkSkipClasses.setSelected(prefs.getPanelBool(PREF_SKIPCLASSES, false));
		
		chkSkipProperties = new JCheckBox("Skip properties");
		chkSkipProperties.setSelected(prefs.getPanelBool(PREF_SKIPPROPERTIES, false));
		
		//Make the GroupLayout for this dialog (somewhat complicated, but very flexible)
		// This Group layout lays the items in relation with eachother.  The horizontal
		// and vertical groups decide the relation between UI elements.
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
					.addComponent(fileType) 					// fileType label
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(filePath, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE) 			// filepath text
							.addComponent(formatList) 	
							)
					.addGroup(layout.createParallelGroup()
							.addComponent(browse)
							)
				)
				.addGroup(layout.createSequentialGroup()
						.addComponent(equivalenceCheck) 
				)
				.addGroup(layout.createSequentialGroup()
						.addComponent(chkSkipClasses)
						.addComponent(chkSkipProperties)
				)
		);
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(fileType)
					.addGroup(layout.createSequentialGroup()
							.addComponent(filePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							          GroupLayout.PREFERRED_SIZE)
							.addComponent(formatList)
							)
					.addGroup(layout.createSequentialGroup() 
							.addComponent(browse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							          GroupLayout.PREFERRED_SIZE))
	            )
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(equivalenceCheck) 
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(chkSkipClasses)
						.addComponent(chkSkipProperties)
				)
		);
	}
	
	public void actionPerformed (ActionEvent ae){
		
		Object obj = ae.getSource();
		JFileChooser fc;
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
		
		if(obj == browse){
			
			
			//File lastFile;
			
			//If the user has opened a reference file already the system will use the last reference file dir to start the chooser,
			//if he didn't the system will try to use the last dir used to open the ontologies, maybe is the same one of the reference or it's closer
			//if not even that one exists, the chooser starts normally
			if( prefs.getLastDirReference().exists() ) {
				fc = new JFileChooser(prefs.getLastDirReference());
			}
			else if( prefs.getLastDir().exists() ) {
				fc = new JFileChooser(prefs.getLastDir());
			} else { fc = new JFileChooser(); } 
			
			int returnVal = fc.showOpenDialog(null);

			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				File selectedfile = fc.getSelectedFile();
				
				// ok, now that we know what file the user selected
				// let's save it for future use (for the chooser)
				prefs.saveLastDirReference(selectedfile); 
				filePath.setText(selectedfile.getPath());
				

			}
		}
	}

	
	
	public AbstractParameters getParameters() {
		
		parameters = new ReferenceAlignmentParameters();
		parameters.fileName = filePath.getText();
		parameters.format = (String)formatList.getSelectedValue();
		parameters.onlyEquivalence = equivalenceCheck.isSelected();
		parameters.skipClasses = chkSkipClasses.isSelected();
		parameters.skipProperties = chkSkipProperties.isSelected();
		return parameters;
	}
	
	public String checkParameters() {
		if(filePath.getText().equals("")){
			return "Load a reference file to proceed.";
		}
		if(!new File(filePath.getText()).exists()) {
			return "The reference file selected does not exist";
		}
		Object format = formatList.getSelectedValue();
		if(format == null)
			return "Select reference file's format";
		
		// save settings
		Core.getAppPreferences().saveLastFormatReference(formatList.getSelectedIndex());
		Core.getAppPreferences().savePanelBool(PREF_EQUIVALENCE, equivalenceCheck.isSelected());
		Core.getAppPreferences().savePanelBool(PREF_SKIPCLASSES, chkSkipClasses.isSelected());
		Core.getAppPreferences().savePanelBool(PREF_SKIPPROPERTIES, chkSkipProperties.isSelected());
		
		return null;//null means everything ok
	}
}
