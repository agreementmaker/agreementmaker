package am.app.mappingEngine.oaei.oaei2011;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.oaei.OAEI_Track;
import am.userInterface.AppPreferences;

/**
 * Base Similarity Matcher - The Parameters Panel
 * @author Cosmin Stroe
 * @date Nov 22, 2008
 * ADVIS @ UIC
 */

public class OAEI2011MatcherParametersPanel extends AbstractMatcherParametersPanel {

	private static final long serialVersionUID = -7652636660460034435L;

	@SuppressWarnings("unused")
	private OAEI2011MatcherParameters parameters;
	
	private JComboBox trackCombo;
	//private JCheckBox chkUseExtractedTermSynonyms = new JCheckBox("Use extracted term synonyms.");
	
	@SuppressWarnings("unused")
	private AppPreferences prefs;
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public OAEI2011MatcherParametersPanel() {
		super();
		
		this.setPreferredSize(new Dimension(350, 175) );
					
	}
	
		
	public AbstractParameters getParameters() {
		
		return new OAEI2011MatcherParameters();
		
	}
	
	public String checkParameters() {
		return null;
	}
}
