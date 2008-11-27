package agreementMaker.application.mappingEngine.referenceAlignment;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import agreementMaker.application.Core;
import agreementMaker.application.evaluationEngine.ReferenceEvaluation;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.AbstractParameters;
import agreementMaker.application.mappingEngine.Evaluator;
import agreementMaker.application.mappingEngine.MatcherSetting;
import agreementMaker.userInterface.AppPreferences;
import agreementMaker.userInterface.ReferenceOutputFileDialog;
import agreementMaker.userInterface.UI;
import agreementMaker.userInterface.UI.WindowEventHandler;

public class ReferenceAlignmentParametersPanel extends AbstractMatcherParametersPanel implements ActionListener {

	/**
	 * Base Similarity Matcher - The Parameters Panel
	 * @author Cosmin Stroe
	 * @date Nov 22, 2008
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private ReferenceAlignmentParameters parameters;
	
	private JButton browse;
	public JTextField filePath;
	private JLabel fileType;
	public JList formatList;


	
	private AppPreferences prefs;
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public ReferenceAlignmentParametersPanel() {
		super();
		prefs = Core.getInstance().getUI().getAppPreferences(); // Class interface to Application Preferences
		fileType = new JLabel("Reference file");
		filePath = new JTextField(0);
		//the system suggests the last file opened
		if( prefs.getLastDirReference().exists() ) {
			filePath.setText(prefs.getLastDirReference().getPath());
		}
		
		browse = new JButton("Browse...");
		browse.addActionListener(this);
		
		//Formats are fixed, the development.ReferenceEvaluation class contains definitions.
		String[] format_list = {Evaluator.REF0,Evaluator.REF1,Evaluator.REF2, Evaluator.REF3};
		formatList = new JList(format_list);
		formatList.setPrototypeCellValue("012345678901234567890123456789012345678901234567890123456789"); // this string sets the width of the list
		formatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		formatList.setVisibleRowCount(3);
		formatList.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "File format"));
		formatList.setSelectedIndex(prefs.getFileFormatReference());  // select the last thing selected

		
		//Make the GroupLayout for this dialog (somewhat complicated, but very flexible)
		// This Group layout lays the items in relation with eachother.  The horizontal
		// and vertical groups decide the relation between UI elements.
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(fileType) 					// fileType label
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(filePath) 			// filepath text
							.addComponent(formatList) 	
							)
					.addGroup(layout.createParallelGroup()
							.addComponent(browse)
							)
		);
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(
				layout.createParallelGroup()
					.addComponent(fileType)
					.addGroup(layout.createSequentialGroup()
							.addComponent(filePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							          GroupLayout.PREFERRED_SIZE)
							.addComponent(formatList)
							)
					.addGroup(layout.createSequentialGroup() 
							.addComponent(browse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							          GroupLayout.PREFERRED_SIZE))
		);
	}
	
	public void actionPerformed (ActionEvent ae){
		
		Object obj = ae.getSource();
		JFileChooser fc;
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
		
		if(obj == browse){
			
			
			File lastFile;
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

	
	
	public ReferenceAlignmentParameters getParameters() {
		
		parameters = new ReferenceAlignmentParameters();
		parameters.fileName = filePath.getText();
		parameters.format = (String)formatList.getSelectedValue();
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
		prefs.saveLastFormatReference(formatList.getSelectedIndex());	
		return null;//null means everything ok
	}
}
