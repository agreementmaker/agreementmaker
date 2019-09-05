package am.matcher.mediatingMatcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.utility.AMFileChooser;

public class MediatingMatcherParametersPanel extends
		AbstractMatcherParametersPanel implements ActionListener {

	/*
	 * 
	 * UI Elements with their variable assignments:
	 * 
	 * Mediating Ontology: [_________________________] [...]
	 *   labels[0]             textfields[0]            buttons[0]
	 *   
	 * [] Source ontology bridge: [__________________] [...]
	 *   checkboxes[0]               textfields[1]      buttons[1]
	 *   
	 * [] Target ontology bridge: [__________________] [...]
	 *   checkboxes[0]               textfields[2]      buttons[2]
	 */
	
	private static final long serialVersionUID = -2541854106782962184L;

	private JLabel[] labels;
	private JCheckBox[] checkboxes;
	private JTextField[] textfields;
	private JButton[] buttons;
	
	private static final String PREF_LASTFILE = "PREF_LASTFILE";
	private static final String PREF_LASTFILTER = "PREF_LASTFILTER";
	
	public MediatingMatcherParametersPanel() {
		
		// Initialize variables;
		labels = new JLabel[1];
		checkboxes = new JCheckBox[2];
		textfields = new JTextField[3];
		buttons = new JButton[3];
		
		labels[0] = new JLabel("Mediating Ontology:");
		
		checkboxes[0] = new JCheckBox("Source ontology bridge:");
		checkboxes[1] = new JCheckBox("Target ontology bridge:");
		
		textfields[0] = new JTextField();
		textfields[1] = new JTextField();
		textfields[2] = new JTextField();
		
		buttons[0] = new JButton("...");
		buttons[1] = new JButton("...");
		buttons[2] = new JButton("...");
		
		
		checkboxes[0].setSelected(false);
		checkboxes[1].setSelected(false);
		
		textfields[1].setEnabled(false);
		textfields[2].setEnabled(false);
		
		buttons[1].setEnabled(false);
		buttons[2].setEnabled(false);
		
		// Action listeners.
		checkboxes[0].addActionListener(this);
		checkboxes[1].addActionListener(this);
		
		buttons[0].addActionListener(this);
		buttons[1].addActionListener(this);
		buttons[2].addActionListener(this);
		
		// Create the layout.
		GroupLayout lay = new GroupLayout(this);
		
		lay.setAutoCreateContainerGaps(true);
		lay.setAutoCreateGaps(true);
		
		lay.setHorizontalGroup( lay.createParallelGroup()
				.addGroup( lay.createSequentialGroup()
						.addComponent( labels[0] )
						.addComponent( textfields[0] )
						.addComponent( buttons[0] )
				)
				
				.addGroup( lay.createSequentialGroup()
						.addComponent( checkboxes[0] )
						.addComponent( textfields[1] )
						.addComponent( buttons[1] )
				)
				
				.addGroup( lay.createSequentialGroup()
						.addComponent( checkboxes[1] )
						.addComponent( textfields[2] )
						.addComponent( buttons[2] )
				)
		);
		
		lay.setVerticalGroup( lay.createSequentialGroup()
				.addGroup( lay.createParallelGroup()
						.addComponent( labels[0] )
						.addComponent( textfields[0] )
						.addComponent( buttons[0] )
				)
				
				.addGroup( lay.createParallelGroup()
						.addComponent( checkboxes[0] )
						.addComponent( textfields[1] )
						.addComponent( buttons[1] )
				)
				
				.addGroup( lay.createParallelGroup()
						.addComponent( checkboxes[1] )
						.addComponent( textfields[2] )
						.addComponent( buttons[2] )
				)
		);
	
		this.setLayout(lay);
		
		
	}
	
	@Override
	public String checkParameters() {
		if( textfields[0].getText() == null || textfields[0].getText().trim().isEmpty() ) {
			return "You must select a mediating ontology.";
		}
		
		if( checkboxes[0].isSelected() && 
			(textfields[1].getText() == null || textfields[1].getText().trim().isEmpty()) )
			return "You have selected to load a source bridge, but the source bridge text field is empty.  Please select a source bridge file.";
		
		if( checkboxes[1].isSelected() && 
				(textfields[2].getText() == null || textfields[2].getText().trim().isEmpty()) )
				return "You have selected to load a target bridge, but the target bridge text field is empty.  Please select a target bridge file.";

		
		return null;
	}
	
	@Override
	public DefaultMatcherParameters getParameters() {
		MediatingMatcherParameters param = new MediatingMatcherParameters();
		
		param.mediatingOntology = textfields[0].getText();
		
		param.loadSourceBridge = checkboxes[0].isSelected();
		if( param.loadSourceBridge ) param.sourceBridge = textfields[1].getText();
		
		param.loadTargetBridge = checkboxes[1].isSelected();
		if( param.loadTargetBridge ) param.targetBridge = textfields[2].getText();
		
		return param;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == checkboxes[0] ) {
			if( checkboxes[0].isSelected() ) {
				textfields[1].setEnabled(true);
				buttons[1].setEnabled(true);
			} else {
				textfields[1].setEnabled(false);
				buttons[1].setEnabled(false);
			}
		}
		
		if( e.getSource() == checkboxes[1] ) {
			if( checkboxes[1].isSelected() ) {
				textfields[2].setEnabled(true);
				buttons[2].setEnabled(true);
			} else {
				textfields[2].setEnabled(false);
				buttons[2].setEnabled(false);
			}
		}
		
		if( e.getSource() == buttons[0] ) {
			chooseFile( textfields[0] );
		}
		
		if( e.getSource() == buttons[1] ) {
			chooseFile( textfields[1] );
		}
		
		if( e.getSource() == buttons[2] ) {
			chooseFile( textfields[2] );
		}
		
	}
	
	private void chooseFile( JTextField textField ) {
		Preferences localPrefs = Preferences.userNodeForPackage(this.getClass());
		
		File lastSelectedFile = new File( localPrefs.get(PREF_LASTFILE , "."));
		int lastSelectedFilter = localPrefs.getInt(PREF_LASTFILTER, -1);
		AMFileChooser fc = new AMFileChooser(lastSelectedFile, lastSelectedFilter);

		int retVal = fc.showOpenDialog(textField);
		
		if( retVal == JFileChooser.APPROVE_OPTION ) {
			File selectedFile = fc.getSelectedFile();

			// ok, now that we know what file the user selected
			// let's save it for future use (for the chooser)
			localPrefs.put(PREF_LASTFILE, selectedFile.getAbsolutePath());
			localPrefs.putInt(PREF_LASTFILTER, fc.getFileFilterIndex());
			textField.setText(selectedFile.getPath());
		}
	}

}
