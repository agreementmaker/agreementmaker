package agreementMaker.userInterface;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import agreementMaker.GSM;
import agreementMaker.development.ReferenceEvaluation;
import agreementMaker.userInterface.vertex.VertexDescriptionPane;


/**
 * @author Nalin
 *
 */
public class ReferenceFileDialog implements ActionListener{
	
	/**
	 * @param args
	 */
	
	private JButton browse, cancel, next;
	public JTextField filePath;
	private JLabel fileType;
	public JDialog frame;
	public JList formatList;
	public UI ui;
	private ReferenceOutputFileDialog nextDialog;
	
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public ReferenceFileDialog(UI userInterface) {
		  
		ui = userInterface;
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
		frame = new JDialog();
		frame.setTitle("Evaluate reference - reference file ");
		fileType = new JLabel("Reference file");
		filePath = new JTextField(0);
		//the system suggests the last file opened
		if( prefs.getLastDirReference().exists() ) {
			filePath.setText(prefs.getLastDirReference().getPath());
		}
		
		browse = new JButton("Browse...");
		cancel = new JButton("Cancel");
		next = new JButton("Next");
		browse.addActionListener(this);
		cancel.addActionListener(this);
		next.addActionListener(this);
		
		//Formats are fixed, the development.ReferenceEvaluation class contains definitions.
		String[] format_list = {ReferenceEvaluation.REF1,ReferenceEvaluation.REF2, ReferenceEvaluation.REF3};
		
		
		formatList = new JList(format_list);
		formatList.setPrototypeCellValue("012345678901234567890123456789012345678901234567890123456789"); // this string sets the width of the list
		formatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		formatList.setVisibleRowCount(3);
		formatList.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "File format"));
		formatList.setSelectedIndex(prefs.getFileFormatReference());  // select the last thing selected

		
		//Make the GroupLayout for this dialog (somewhat complicated, but very flexible)
		// This Group layout lays the items in relation with eachother.  The horizontal
		// and vertical groups decide the relation between UI elements.
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		
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
							.addGroup(layout.createSequentialGroup()
									.addComponent(cancel)		// the buttons are also part of their own groups
									.addComponent(next)
									)
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
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
									.addComponent(cancel)
									.addComponent(next)
									)
							)
					.addGroup(layout.createSequentialGroup() 
							.addComponent(browse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							          GroupLayout.PREFERRED_SIZE))
		);

		// end of Layout Code
		
		frame.addWindowListener(new WindowEventHandler());//THIS SHOULD BE CHANGED THE PROGRAM SHOULD NOT CLOSE
		frame.pack(); // automatically set the frame size
		frame.setLocationRelativeTo(null); 	// center the window on the screen
		frame.setModal(true);
		
		frame.setVisible(true);
		
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */

	public void actionPerformed (ActionEvent ae){
		
		Object obj = ae.getSource();
		JFileChooser fc;
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
		
		if(obj == cancel){
			if(nextDialog != null) {
				nextDialog.frame.dispose();
				nextDialog = null;
			}
			frame.setModal(false);
			frame.dispose();
		}else if(obj == browse){
			
			
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
			
			int returnVal = fc.showOpenDialog(frame);

			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				File selectedfile = fc.getSelectedFile();
				
				// ok, now that we know what file the user selected
				// let's save it for future use (for the chooser)
				prefs.saveLastDirReference(selectedfile); 
				filePath.setText(selectedfile.getPath());
				

			}
		}
		else if(obj == next){
			if(filePath.getText().equals("")){
				JOptionPane.showMessageDialog(frame, "Load a reference file to proceed.");
			}
			else if(!new File(filePath.getText()).exists()) {
				JOptionPane.showMessageDialog(frame, "The reference file selected does not exist");
			}
			else{
				prefs.saveLastFormatReference(formatList.getSelectedIndex());	
				
				frame.setModal(false);
				frame.setVisible(false);
				if(nextDialog == null || nextDialog.frame == null) {
					ReferenceOutputFileDialog nextDialog = new ReferenceOutputFileDialog(this);
				}
				else {
					nextDialog.frame.setModal(true);
					nextDialog.frame.setVisible(true);	
				}			
			}
		}// end of obj == next
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	


	
}
