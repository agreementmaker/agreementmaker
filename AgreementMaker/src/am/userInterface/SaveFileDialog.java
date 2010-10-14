package am.userInterface;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import am.Utility;
import am.app.Core;
import am.output.OutputController;
import am.userInterface.AppPreferences.ExportType;



public class SaveFileDialog extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = 7313974994418768939L;
	AppPreferences prefs = new AppPreferences(); // Application preferences.  Used to automatically fill in previous values.

	private JLabel lblFilename, lblFileDir, lblFileFormat;
	private JButton btnBrowse, btnCancel, btnSave;
	private JTextField txtFilename, txtFileDir;
	private JComboBox cmbAlignmentFormat;
	private JPanel pnlSaveOptions;
	
	private JRadioButton radAlignmentOnly, radMatrixAsCSV, radCompleteMatcher;
	
	/**
	 * Helper function to create the export options panel.
	 * @return The panel in the dialog that contains the radio buttons of what should be saved.
	 */
	private JPanel getSaveOptionsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Export: "));
		
		// create the radio buttons (using the preferences to set the options)
		radAlignmentOnly = new JRadioButton("Alignment only.", prefs.isExportTypeSelected( ExportType.ALIGNMENT_ONLY ));
		radMatrixAsCSV = new JRadioButton("Similarity matrix as CSV", prefs.isExportTypeSelected( ExportType.MATRIX_AS_CSV ));
		radCompleteMatcher = new JRadioButton("Complete Matcher", prefs.isExportTypeSelected( ExportType.COMPLETE_MATCHER ) );
		
		// create a button group for the radio buttons
		ButtonGroup grpType = new ButtonGroup();
		grpType.add(radAlignmentOnly);
		grpType.add(radMatrixAsCSV);
		grpType.add(radCompleteMatcher);
		
		// we need to create a subpanel to group the "Alignment only" radio button with the format label and combobox.
		lblFileFormat = new JLabel("Format: ");
		
		cmbAlignmentFormat = new JComboBox( OutputController.getAlignmentFormatDescriptionList() );
		cmbAlignmentFormat.setSelectedIndex( prefs.getExportAlignmentFormatIndex() );
		
		JPanel subPanel = new JPanel();
		subPanel.setBorder( BorderFactory.createEmptyBorder() );
		//FlowLayout subLayout = new FlowLayout( FlowLayout.LEADING );
		
		GroupLayout subLayout = new GroupLayout(subPanel);
		subLayout.setAutoCreateGaps(true);

		
		subLayout.setHorizontalGroup( subLayout.createSequentialGroup()
				.addComponent(radAlignmentOnly)
				.addGap(30)
				.addComponent(lblFileFormat)
				.addComponent(cmbAlignmentFormat)
				.addGap(20)
		);
		
		subLayout.setVerticalGroup( subLayout.createParallelGroup(Alignment.CENTER)
				.addComponent(radAlignmentOnly)
				.addComponent(lblFileFormat)
				.addComponent(cmbAlignmentFormat)
		);
		
		subPanel.setLayout(subLayout);
		
		panel.setLayout(new GridLayout(3,1) );
		panel.add(subPanel);
		panel.add(radMatrixAsCSV);
		panel.add(radCompleteMatcher);
		
		return panel;
	}

	/**
	 * This is the dialog that is shown when the Export menu item is used.
	 * It can save Alignments or Similarity Matrices of Matchers, or complete Matchers (for later Import).
	 */
	public SaveFileDialog() {
		 // Class interface to Application Preferences
		
		// 
		setTitle("Export ...");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// elements of the dialog (in order from left to right, top to bottom) 
		lblFilename = new JLabel("Filename (without extension): ");
		txtFilename = new JTextField();
		lblFileDir = new JLabel("Save in folder: ");
		txtFileDir = new JTextField();
		
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(this);
		
		// Restore some values saved 
		//the system suggests the last file opened
		if( prefs.getLastDirOutput().exists() ) {
			txtFileDir.setText(prefs.getLastDirOutput().getPath());
			Dimension currentPreferred = txtFileDir.getPreferredSize();
			currentPreferred.width += 50;
			txtFileDir.setPreferredSize(currentPreferred);
		}
		txtFilename.setText(prefs.getLastNameOutput());
		
		pnlSaveOptions = getSaveOptionsPanel();
		
		
		btnCancel = new JButton("Cancel");
		btnSave = new JButton("Save");
		
		btnCancel.addActionListener(this);
		btnSave.addActionListener(this);
				
		
		//Make the GroupLayout for this dialog (somewhat complicated, but very flexible)
		// This Group layout lays the items in relation with eachother.  The horizontal
		// and vertical groups decide the relation between UI elements.
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup( layout.createParallelGroup(Alignment.TRAILING)
				.addGroup( layout.createSequentialGroup()
						.addComponent(lblFilename)
						.addComponent(txtFilename)
				)
				.addGroup( layout.createSequentialGroup()
						.addComponent(lblFileDir)
						.addComponent(txtFileDir)
						.addComponent(btnBrowse)
				)
				.addComponent(pnlSaveOptions)
				.addGroup( layout.createSequentialGroup()
						.addComponent(btnCancel)
						.addComponent(btnSave)
				)
		);

		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged		
		layout.setVerticalGroup( layout.createSequentialGroup()
				.addGroup( layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblFilename)
						.addComponent(txtFilename)
				)
				.addGroup( layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblFileDir)
						.addComponent(txtFileDir)
						.addComponent(btnBrowse)
				)
				.addComponent(pnlSaveOptions)
				.addGroup( layout.createParallelGroup(Alignment.CENTER)
						.addComponent(btnCancel)
						.addComponent(btnSave)
				)
		);
		
		// end of Layout Code
		addWindowListener(Core.getInstance().getUI().new WindowEventHandler());
		pack(); // automatically set the frame size
		setLocationRelativeTo(null); 	// center the window on the screen
		setModal(true);
		setVisible(true);
		//the order of modal and visible must be exactly this one!
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if(obj == btnCancel){
			setModal(false);
			dispose();
		}
		else if(obj == btnBrowse){
			browse();
		}
		else if(obj == btnSave){
			save();
		}
	}
	
	private void save() {
		try {
			AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
			String outPath = txtFileDir.getText();
			String outName = txtFilename.getText();
			int outF = cmbAlignmentFormat.getSelectedIndex();
			String fullName = outPath+"/"+outName+"."+ OutputController.getAlignmentFormatExtension(outF);

			if(outPath.equals("")){
				JOptionPane.showMessageDialog(this, "Select the directory for the output file to proceed.");
			}
			else if(outName.equals("")){
				JOptionPane.showMessageDialog(this, "Insert a name for the output file to proceed");
			}
			else if(outName.indexOf(".")!=-1) {
				JOptionPane.showMessageDialog(this, "Insert a file name without Extension");
			}
			else{
				//boolean ok = true;
				try {
					//File f = new File(fullName);
					if(outF == 0 ){ // RDF
						prefs.saveLastNameOutput(outName);
						OutputController.printDocumentOAEI(fullName);
						Utility.displayMessagePane("File saved successfully.\nLocation: "+fullName+"\n", null);
						setModal(false);
						dispose();
					}
					else{
						prefs.saveLastNameOutput(outName);
						OutputController.printDocument(fullName);
						Utility.displayMessagePane("File saved successfully.\nLocation: "+fullName+"\n", null);
						setModal(false);
						dispose();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					//ok = false;
					JOptionPane.showMessageDialog(this, "Error while saving the file\nTry to change filename or location.");
				}
			}
		}
		catch(Exception e) {
			//for developer users, when the tool released there should be a standard message like Unexpected Exception, for us it's useful to keep it full now
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}
	}

	private void browse() {
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences 
		JFileChooser fc; 
		
		//If the user has select an output file already the system will use the last ouput file dir to start the chooser, 
		//if he didn't the system will try to use the last dir used to open the ontologies, maybe is the same one of the reference or it's closer 
		//if not even that one exists, the chooser starts normally 
		if(prefs.getLastDirOutput().exists()) {  
			fc = new JFileChooser(prefs.getLastDirOutput()); 
		} 	 
		else if( prefs.getLastDir().exists() ) { 
			fc = new JFileChooser(prefs.getLastDir()); 
		} else { fc = new JFileChooser(); } 

		//This lines are needed to set the filechooser as directory chooser, we are creating a file filter class here which has to implements all needed methods.
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
		fc.setFileFilter(new FileFilter() {  
            public boolean accept(File f) {  
                if (f.isDirectory()) { 
                    return true;   
                } 
                return false;  
            }
 
            public String getDescription() { 
                return "";
            }
        });

		int returnVal = fc.showOpenDialog(this);

		if( returnVal == JFileChooser.APPROVE_OPTION ) {
			File selectedfile = fc.getSelectedFile();	
			// ok, now that we know what file the user selected
			// let's save it for future use (for the chooser)
			prefs.saveLastDirOutput(selectedfile); 
			txtFileDir.setText(selectedfile.getPath());
		}
	}
	
}
