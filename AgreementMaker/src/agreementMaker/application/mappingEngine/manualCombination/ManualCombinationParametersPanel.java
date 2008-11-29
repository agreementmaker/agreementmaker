package agreementMaker.application.mappingEngine.manualCombination;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.AbstractParameters;
import agreementMaker.application.mappingEngine.MatcherSetting;
import agreementMaker.userInterface.AppPreferences;


/**
 * This is the preferences panel for the DSI algorithm.
 * The user can set the MCP from this panel.
 * @author cosmin
 * @date Nov 25, 2008
 *
 */
public class ManualCombinationParametersPanel extends AbstractMatcherParametersPanel {
  // TO BE DONE YET
	/**
	 * 
	 */
	private static final long serialVersionUID = -3220525418599504107L;

	private ManualCombinationParameters parameters;
	
	private JLabel instructionsLabel;
	private JLabel mcpLabel;
	private JTextField mcpField;
	private JLabel constraintsLabel;
	
	// Application Wide Preferences
	AppPreferences prefs;
	
	
	public ManualCombinationParametersPanel() {
		super();
	
		prefs = Core.getInstance().getUI().getAppPreferences();  // get a reference to our application preferences
		
		this.setPreferredSize(new Dimension(350, 175) );
		
		instructionsLabel = new JLabel("<html>Please set the initial parameteres for DSI:</html>");
		instructionsLabel.setAlignmentX((float) 0.5);
		
		mcpLabel = new JLabel("<html>MCP: </html>");
		
		constraintsLabel = new JLabel("<html>( 0.0 \u2264 MCP \u2264 1.0 )</html>");
				
		mcpField = new JTextField(6);
		mcpField.setText(    Float.toString(prefs.getPanelFloat( MatcherSetting.DSI_MCP ))    ); // get the saved MCP value (usually the last one the user entered)
		//mcpField.setPreferredSize(new Dimension(200, 40));
		
		parameters = new ManualCombinationParameters();
		
		
		// The GUI layout - a pain in the butt to get right
		
		this.setLayout(new BorderLayout(30, 30));
	
		JPanel a = new JPanel(new FlowLayout(FlowLayout.CENTER));
		a.add(mcpLabel);
		a.add(mcpField);
		a.add(constraintsLabel);
		a.setSize(300, 50);
		
		instructionsLabel.setBorder(new EmptyBorder(20, 10, 10, 10));
		JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER));
		b.add(instructionsLabel);
		b.setSize(300,50);
		
	
		this.add(b, BorderLayout.NORTH);
		this.add(a, BorderLayout.CENTER);
		
		this.setSize(300, 100);		
	}
	
	public ManualCombinationParameters getParameters() {
		
		return parameters;
		
	}

	
	
	
}
