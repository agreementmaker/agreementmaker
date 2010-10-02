package am.app.mappingEngine.oaei2010;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

import com.sun.corba.se.impl.ior.NewObjectKeyTemplateBase;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.userInterface.AppPreferences;
import am.app.mappingEngine.oaei2010.OAEI2010parameters;

public class OAEI2010parametersPanel extends AbstractMatcherParametersPanel {

	/**
	 * Base Similarity Matcher - The Parameters Panel
	 * @author Cosmin Stroe
	 * @date Nov 22, 2008
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652637770460034435L;

	@SuppressWarnings("unused")
	private OAEI2010parameters parameters;
	
	private JComboBox trackCombo;
	
	@SuppressWarnings("unused")
	private AppPreferences prefs;
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public OAEI2010parametersPanel() {
		super();
		
		parameters = new OAEI2010parameters();
		
		this.setPreferredSize(new Dimension(350, 175) );
		
		String[] tracks = {OAEI2010parameters.ANATOMY, OAEI2010parameters.BENCHMARKS1, OAEI2010parameters.BENCHMARKS2, OAEI2010parameters.CONFERENCE1, OAEI2010parameters.CONFERENCE2};
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
		
		return new OAEI2010parameters((String)trackCombo.getSelectedItem());
		
	}
	
	public String checkParameters() {
		return null;
	}
}
