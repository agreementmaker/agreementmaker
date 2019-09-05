package am.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.InputMap;
import javax.swing.JButton;
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

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.utility.CopySelection;
import am.parsing.OutputController;
import am.parsing.OutputController.ImportAlignmentFormats;
import am.utility.AppPreferences;
import am.utility.AppPreferences.FileType;



public class ImportDialog extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = 7313974994418768939L;
	AppPreferences prefs = new AppPreferences(); // Application preferences.  Used to automatically fill in previous values.

	private JLabel lblFilename, lblFileFormat;
	private JButton btnBrowse, btnCancel, btnLoad;
	private JTextField txtFilename;
	private JComboBox cmbAlignmentFormat;
	private JPanel pnlSaveOptions;
	//private JCheckBox boxSort, boxIsolines, boxSkipZeros;
	
	private JRadioButton radAlignmentOnly, radCompleteMatcher;//, radMatrixAsCSV, radCompleteMatcher, radClassesMatrix, radPropertiesMatrix;
	
	private AbstractMatcher loadedMatcher;
	
	/**
	 * Helper function to create the export options panel.
	 * @return The panel in the dialog that contains the radio buttons of what should be saved.
	 */
	private JPanel getLoadOptionsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Import: "));
		
		// create the radio buttons (using the preferences to set the options)
		radAlignmentOnly = new JRadioButton("Alignment only", prefs.isExportTypeSelected( FileType.ALIGNMENT_ONLY ));
		radAlignmentOnly.setActionCommand( FileType.ALIGNMENT_ONLY.getKey() );
		radAlignmentOnly.addActionListener(this);
		/*radMatrixAsCSV = new JRadioButton("Similarity matrix as CSV", prefs.isExportTypeSelected( FileType.MATRIX_AS_CSV ));
		radMatrixAsCSV.setActionCommand( FileType.MATRIX_AS_CSV.getKey() );
		radMatrixAsCSV.addActionListener(this); */
		radCompleteMatcher = new JRadioButton("Complete Matcher", prefs.isExportTypeSelected( FileType.COMPLETE_MATCHER ) );
		radCompleteMatcher.setActionCommand( FileType.COMPLETE_MATCHER.getKey() );
		radCompleteMatcher.addActionListener(this);
		/*
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
		*/
		// create a button group for the radio buttons
		ButtonGroup grpType = new ButtonGroup();
		grpType.add(radAlignmentOnly);
		grpType.add(radCompleteMatcher);
