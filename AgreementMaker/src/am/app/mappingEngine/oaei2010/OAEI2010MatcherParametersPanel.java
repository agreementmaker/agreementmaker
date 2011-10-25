package am.app.mappingEngine.oaei2010;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.oaei.OAEI_Track;
import am.app.ontology.Ontology;
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
	//private JCheckBox chkUseExtractedTermSynonyms = new JCheckBox("Use extracted term synonyms.");
	
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
		
		
		// setup the checkbox for using extracted term synonyms.
		//Ontology sourceOnt = Core.getInstance().getSourceOntology();
		//Ontology targetOnt = Core.getInstance().getTargetOntology();
		
		//if( Core.getLexiconStore().getLexicon(, type))
		
		
		OAEI_Track[] tracks = {  OAEI_Track.Anatomy, 
							OAEI_Track.Benchmarks, 
							OAEI_Track.Conference,
							OAEI_Track.AllMatchers };
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
		
		return new OAEI2010MatcherParameters((OAEI_Track)trackCombo.getSelectedItem());
		
	}
	
	public String checkParameters() {
		return null;
	}
}
