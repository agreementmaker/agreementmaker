package am.app.mappingEngine.oaei2010;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.oaei2010.OAEI2010MatcherParameters.Track;
import am.userInterface.AppPreferences;

/**
 * Base Similarity Matcher - The Parameters Panel
 * @author Cosmin Stroe
 * @date Nov 22, 2008
 * ADVIS @ UIC
 */

public class OAEI2010MatcherParametersPanel extends AbstractMatcherParametersPanel {

	private static final long serialVersionUID = -7652636660460034435L;

	@SuppressWarnings("unused")
	private OAEI2010MatcherParameters parameters;
	
	private JComboBox trackCombo;
	
	@SuppressWarnings("unused")
	private AppPreferences prefs;
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public OAEI2010MatcherParametersPanel() {
		super();
		
		
		this.setPreferredSize(new Dimension(350, 175) );
		
		Track[] tracks = {  OAEI2010MatcherParameters.Track.Anatomy, 
						     OAEI2010MatcherParameters.Track.Benchmarks, 
						     OAEI2010MatcherParameters.Track.Conference,
						     OAEI2010MatcherParameters.Track.AllMatchers };
		trackCombo = new JComboBox(tracks);		
		trackCombo.setAlignmentX((float) 0.5);
		
		BoxLayout panelLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		
		Box paddingBox = Box.createHorizontalBox();
		
		Box contentBox = Box.createVerticalBox();
		
		contentBox.add(Box.createVerticalStrut(20));
		contentBox.add(trackCombo);
		paddingBox.add(Box.createHorizontalStrut(20));
		paddingBox.add(contentBox);
		paddingBox.add(Box.createHorizontalStrut(20));
		
		this.setLayout(panelLayout);
		
		this.add(paddingBox);
		
	}
	
		
	public AbstractParameters getParameters() {
		
		return new OAEI2010MatcherParameters((Track)trackCombo.getSelectedItem());
		
	}
	
	public String checkParameters() {
		return null;
	}
}