/*		grpType.add(radMatrixAsCSV); 
		
		ButtonGroup grpMatrix = new ButtonGroup();
		grpMatrix.add(radClassesMatrix);
		grpMatrix.add(radPropertiesMatrix);*/
		
		// we need to create a subpanel to group the "Alignment only" radio button with the format label and combobox.
		lblFileFormat = new JLabel("Format: ");
		
		cmbAlignmentFormat = new JComboBox( OutputController.getImportAlignmentFormatDescriptionList() );
		try {
			cmbAlignmentFormat.setSelectedIndex( prefs.getImportAlignmentFormatIndex() );
		} catch( IllegalArgumentException e ) {
			// index out of bounds.
			if( cmbAlignmentFormat.getItemCount() > 0 ) cmbAlignmentFormat.setSelectedIndex(0);
		}
		
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
				/*.addGroup( layMatrices.createParallelGroup()
						.addComponent(radClassesMatrix)
						.addComponent(radPropertiesMatrix)
				)
				.addGap(10)
				.addGroup( layMatrices.createParallelGroup()
						.addComponent(boxSort)
						.addComponent(boxIsolines)
						.addComponent(boxSkipZeros)
				)
				.addGap(30)*/
		);
		layMatrices.setVerticalGroup(  layMatrices.createParallelGroup()
				/*.addGroup(layMatrices.createSequentialGroup()
						.addComponent(radClassesMatrix)
						.addComponent(radPropertiesMatrix)
				)
				.addGroup( layMatrices.createSequentialGroup()
						.addComponent(boxSort)
						.addComponent(boxIsolines)
						.addComponent(boxSkipZeros)
				)*/
		);
		pnlMatrices.setLayout(layMatrices);
		
		
		GroupLayout layMain = new GroupLayout(panel);
		layMatrices.setAutoCreateGaps(true);
		
		// TODO: Enable all commented out options when they work.
		layMain.setHorizontalGroup( layMain.createParallelGroup()
				.addComponent(radAlignmentOnly)
				.addComponent(pnlAlignmentFormat)
				//.addComponent(radMatrixAsCSV)
				//.addComponent(pnlMatrices)
				.addComponent(radCompleteMatcher)
		);
		
		layMain.setVerticalGroup( layMain.createSequentialGroup() 
				.addComponent(radAlignmentOnly)
				.addComponent(pnlAlignmentFormat)
				//.addGap(10)
				//.addComponent(radMatrixAsCSV)
				//.addComponent(pnlMatrices)
				.addGap(10)
				.addComponent(radCompleteMatcher)				
		);
		
		panel.setLayout(layMain);
		return panel;
	}

	/**
	 * This is the dialog that is shown when the Export menu item is used.
	 * It can save Alignments or Similarity Matrices of Matchers, or complete Matchers (for later Import).
	 */
	public ImportDialog() {
		super(UICore.getUI().getUIFrame(), true);
		
		loadedMatcher = null;
		
		// 
		setTitle("Import ...");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// elements of the dialog (in order from left to right, top to bottom)
		//lblMatcher = new JLabel("Importing Matcher: ");
		
		lblFilename = new JLabel("File: ");
		txtFilename = new JTextField();
		
		txtFilename.setPreferredSize(new Dimension(500, lblFilename.getPreferredSize().height));
		
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(this);
		
		// Restore some values saved 
		//the system suggests the last file opened
		txtFilename.setText(prefs.getImportLastFilename());		
		pnlSaveOptions = getLoadOptionsPanel();
		
		btnCancel = new JButton("Cancel");
		btnLoad = new JButton("Load");
		
		btnCancel.addActionListener(this);
		btnLoad.addActionListener(this);
				
		getRootPane().setDefaultButton(btnLoad);
		
		//Make the GroupLayout for this dialog (somewhat complicated, but very flexible)
		// This Group layout lays the items in relation with eachother.  The horizontal
		// and vertical groups decide the relation between UI elements.
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup( layout.createParallelGroup(Alignment.TRAILING)
				//.addComponent(lblMatcher, Alignment.LEADING)
				.addGroup( layout.createSequentialGroup()
						.addComponent(lblFilename)
						.addComponent(txtFilename)
						.addComponent(btnBrowse)
				)
				.addComponent(pnlSaveOptions, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 2000 )
				.addGroup( layout.createSequentialGroup()
						.addComponent(btnCancel)
						.addComponent(btnLoad)
				)
		);

		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged		
		layout.setVerticalGroup( layout.createSequentialGroup()
				//.addComponent(lblMatcher)
				//.addGap(10)
				.addGroup( layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblFilename)
						.addComponent(txtFilename)
						.addComponent(btnBrowse)
				)
				.addComponent(pnlSaveOptions)
				.addGroup( layout.createParallelGroup(Alignment.CENTER)
						.addComponent(btnCancel)
						.addComponent(btnLoad)
				)
		);
		
		// end of Layout Code
		
		// some ease of use code
		setAlignmentOnlyEnabled( radAlignmentOnly.isSelected() );		
		//setMatrixRadioButtonsEnable( radMatrixAsCSV.isSelected() );
	
		pack(); // automatically set the frame size
		setLocationRelativeTo(null); 	// center the window on the screen
		setVisible(true);
	}
	
	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
	    JRootPane rootPane = new JRootPane();
	    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
	    Action actionListener = new AbstractAction() {
	    	private static final long serialVersionUID = -8276043191093337721L;
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
		else if(obj == btnLoad){
			load();
		}/* else if( obj == radAlignmentOnly || obj == radMatrixAsCSV || obj == radCompleteMatcher ) {
			// enables/disables controls depending on which radio button is selected
			setAlignmentOnlyEnabled(radAlignmentOnly.isSelected());
			setMatrixRadioButtonsEnable(radMatrixAsCSV.isSelected());	
		}*/
	}
	
	private void setAlignmentOnlyEnabled( boolean en ) {
		lblFileFormat.setEnabled(en);
		cmbAlignmentFormat.setEnabled(en);
	}
	/*private void setMatrixRadioButtonsEnable( boolean en ) {
		radClassesMatrix.setEnabled(en);
		radPropertiesMatrix.setEnabled(en);
		boxSort.setEnabled(en);
		boxIsolines.setEnabled(en);
		boxSkipZeros.setEnabled(en);
	}*/
	
	private void load() {
		try {
			// what kind of import are we doing?
			FileType inputType = FileType.ALIGNMENT_ONLY;
/*			if( radCompleteMatcher.isSelected() ) { inputType = FileType.COMPLETE_MATCHER; }
			else if( radMatrixAsCSV.isSelected() ) { inputType = FileType.MATRIX_AS_CSV; }
			else { inputType = FileType.ALIGNMENT_ONLY; } // use Alignment_ONLY as default.
*/			
			// directory, filename, fileformat ?
			String inFileName = txtFilename.getText();
			
			if(inFileName.equals("")){
				JOptionPane.showMessageDialog(this, "Select an input file to proceed");
				return;
			}
			else{
				// save app preferences.
				prefs.saveImportLastFilename(inFileName);
				prefs.saveImportType(inputType);
				
				if( inputType == FileType.ALIGNMENT_ONLY ) {
					// TODO: Make this work with other types of outputs.
					int lastIndex = Core.getInstance().getMatcherInstances().size();
					final AbstractMatcher referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
					
					ReferenceAlignmentParameters refParams = new ReferenceAlignmentParameters();
					
					refParams.fileName = inFileName;  // the path of the input file name as a String
					
					int selectedIndex = cmbAlignmentFormat.getSelectedIndex();
					ImportAlignmentFormats[] array = ImportAlignmentFormats.values();
					switch( array[selectedIndex] ) {
					case RDF:
						refParams.format = ReferenceAlignmentMatcher.OAEI;  // OAEI format
						break;
					case TABBEDTEXT:
						refParams.format = ReferenceAlignmentMatcher.REF2a;
						break;
					}
					referenceAlignmentMatcher.setParameters(refParams);
					referenceAlignmentMatcher.setSourceOntology(Core.getInstance().getSourceOntology()); // set the source ontology
					referenceAlignmentMatcher.setTargetOntology(Core.getInstance().getTargetOntology()); // set the target ontology
					
					referenceAlignmentMatcher.getParam().threshold = referenceAlignmentMatcher.getDefaultThreshold();
					referenceAlignmentMatcher.setMaxSourceAlign(referenceAlignmentMatcher.getDefaultMaxSourceRelations());
					referenceAlignmentMatcher.setMaxTargetAlign(referenceAlignmentMatcher.getDefaultMaxTargetRelations());
					
					final MatchingTask t = new MatchingTask(referenceAlignmentMatcher, referenceAlignmentMatcher.getParam(), 
							new CopySelection(), new DefaultSelectionParameters());

					
					/*referenceAlignmentMatcher.addProgressDisplay(new MatchingProgressDisplay() {
						private boolean ignore = false;
						@Override public void setProgressLabel(String label) {}
						@Override public void setIndeterminate(boolean indeterminate) {}
						@Override public void scrollToEndOfReport() {}
						@Override public void propertyChange(PropertyChangeEvent evt) {}
						@Override public void matchingStarted(AbstractMatcher m) {}
						@Override synchronized public void matchingComplete() {
							if( ignore ) return;
							this.ignore = true;
							if(!referenceAlignmentMatcher.isCancelled()) {  // If the algorithm finished successfully, add it to the control panel.
								SelectionResult selR = new SelectionResult();
								selR.classesAlignment = referenceAlignmentMatcher.getClassAlignmentSet();
								selR.propertiesAlignment = referenceAlignmentMatcher.getPropertyAlignmentSet();
								t.selectionResult = selR;
								Core.getInstance().addMatchingTask(t);
							}
						}
						
						@Override public void ignoreComplete(boolean ignore) {this.ignore = ignore;}
						@Override public void clearReport() {}
						@Override public void appendToReport(String report) {}
					});*/
					
					new MatcherProgressDialog(t);
					
					loadedMatcher = referenceAlignmentMatcher;
					setVisible(false);
					dispose();
				}
				else if( inputType == FileType.MATRIX_AS_CSV ) {
					// TODO: Implement this.
					setVisible(false);
					dispose();
				}
				else if ( inputType == FileType.COMPLETE_MATCHER ) {
					FileInputStream fis = new FileInputStream(inFileName);
					ObjectInputStream in = new ObjectInputStream(fis);
					AbstractMatcher m = (AbstractMatcher)in.readObject();
					in.close();
					
					//TODO: Fix serialization of matching tasks
					//Core.getInstance().addMatcherInstance(m);
					
					setVisible(false);
					dispose();
				}
				else {
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
		
		File lastDir = new File(prefs.getImportLastDirectory());
		
		if(lastDir.exists()) {  
			fc = new JFileChooser(lastDir); 
		} 	 
		else {
			fc = new JFileChooser();
		}

		int returnVal = fc.showOpenDialog(this);

		if( returnVal == JFileChooser.APPROVE_OPTION ) {
			File selectedfile = fc.getSelectedFile();	
			// ok, now that we know what file the user selected
			// let's save it for future use (for the chooser)
			prefs.saveImportLastDirectory(selectedfile.getAbsolutePath()); 
			txtFilename.setText(selectedfile.getAbsolutePath());
		}
	}

	public void setLoadedMatcher(AbstractMatcher loadedMatcher) {
		this.loadedMatcher = loadedMatcher;
	}

	public AbstractMatcher getLoadedMatcher() {
		return loadedMatcher;
	}
	
}

