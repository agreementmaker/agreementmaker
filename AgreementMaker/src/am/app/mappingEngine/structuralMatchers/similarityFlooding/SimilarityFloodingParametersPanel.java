package am.app.mappingEngine.structuralMatchers.similarityFlooding;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.userInterface.AppPreferences;

public class SimilarityFloodingParametersPanel extends AbstractMatcherParametersPanel implements ItemListener {

	/**
	 * Similarity Flooding Algorithm - The Parameters Panel
	 * (copied from BaseSimilarityParametersPanel)
	 * @author Cosmin Stroe
	 * @date Dec 1, 2010
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private SimilarityFloodingMatcherParameters parameters;
	
	private JCheckBox chkAnonymous;
	
	protected AppPreferences prefs;
	
	protected Box contentBox;
	
	// AppPreferences parameter strings
	public static final String SFA_OMIT_ANONYMOUS = "SFA_OMIT_ANONYMOUS";
	
	public SimilarityFloodingParametersPanel() {
		super();
		
		prefs = Core.getAppPreferences();
		parameters = new SimilarityFloodingMatcherParameters();
		
		chkAnonymous = new JCheckBox("Omit anonymous nodes.");
		parameters.omitAnonymousNodes = prefs.getPanelBool( SFA_OMIT_ANONYMOUS, false );  // get the saved setting
		chkAnonymous.setSelected( parameters.omitAnonymousNodes ); // update the checkbox
		chkAnonymous.addItemListener(this);  // when the checkbox toggles, we update our parameters.
		
		// TODO: Update this layout. (don't use BoxLayout, use GroupLayout instead)
		BoxLayout panelLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		
		Box paddingBox = Box.createHorizontalBox();
		
		contentBox = Box.createVerticalBox();
		
		contentBox.add(Box.createVerticalStrut(20));
		contentBox.add(chkAnonymous);
		contentBox.add(Box.createVerticalStrut(20));
		
		paddingBox.add(Box.createHorizontalStrut(20));
		paddingBox.add(contentBox);
		paddingBox.add(Box.createHorizontalStrut(20));
		
		this.setLayout(panelLayout);
		
		this.add(paddingBox);
		
	}
	
	@Override
	public AbstractParameters getParameters() { return parameters; }
	
	@Override
	public String checkParameters() {
		prefs.savePanelBool( SFA_OMIT_ANONYMOUS, parameters.omitAnonymousNodes );
		return null;  // everything is OK
	}


	public void itemStateChanged(ItemEvent e) {
		
		Object source = e.getItemSelectable();
		
		if( source == chkAnonymous ) {
			if( e.getStateChange() == ItemEvent.SELECTED ) {
				parameters.omitAnonymousNodes = true;
			} else {  // DESELECTED
				parameters.omitAnonymousNodes = false;
			}
		}
		
	}
}
