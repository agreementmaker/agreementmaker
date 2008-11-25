package agreementMaker.application.mappingEngine.Matchers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.AbstractParameters;

public class BaseSimilarityMatcherParametersPanel extends AbstractMatcherParametersPanel implements ItemListener {

	/**
	 * Base Similarity Matcher - The Parameters Panel
	 * @author Cosmin Stroe
	 * @date Nov 22, 2008
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private BaseSimilarityParameters parameters;
	
	private JLabel useDictionaryLabel;
	private JLabel warningLabel;
	private JCheckBox useDictionaryCheckbox;
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public BaseSimilarityMatcherParametersPanel() {
		super();
		
		
		this.setPreferredSize(new Dimension(350, 175) );
		
		useDictionaryLabel = new JLabel("<html>Would you like to consult a dictionary while performing the Base Similarity Matching ?</html>");
		useDictionaryLabel.setAlignmentX((float) 0.5);
		
		warningLabel = new JLabel("<html><i>Warning: Consulting a dictionary can dramatically increase matching time!</i></html>");
		warningLabel.setAlignmentX((float) 0.5);
		
				
		useDictionaryCheckbox = new JCheckBox("Use Dictionary");
		useDictionaryCheckbox.addItemListener(this);  // when the checkbox toggles, we update our parameters.
		parameters = new BaseSimilarityParameters();
		
		
		// The GUI layout - a pain in the butt to get right
		
		BoxLayout panelLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		
		Box paddingBox = Box.createHorizontalBox();
		
		Box contentBox = Box.createVerticalBox();
		
		contentBox.add(Box.createVerticalStrut(20));
		contentBox.add(useDictionaryLabel);
		contentBox.add(Box.createVerticalStrut(20));
		contentBox.add(warningLabel);
		contentBox.add(Box.createVerticalStrut(20));
		contentBox.add(useDictionaryCheckbox);
		contentBox.add(Box.createVerticalStrut(20));
		
		paddingBox.add(Box.createHorizontalStrut(20));
		paddingBox.add(contentBox);
		paddingBox.add(Box.createHorizontalStrut(20));
		
		
		this.setLayout(panelLayout);
		
		this.add(paddingBox);
		
	}
	
	
	public AbstractParameters getParameters() {
		
		return parameters;
		
	}
	
	public String checkParameters() {
		//If there are any constraints to be satisfied by matcher params
		//check them overriding this method
		//if there are no errors in parameters selected then return null or "", 
		//else return the message to be shown to the user to correct errors
		return null;
	}


	public void itemStateChanged(ItemEvent e) {
		
		Object source = e.getItemSelectable();
		
		if( source == useDictionaryCheckbox ) {
			if( e.getStateChange() == ItemEvent.SELECTED ) {
				parameters.useDictionary = true;
			} else {  // DESELECTED
				parameters.useDictionary = false;
			}
		}
		
	}
}
