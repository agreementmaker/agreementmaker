package am.userInterface;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
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
import javax.swing.filechooser.FileFilter;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.AbstractMatcher.alignType;
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
	private JCheckBox boxSort, boxIsolines, boxSkipZeros;
	
	private JRadioButton radAlignmentOnly, radMatrixAsCSV, radCompleteMatcher, radClassesMatrix, radPropertiesMatrix;
	
	/**
	 * Helper function to create the export options panel.
	 * @return The panel in the dialog that contains the radio buttons of what should be saved.
	 */
	private JPanel getSaveOptionsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Export: "));
		
		// create the radio buttons (using the preferences to set the options)
		radAlignmentOnly = new JRadioButton("Alignment only", prefs.isExportTypeSelected( ExportType.ALIGNMENT_ONLY ));
		radAlignmentOnly.setActionCommand( ExportType.ALIGNMENT_ONLY.getKey() );
		radAlignmentOnly.addActionListener(this);
		radMatrixAsCSV = new JRadioButton("Similarity matrix as CSV", prefs.isExportTypeSelected( ExportType.MATRIX_AS_CSV ));
		radMatrixAsCSV.setActionCommand( ExportType.MATRIX_AS_CSV.getKey() );
		radMatrixAsCSV.addActionListener(this);
		radCompleteMatcher = new JRadioButton("Complete Matcher", prefs.isExportTypeSelected( ExportType.COMPLETE_MATCHER ) );
		radCompleteMatcher.setActionCommand( ExportType.COMPLETE_MATCHER.getKey() );
		radCompleteMatcher.addActionListener(this);
		
		radClassesMatrix = new JRadioButton("Classes Matrix", prefs.isExportClassesMatrix() );
		radPropertiesMatrix = new JRadioButton("Properties Matrix", !prefs.isExportClassesMatrix() );
		boxSort = new JCheckBox("Sort by similarity");
		boxSort.setToolTipText("Sort rows and columns by similarity.");
		boxSort.setSelected( prefs.getExportSort() );
		
		boxIsolines = new JCheckBox("Add \"Isolines\"");
		boxIsolines.setToolTipText("Add blank lines when the x-column value changes.  For compatibility with GNUPlot.");
		boxIsolines.setSelected( prefs.getExportIsolines() );
		
		boxSkipZeros = new JCheckBox("Skip Zeros");
		boxSkipZeros.setToolTipText("Do not write points which have a similarity of 0.0.  Useful for plotting reference alignments.");
		boxSkipZeros.setSelected( prefs.getExportSkipZeros() );
		
		// create a button group for the radio buttons
		ButtonGroup grpType = new ButtonGroup();
		grpType.add(radAlignmentOnly);
		grpType.add(radMatrixAsCSV);
		grpType.add(radCompleteMatcher);
		
		ButtonGroup grpMatrix = new ButtonGroup();
		grpMatrix.add(radClassesMatrix);
		grpMatrix.add(radPropertiesMatrix);
		
		// we need to create a subpanel to group the "Alignment only" radio button with the format label and combobox.
		lblFileFormat = new JLabel("Format: ");
		
		cmbAlignmentFormat = new JComboBox( OutputController.getAlignmentFormatDescriptionList() );
		cmbAlignmentFormat.setSelectedIndex( prefs.getExportAlignmentFormatIndex() );
		
		// a panel for the alignment format label + combo box 
		JPanel pnlAlignmentFormat = new JPanel();
		pnlAlignmentFormat.setBorder( BorderFactory.createEmptyBorder() );
		//FlowLayout subLayout = new FlowLayout( FlowLayout.LEADING );
		
		GroupLayout layAlignmentFormat = new GroupLayout(pnlAlignmentFormat);
		layAlignmentFormat.setAutoCreateGaps(true);

		
		layAlignmentFormat.setHorizontalGroup( layAlignmentFormat.createSequentialGroup()
				.addGap(30)
				.addComponent(lblFileFormat)
				.addComponent(cmbAlignmentFormat, cmbAlignmentFormat.getPreferredSize().width, cmbAlignmentFormat.getPreferredSize().width, cmbAlignmentFormat.getPreferredSize().width)
		);
		
		layAlignmentFormat.setVerticalGroup( layAlignmentFormat.createParallelGroup(Alignment.CENTER)
				.addComponent(lblFileFormat)
				.addComponent(cmbAlignmentFormat, cmbAlignmentFormat.getPreferredSize().height,cmbAlignmentFormat.getPreferredSize().height,cmbAlignmentFormat.getPreferredSize().height)
		);
		
		pnlAlignmentFormat.setLayout(layAlignmentFormat);
		
		// a panel for the classes matrix and properties matrix radio buttons
		JPanel pnlMatrices = new JPanel();
		pnlMatrices.setBorder( BorderFactory.createEmptyBorder() );
		
		GroupLayout layMatrices = new GroupLayout(pnlMatrices);
		layMatrices.setAutoCreateGaps(true);
		
		layMatrices.setHorizontalGroup(  layMatrices.createSequentialGroup()
				.addGap(30)
				.addGroup( layMatrices.createParallelGroup()
						.addComponent(radClassesMatrix)
						.addComponent(radPropertiesMatrix)
				)
				.addGap(10)
				.addGroup( layMatrices.createParallelGroup()
						.addComponent(boxSort)
						.addComponent(boxIsolines)
						.addComponent(boxSkipZeros)
				)
				.addGap(30)
		);
		layMatrices.setVerticalGroup(  layMatrices.createParallelGroup()
				.addGroup(layMatrices.createSequentialGroup()
						.addComponent(radClassesMatrix)
						.addComponent(radPropertiesMatrix)
				)
				.addGroup( layMatrices.createSequentialGroup()
						.addComponent(boxSort)
						.addComponent(boxIsolines)
						.addComponent(boxSkipZeros)
				)
		);
		pnlMatrices.setLayout(layMatrices);
		
		panel.setLayout(new GridLayout(5,1) );
		panel.add(radAlignmentOnly);
		panel.add(pnlAlignmentFormat);
		panel.add(radMatrixAsCSV);
		panel.add(pnlMatrices);
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
		if( prefs.getExportLastDir().exists() ) {
			txtFileDir.setText(prefs.getExportLastDir().getPath());
			Dimension currentPreferred = txtFileDir.getPreferredSize();
			currentPreferred.width += 50;
			txtFileDir.setPreferredSize(currentPreferred);
		}
		txtFilename.setText(prefs.getExportLastFilename());		
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
		
		// some ease of use code
		setAlignmentOnlyEnabled( radAlignmentOnly.isSelected() );		
		setMatrixRadioButtonsEnable( radMatrixAsCSV.isSelected() );
	
		
		addWindowListener(Core.getUI().new WindowEventHandler());
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
		} else if( obj == radAlignmentOnly || obj == radMatrixAsCSV || obj == radCompleteMatcher ) {
			// enables/disables controls depending on which radio button is selected
			setAlignmentOnlyEnabled(radAlignmentOnly.isSelected());
			setMatrixRadioButtonsEnable(radMatrixAsCSV.isSelected());	
		}
	}
	
	private void setAlignmentOnlyEnabled( boolean en ) {
		lblFileFormat.setEnabled(en);
		cmbAlignmentFormat.setEnabled(en);
	}
	private void setMatrixRadioButtonsEnable( boolean en ) {
		radClassesMatrix.setEnabled(en);
		radPropertiesMatrix.setEnabled(en);
		boxSort.setEnabled(en);
	}
	
	private void save() {
		try {
			// what kind of export are we doing?
			ExportType outputType;
			if( radCompleteMatcher.isSelected() ) { outputType = ExportType.COMPLETE_MATCHER; }
			else if( radMatrixAsCSV.isSelected() ) { outputType = ExportType.MATRIX_AS_CSV; }
			else { outputType = ExportType.ALIGNMENT_ONLY; } // use Alignment_ONLY as default.
			
			// directory, filename, fileformat ?
			String outDirectory = txtFileDir.getText();
			String outFileName = txtFilename.getText();
			int outFormatIndex = cmbAlignmentFormat.getSelectedIndex();
			
			if(outDirectory.equals("")){
				JOptionPane.showMessageDialog(this, "Select the directory for the output file to proceed.");
			}
			else if(outFileName.equals("")){
				JOptionPane.showMessageDialog(this, "Insert a name for the output file to proceed");
			}
			else if(outFileName.indexOf(".")!=-1) {
				JOptionPane.showMessageDialog(this, "Insert a file name without Extension");
			}
			else{
				// save app preferences.
				prefs.saveExportLastFilename(outFileName);
				prefs.saveExportLastDir(outDirectory);
				prefs.saveExportType(outputType);
				
				if( outputType == ExportType.ALIGNMENT_ONLY ) {
					prefs.saveExportAlignmentFormatIndex(outFormatIndex);
					try {
						// full file name
						String fullFileName = outDirectory+ "/" +outFileName+ "." + OutputController.getAlignmentFormatExtension(outFormatIndex);

						if( OutputController.getAlignmentFormatExtension(outFormatIndex) == "rdf" ){ // RDF	
							OutputController.printDocumentOAEI(fullFileName);
							Utility.displayMessagePane("File saved successfully.\nLocation: "+fullFileName+"\n", null);
						}
						else{						
							OutputController.printDocument(fullFileName);
							Utility.displayMessagePane("File saved successfully.\nLocation: "+fullFileName+"\n", null);	
						}
						setModal(false);
						dispose();
					}
					catch(Exception e) {
						e.printStackTrace();
						//ok = false;
						JOptionPane.showMessageDialog(this, "Error while saving the file\nTry to change filename or location.");
					}
				} else if( outputType == ExportType.MATRIX_AS_CSV ) {
					prefs.saveExportClassesMatrix( radClassesMatrix.isSelected() );
					prefs.saveExportSort(boxSort.isSelected());
					prefs.saveExportIsolines(boxIsolines.isSelected());
					prefs.saveExportSkipZeros(boxSkipZeros.isSelected());
					// full file name
					String fullFileName = outDirectory+ "/" + outFileName + ".csv";
					
					
					ArrayList<AbstractMatcher> list = Core.getInstance().getMatcherInstances();
					AbstractMatcher matcher;
					int[] rowsIndex = Core.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
					matcher = list.get(rowsIndex[0]); // we only care about the first matcher selected
					
					if( radClassesMatrix.isSelected() ) {
						if( matcher.getClassesMatrix() == null ) {
							// create a new matrix
							if( matcher.getSourceOntology() == null || matcher.getTargetOntology() == null ) { 
								throw new Exception("Matcher does not have Source or Target ontologies set.");
							}
							AlignmentMatrix m = new AlignmentMatrix(matcher.getSourceOntology().getClassesList().size(), 
																	matcher.getTargetOntology().getClassesList().size(), 
																	alignType.aligningClasses);
							if( matcher.getClassAlignmentSet() == null ) 
								throw new Exception("Matcher does not have a Classes Matrix nor a Classes Alignment Set.  Cannot do anything.");
							
							for( int i = 0; i < matcher.getClassAlignmentSet().size(); i++ ) {
								am.app.mappingEngine.Alignment currentAlignment = matcher.getClassAlignmentSet().getAlignment(i);
								m.set(currentAlignment.getEntity1().getIndex(), currentAlignment.getEntity2().getIndex(), currentAlignment);
							}
							
							OutputController.saveMatrixAsCSV(m, fullFileName, boxSort.isSelected(), boxIsolines.isSelected(), boxSkipZeros.isSelected());
							
						} else { 
							OutputController.saveMatrixAsCSV(matcher.getClassesMatrix(), fullFileName, boxSort.isSelected(), boxIsolines.isSelected(), boxSkipZeros.isSelected());
						}
					} else {
						if( matcher.getPropertiesMatrix() == null ) {
							// create a new matrix
							if( matcher.getSourceOntology() == null || matcher.getTargetOntology() == null ) { 
								throw new Exception("Matcher does not have Source or Target ontologies set.");
							}
							AlignmentMatrix m = new AlignmentMatrix(matcher.getSourceOntology().getPropertiesList().size(), 
																	matcher.getTargetOntology().getPropertiesList().size(), 
																	alignType.aligningProperties);
							if( matcher.getPropertyAlignmentSet() == null ) 
								throw new Exception("Matcher does not have a Properties Matrix nor a Properties Alignment Set.  Cannot do anything.");
							
							for( int i = 0; i < matcher.getPropertyAlignmentSet().size(); i++ ) {
								am.app.mappingEngine.Alignment currentAlignment = matcher.getPropertyAlignmentSet().getAlignment(i);
								m.set(currentAlignment.getEntity1().getIndex(), currentAlignment.getEntity2().getIndex(), currentAlignment);
							}
							
							OutputController.saveMatrixAsCSV(m, fullFileName, boxSort.isSelected(), boxIsolines.isSelected(), boxSkipZeros.isSelected());
						} else {
							OutputController.saveMatrixAsCSV(matcher.getPropertiesMatrix(), fullFileName, boxSort.isSelected(), boxIsolines.isSelected(), boxSkipZeros.isSelected());
						}
					}
					Utility.displayMessagePane("File saved successfully.\nLocation: "+fullFileName+"\n", null);
				} else if( outputType == ExportType.COMPLETE_MATCHER ) {
					throw new Exception("Michele, implement this function.");
					//Utility.displayMessagePane("File saved successfully.\nLocation: "+fullFileName+"\n", null);
				} else {
					throw new Exception("Could not determine the output type.\nAt least one radio button must be selected.");
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
                return "Directory";
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
