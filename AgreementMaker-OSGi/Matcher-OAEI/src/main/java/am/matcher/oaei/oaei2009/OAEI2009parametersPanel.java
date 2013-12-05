package am.matcher.oaei.oaei2009;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.utility.AppPreferences;

public class OAEI2009parametersPanel extends AbstractMatcherParametersPanel {

	/**
	 * Base Similarity Matcher - The Parameters Panel
	 * @author Cosmin Stroe
	 * @date Nov 22, 2008
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	@SuppressWarnings("unused")
	private OAEI2009parameters parameters;
	
	private JComboBox<String> trackCombo;
	
	@SuppressWarnings("unused")
	private AppPreferences prefs;
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public OAEI2009parametersPanel() {
		super();
		
		
		this.setPreferredSize(new Dimension(350, 175) );
		
		String[] tracks = {OAEI2009parameters.BENCHMARKS, OAEI2009parameters.ANATOMY, OAEI2009parameters.ANATOMY_PRA, OAEI2009parameters.ANATOMY_PRI, OAEI2009parameters.CONFERENCE, OAEI2009parameters.WordnetNoUMLS, OAEI2009parameters.BENCHMARKS_303_PRA};
		trackCombo = new JComboBox<String>(tracks);		
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
	
	
	public DefaultMatcherParameters getParameters() {
		
		return new OAEI2009parameters((String)trackCombo.getSelectedItem());
		
	}
	
	public String checkParameters() {
		return null;
	}
}
