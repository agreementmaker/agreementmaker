package conceptmatcher.internal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.MatcherSetting;
import am.userInterface.AppPreferences;



public class ConceptMatcherParametersPanel extends AbstractMatcherParametersPanel implements ItemListener {

	/**
	 * Concept Similarity Matcher - The Parameters Panel
	 * @author Will Underwood
	 * @date January 17, 2009
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private ConceptMatcherParameters parameters;
	
	private JLabel instructionsLabel;
	private JLabel descLabel;
	private JTextField descField;
	private JLabel descConstraintsLabel;
	private JLabel ancLabel;
	private JTextField ancField;
	private JLabel ancConstraintsLabel;
	private JLabel textLabel;
	private JTextField textField;
	private JLabel textConstraintsLabel;
	
	// Application Wide Preferences
	AppPreferences prefs;
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public ConceptMatcherParametersPanel() {
		super();
		
		prefs = Core.getAppPreferences();  // get a reference to our application preferences
		
		this.setPreferredSize(new Dimension(350, 175) );
		
		instructionsLabel = new JLabel("<html>Please set weights for the similarity components:</html>");
		instructionsLabel.setAlignmentX((float) 0.5);
		
		descLabel = new JLabel("<html>Descendant Set: </html>");
		descConstraintsLabel = new JLabel("<html>( 0.0 \u2264 Descendant Set \u2264 1.0 )</html>");
		descField = new JTextField(6);
		descField.setText(Float.toString(prefs.getPanelFloat( MatcherSetting.CON_DES ))); // get the saved value
		
		ancLabel = new JLabel("<html>Ancestor Set: </html>");
		ancConstraintsLabel = new JLabel("<html>( 0.0 \u2264 Ancestor Set \u2264 1.0 )</html>");
		ancField = new JTextField(6);
		ancField.setText(Float.toString(prefs.getPanelFloat( MatcherSetting.CON_DES ))); // get the saved value
		
		textLabel = new JLabel("<html>Text Similarity: </html>");
		textConstraintsLabel = new JLabel("<html>( 0.0 \u2264 Text Similarity \u2264 1.0 )</html>");
		textField = new JTextField(6);
		textField.setText(Float.toString(prefs.getPanelFloat( MatcherSetting.CON_TXT ))); // get the saved value
		
		parameters = new ConceptMatcherParameters();
		
		
		// The GUI layout - a pain in the butt to get right
		
		this.setLayout(new BorderLayout(30, 30));
	
		JPanel a = new JPanel(new FlowLayout(FlowLayout.CENTER));
		a.add(descLabel);
		a.add(descField);
		a.add(descConstraintsLabel);
		a.add(ancLabel);
		a.add(ancField);
		a.add(ancConstraintsLabel);
		a.add(textLabel);
		a.add(textField);
		a.add(textConstraintsLabel);
		a.setSize(300, 50);
		
		instructionsLabel.setBorder(new EmptyBorder(20, 10, 10, 10));
		JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER));
		b.add(instructionsLabel);
		b.setSize(300,50);
		
	
		this.add(b, BorderLayout.NORTH);
		this.add(a, BorderLayout.CENTER);
		
		this.setSize(300, 100);		
		
	}
	
	
	public DefaultMatcherParameters getParameters() {
		parameters.AncestorSetWeight = Double.parseDouble(ancField.getText());
		parameters.DescendantSetWeight = Double.parseDouble(descField.getText());
		parameters.TextSimilarityWeight = Double.parseDouble(textField.getText());
		return parameters;
	}
	
	public String checkParameters() {
		
		float inputDesc = Float.parseFloat(descField.getText());
		float inputAnc = Float.parseFloat(ancField.getText());
		float inputText = Float.parseFloat(textField.getText());
		
		if( inputDesc < 0.0 || inputDesc > 1.0 )
			return "Descendant Set should be a value between 0.0 and 1.0 (inclusive).  Please change the value to match the constraints."; 
		if (inputAnc < 0.0 || inputAnc > 1.0)
			return "Ancestor Set should be a value between 0.0 and 1.0 (inclusive).  Please change the value to match the constraints."; 
		if (inputText < 0.0 || inputText > 1.0)
			return "Text Similarity should be a value between 0.0 and 1.0 (inclusive).  Please change the value to match the constraints."; 

		// we are going to save the value the user enters, so they don't have to keep entering it.		
		Core.getAppPreferences().savePanelFloat( MatcherSetting.CON_DES , inputDesc);
		Core.getAppPreferences().savePanelFloat( MatcherSetting.CON_ANC , inputAnc);
		Core.getAppPreferences().savePanelFloat( MatcherSetting.CON_TXT , inputText);
		parameters.DescendantSetWeight = inputDesc;
		parameters.AncestorSetWeight = inputAnc;
		parameters.TextSimilarityWeight = inputText;
		
		return null;
	}


	public void itemStateChanged(ItemEvent e) {
		
		//Object source = e.getItemSelectable();
		
		/*if( source == useDictionaryCheckbox ) {
			if( e.getStateChange() == ItemEvent.SELECTED ) {
				parameters.useDictionary = true;
			} else {  // DESELECTED
				parameters.useDictionary = false;
			}
		}*/
		
	}
}