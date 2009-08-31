package am.application.mappingEngine.oaei2009;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import am.application.Core;
import am.application.mappingEngine.AbstractMatcherParametersPanel;
import am.application.mappingEngine.AbstractParameters;
import am.application.mappingEngine.MatcherSetting;
import am.application.mappingEngine.Combination.CombinationParameters;
import am.batchMode.TrackDispatcher;
import am.userInterface.AppPreferences;

public class OAEI2009parametersPanel extends AbstractMatcherParametersPanel {

	/**
	 * Base Similarity Matcher - The Parameters Panel
	 * @author Cosmin Stroe
	 * @date Nov 22, 2008
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private OAEI2009parameters parameters;
	
	private JComboBox trackCombo;
	
	private AppPreferences prefs;
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public OAEI2009parametersPanel() {
		super();
		
		
		this.setPreferredSize(new Dimension(350, 175) );
		
		String[] tracks = {OAEI2009parameters.BENCHMARKS, OAEI2009parameters.ANATOMY, OAEI2009parameters.ANATOMY_PRA, OAEI2009parameters.CONFERENCE, OAEI2009parameters.WordnetNoUMLS};
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
		
		return new OAEI2009parameters((String)trackCombo.getSelectedItem());
		
	}
	
	public String checkParameters() {
		return null;
	}
}
