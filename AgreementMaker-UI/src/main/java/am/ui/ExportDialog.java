package am.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
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
import javax.swing.filechooser.FileFilter;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.parsing.OutputController;
import am.utility.AppPreferences;
import am.utility.AppPreferences.FileType;


/**
 * The matcher export dialog.
 * 
 * Allows to save:
 * 
 * (1) Alignments in RDF and text format.
 * (2) Similarity matrices in CSV format.
 * 
 * TODO: Save complete matchers.
 * 
 * @author Cosmin Stroe
 *
 */
public class ExportDialog extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = 7313974994418768939L;
	AppPreferences prefs = new AppPreferences(); // Application preferences.  Used to automatically fill in previous values.

	private JLabel lblMatcher, lblFilename, lblFileDir, lblFileFormat;
	private JButton btnBrowse, btnCancel, btnSave;
	private JTextField txtFilename, txtFileDir;
	private JComboBox<String> cmbAlignmentFormat;
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
		radAlignmentOnly = new JRadioButton("Alignment only", prefs.isExportTypeSelected( FileType.ALIGNMENT_ONLY ));
		radAlignmentOnly.setActionCommand( FileType.ALIGNMENT_ONLY.getKey() );
		radAlignmentOnly.addActionListener(this);
		radMatrixAsCSV = new JRadioButton("Similarity matrix as CSV", prefs.isExportTypeSelected( FileType.MATRIX_AS_CSV ));
		radMatrixAsCSV.setActionCommand( FileType.MATRIX_AS_CSV.getKey() );
		radMatrixAsCSV.addActionListener(this);
		radCompleteMatcher = new JRadioButton("Complete Matcher", prefs.isExportTypeSelected( FileType.COMPLETE_MATCHER ) );
		radCompleteMatcher.setActionCommand( FileType.COMPLETE_MATCHER.getKey() );
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
		
		
		GroupLayout layMain = new GroupLayout(panel);
		layMatrices.setAutoCreateGaps(true);
		
		layMain.setHorizontalGroup( layMain.createParallelGroup()
				.addComponent(radAlignmentOnly)
				.addComponent(pnlAlignmentFormat)
				.addComponent(radMatrixAsCSV)
				.addComponent(pnlMatrices)
				.addComponent(radCompleteMatcher)
		);
		
		layMain.setVerticalGroup( layMain.createSequentialGroup() 
				.addComponent(radAlignmentOnly)
				.addComponent(pnlAlignmentFormat)
				.addGap(10)
				.addComponent(radMatrixAsCSV)
				.addComponent(pnlMatrices)
				.addGap(10)
				.addComponent(radCompleteMatcher)
				.addGap(10)
		);
		
		panel.setLayout(layMain);
		return panel;
	}

	/**
	 * This is the dialog that is shown when the Export menu item is used.
	 * It can save Alignments or Similarity Matrices of Matchers, or complete Matchers (for later Import).
	 */
	public ExportDialog(Frame parent) {
		super(parent, true);

		// 
		setTitle("Export ...");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// get the currently selected matcher
		List<MatchingTask> list = Core.getInstance().getMatchingTasks();
		MatchingTask selectedTask;
		int[] rowsIndex = UICore.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
		selectedTask = list.get(rowsIndex[0]); // we only care about the first matcher selected
		
		// elements of the dialog (in order from left to right, top to bottom)
		lblMatcher = new JLabel("Exporting \"" + selectedTask.matchingAlgorithm.getName() + "\"");
		
		lblFilename = new JLabel("Filename: ");
		txtFilename = new JTextField();
		lblFileDir = new JLabel("Save in folder: ");
		txtFileDir = new JTextField();
		txtFilename.setPreferredSize(new Dimension(500, lblFilename.getPreferredSize().height));
		
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
		
		int pnlwidth = lblFileDir.getPreferredSize().width + txtFileDir.getPreferredSize().width + btnBrowse.getPreferredSize().width;
		
		layout.setHorizontalGroup( layout.createParallelGroup(Alignment.TRAILING)
				.addComponent(lblMatcher, Alignment.LEADING)
				.addGroup( layout.createSequentialGroup()
						.addComponent(lblFilename)
						.addComponent(txtFilename)
				)
				.addGroup( layout.createSequentialGroup()
						.addComponent(lblFileDir)
						.addComponent(txtFileDir)
						.addComponent(btnBrowse)
				)
				.addComponent(pnlSaveOptions, pnlwidth, GroupLayout.PREFERRED_SIZE, 2000 )
				.addGroup( layout.createSequentialGroup()
						.addComponent(btnCancel)
						.addComponent(btnSave)
				)
		);

		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged		
		layout.setVerticalGroup( layout.createSequentialGroup()
				.addComponent(lblMatcher)
				.addGap(10)
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
	
		getRootPane().setDefaultButton(btnSave);
		
		pack(); // automatically set the frame size
		setLocationRelativeTo(null); 	// center the window on the screen
		setVisible(true);
		//the order of modal and visible must be exactly this one!
	}
	
	
	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
	    JRootPane rootPane = new JRootPane();
	    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
	    Action actionListener = new AbstractAction() {
	      public void actionPerformed(ActionEvent actionEvent) {
	        btnCancel.doClick();
	      }
	    };
	    InputMap inputMap = rootPane
	        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    inputMap.put(stroke, "ESCAPE");
	    rootPane.getActionMap().put("ESCAPE", actionListener);

	    return rootPane;
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
		boxIsolines.setEnabled(en);
		boxSkipZeros.setEnabled(en);
	}
	
	/**
	 * TODO: This is a mess, we need to fix it. -- Cosmin, Oct. 21, 2013.
	 */
	private void save() {
		try {
			// what kind of export are we doing?
			FileType outputType;
			if( radCompleteMatcher.isSelected() ) { outputType = FileType.COMPLETE_MATCHER; }
			else if( radMatrixAsCSV.isSelected() ) { outputType = FileType.MATRIX_AS_CSV; }
			else { outputType = FileType.ALIGNMENT_ONLY; } // use Alignment_ONLY as default.
			
			// directory, filename, fileformat ?
			String outDirectory = txtFileDir.getText();
			String outFileName = txtFilename.getText();
			int outFormatIndex = cmbAlignmentFormat.getSelectedIndex();
			
			if(outDirectory.equals("")){
				JOptionPane.showMessageDialog(this, "Select the directory for the output file to proceed.");
				return;
			}
			else if(outFileName.equals("")){
				JOptionPane.showMessageDialog(this, "Insert a name for the output file to proceed");
				return;
			}
			/*else if(outFileName.indexOf(".")!=-1) {
				JOptionPane.showMessageDialog(this, "Insert a file name without Extension");
			}*/
			else{
				// save app preferences.
				prefs.saveExportLastFilename(outFileName);
				prefs.saveExportLastDir(outDirectory);
				prefs.saveExportType(outputType);
				
				// get the currently selected matcher
				List<MatchingTask> list = Core.getInstance().getMatchingTasks();
				int[] rowsIndex = UICore.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
				
				MatchingTask[] matchingTasks = new MatchingTask[rowsIndex.length];

				for( int i = 0; i < rowsIndex.length; i++ ) {
					matchingTasks[i] = list.get(rowsIndex[i]);
				}
				
				AbstractMatcher selectedMatcher = matchingTasks[0].matchingAlgorithm;
				
				if( outputType == FileType.ALIGNMENT_ONLY ) {
					prefs.saveExportAlignmentFormatIndex(outFormatIndex);
					try {
						// append extension
						if( !outFileName.endsWith("." + OutputController.getAlignmentFormatExtension(outFormatIndex)) ) {
							outFileName+= "." + OutputController.getAlignmentFormatExtension(outFormatIndex);
							txtFilename.setText(outFileName);
						}
						
						// full file name						
						String fullFileName = outDirectory+ File.separator +outFileName;
						
						
						
						if( OutputController.getAlignmentFormatExtension(outFormatIndex) == "rdf" ){ // RDF	
							
							OutputController.printDocumentOAEI(fullFileName, matchingTasks[0]);
							Utility.displayMessagePane("File saved successfully.\nLocation: "+fullFileName+"\n", null);
						}
						else{						
							OutputController.printDocument(fullFileName, matchingTasks);
							Utility.displayMessagePane("File saved successfully.\nLocation: "+fullFileName+"\n", null);
						}
						setVisible(false);
						setModal(false);
						dispose();
					}
					catch(Exception e) {
						e.printStackTrace();
						//ok = false;
						JOptionPane.showMessageDialog(this, "Error while saving the file\nTry to change filename or location.");
					}
				} else if( outputType == FileType.MATRIX_AS_CSV ) {
					prefs.saveExportClassesMatrix( radClassesMatrix.isSelected() );
					prefs.saveExportSort(boxSort.isSelected());
					prefs.saveExportIsolines(boxIsolines.isSelected());
					prefs.saveExportSkipZeros(boxSkipZeros.isSelected());
					
					// append extension
					if( !outFileName.endsWith("." + OutputController.getAlignmentFormatExtension(outFormatIndex)) ) {
						outFileName+= "." + OutputController.getAlignmentFormatExtension(outFormatIndex);
						txtFilename.setText(outFileName);
					}
					
					// full file name
					String fullFileName = outDirectory + File.separator + outFileName;
					
					if( radClassesMatrix.isSelected() ) {
						if( selectedMatcher.getClassesMatrix() == null ) {
							// create a new matrix
							if( selectedMatcher.getSourceOntology() == null || selectedMatcher.getTargetOntology() == null ) { 
								throw new Exception("Matcher does not have Source or Target ontologies set.");
							}
							SimilarityMatrix m = new ArraySimilarityMatrix(selectedMatcher.getSourceOntology(), 
																	selectedMatcher.getTargetOntology(), 
																	alignType.aligningClasses);
							if( selectedMatcher.getClassAlignmentSet() == null ) 
								throw new Exception("Matcher does not have a Classes Matrix nor a Classes Alignment Set.  Cannot do anything.");
							
							for( int i = 0; i < selectedMatcher.getClassAlignmentSet().size(); i++ ) {
								am.app.mappingEngine.Mapping currentAlignment = selectedMatcher.getClassAlignmentSet().get(i);
								m.set(currentAlignment.getEntity1().getIndex(), currentAlignment.getEntity2().getIndex(), currentAlignment);
							}
							
							OutputController.saveMatrixAsCSV(m, fullFileName, boxSort.isSelected(), boxIsolines.isSelected(), boxSkipZeros.isSelected());
							
						} else { 
							OutputController.saveMatrixAsCSV(selectedMatcher.getClassesMatrix(), fullFileName, boxSort.isSelected(), boxIsolines.isSelected(), boxSkipZeros.isSelected());
						}
					} else {
						if( selectedMatcher.getPropertiesMatrix() == null ) {
							// create a new matrix
							if( selectedMatcher.getSourceOntology() == null || selectedMatcher.getTargetOntology() == null ) { 
								throw new Exception("Matcher does not have Source or Target ontologies set.");
							}
							SimilarityMatrix m = new ArraySimilarityMatrix(selectedMatcher.getSourceOntology(), 
																	selectedMatcher.getTargetOntology(), 
																	alignType.aligningProperties);
							if( selectedMatcher.getPropertyAlignmentSet() == null ) 
								throw new Exception("Matcher does not have a Properties Matrix nor a Properties Alignment Set.  Cannot do anything.");
							
							for( int i = 0; i < selectedMatcher.getPropertyAlignmentSet().size(); i++ ) {
								am.app.mappingEngine.Mapping currentAlignment = selectedMatcher.getPropertyAlignmentSet().get(i);
								m.set(currentAlignment.getEntity1().getIndex(), currentAlignment.getEntity2().getIndex(), currentAlignment);
							}
							
							OutputController.saveMatrixAsCSV(m, fullFileName, boxSort.isSelected(), boxIsolines.isSelected(), boxSkipZeros.isSelected());
						} else {
							OutputController.saveMatrixAsCSV(selectedMatcher.getPropertiesMatrix(), fullFileName, boxSort.isSelected(), boxIsolines.isSelected(), boxSkipZeros.isSelected());
						}
					}
					Utility.displayMessagePane("File saved successfully.\nLocation: "+fullFileName+"\n", null);
					setVisible(false);
				} else if( outputType == FileType.COMPLETE_MATCHER ) {
					//throw new Exception("Michele, implement this function.");
					String fullFileName = outDirectory+ "/" + outFileName + ".bin";
					FileOutputStream fos = new FileOutputStream(fullFileName);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					selectedMatcher.writeObject(oos);
					oos.flush();
					oos.close();
					Utility.displayMessagePane("File saved successfully.\nLocation: "+fullFileName+"\n", null);
					this.setVisible(false);
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
		else if( prefs.getLastFile().exists() ) { 
			fc = new JFileChooser(prefs.getLastFile()); 
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

	public static void main(String[] args) {
		
		new ExportDialog(null);
		
	}
	
}
