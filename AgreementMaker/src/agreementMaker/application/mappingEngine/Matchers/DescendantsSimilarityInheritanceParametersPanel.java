package agreementMaker.application.mappingEngine.Matchers;

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
public class DescendantsSimilarityInheritanceParametersPanel extends AbstractMatcherParametersPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3220525418599504107L;

	private DescendantsSimilarityInheritanceParameters parameters;
	
	private JLabel instructionsLabel;
	private JLabel mcpLabel;
	private JTextField mcpField;
	private JLabel constraintsLabel;
	
	// Application Wide Preferences
	AppPreferences prefs;
	
	
	public DescendantsSimilarityInheritanceParametersPanel() {
		super();
	
		prefs = Core.getInstance().getUI().getAppPreferences();  // get a reference to our application preferences
		
		this.setPreferredSize(new Dimension(350, 175) );
		
		instructionsLabel = new JLabel("<html>Please set the initial parameteres for DSI:</html>");
		instructionsLabel.setAlignmentX((float) 0.5);
		
		mcpLabel = new JLabel("<html>MCP: </html>");
		constraintsLabel = new JLabel("<html>( 0.0 ≤ MCP ≤ 1.0 )</html>");
				
		mcpField = new JTextField(6);
		mcpField.setText(    Float.toString(prefs.getPanelFloat( MatcherSetting.DSI_MCP ))    ); // get the saved MCP value (usually the last one the user entered)
		//mcpField.setPreferredSize(new Dimension(200, 40));
		
		parameters = new DescendantsSimilarityInheritanceParameters();
		
		
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
	
	public DescendantsSimilarityInheritanceParameters getParameters() {
		
		return parameters;
		
	}
	
	
	public String checkParameters() {
		
		float inputMCP = Float.parseFloat(mcpField.getText());  // get the MCP value inputed by the user.
		
		if( inputMCP < 0.0 || inputMCP > 1.0 ) {
			return "MCP should be a value between 0.0 and 1.0 (inclusive).  Please change the MCP value to match the constraints."; 
		}

		// we are going to save the value the user enters, so they don't have to keep entering it.		
		Core.getInstance().getUI().getAppPreferences().savePanelFloat( MatcherSetting.DSI_MCP , inputMCP);
		parameters.MCP = inputMCP;  // save our MCP to the parameters.
		
		return null;
	}
	
	
	
}
