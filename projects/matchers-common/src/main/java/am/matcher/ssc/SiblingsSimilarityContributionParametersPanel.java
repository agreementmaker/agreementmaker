package am.matcher.ssc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.MatcherSetting;
import am.utility.AppPreferences;

public class SiblingsSimilarityContributionParametersPanel extends AbstractMatcherParametersPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1493894247209728294L;
	
	
	private SiblingsSimilarityContributionParameters parameters;
	
	private JLabel instructionsLabel;
	private JLabel mcpLabel;
	private JTextField mcpField;
	private JLabel constraintsLabel;
	
	// Application Wide Preferences
	AppPreferences prefs;
	
	
	public SiblingsSimilarityContributionParametersPanel() {
		// TODO Auto-generated constructor stub
		super();
	
		prefs = Core.getAppPreferences();  // get a reference to our application preferences
		
		this.setPreferredSize(new Dimension(350, 175) );
		
		instructionsLabel = new JLabel("<html>Please set the initial parameteres for SSC:</html>");
		instructionsLabel.setAlignmentX((float) 0.5);
		
		mcpLabel = new JLabel("<html>MCP: </html>");
		
		constraintsLabel = new JLabel("<html>( 0.0 \u2264 MCP \u2264 1.0 )</html>");
				
		mcpField = new JTextField(6);
		mcpField.setText(    Float.toString(prefs.getPanelFloat( MatcherSetting.SSC_MCP ))    ); // get the saved MCP value (usually the last one the user entered)
		//mcpField.setPreferredSize(new Dimension(200, 40));
		
		parameters = new SiblingsSimilarityContributionParameters();
		
		
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
	
	public SiblingsSimilarityContributionParameters getParameters() {
		parameters.MCP = Double.parseDouble(mcpField.getText());
		return parameters;
		
	}
	
	
	public String checkParameters() {
		
		float inputMCP = Float.parseFloat(mcpField.getText());  // get the MCP value inputed by the user.
		
		if( inputMCP < 0.0 || inputMCP > 1.0 ) {
			return "MCP should be a value between 0.0 and 1.0 (inclusive).  Please change the MCP value to match the constraints."; 
		}

		// we are going to save the value the user enters, so they don't have to keep entering it.		
		Core.getAppPreferences().savePanelFloat( MatcherSetting.SSC_MCP , inputMCP);
		parameters.MCP = inputMCP;  // save our MCP to the parameters.
		
		return null;
	}
	
	

}
