package am.matcher.asm;

import java.awt.event.ItemEvent;

import javax.swing.JCheckBox;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.matcher.bsm.BaseSimilarityMatcherParametersPanel;
import am.matcher.bsm.BaseSimilarityParameters;

public class AdvancedSimilarityMatcherParametersPanel extends
		BaseSimilarityMatcherParametersPanel {

	/**
	 * 
	 */
	
	protected JCheckBox useLabelCheckbox; 
	
	public boolean useLabelinsteaofLocalname = false;
	
	private static final long serialVersionUID = -126546212340501447L;

	public AdvancedSimilarityMatcherParametersPanel() {
		super();
		
		
		
		useLabelCheckbox = new JCheckBox("Use labels instead of localnames.");
		contentBox.add(useLabelCheckbox);
		
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		super.itemStateChanged(e);
		
		Object source = e.getItemSelectable();
		
		if( source == useLabelCheckbox ) {
			if( e.getStateChange() == ItemEvent.SELECTED ) {
				useLabelinsteaofLocalname = true;
			} else {  // DESELECTED
				useLabelinsteaofLocalname = false;
			}
		}
		
	}
	
	@Override
	public DefaultMatcherParameters getParameters() {
		// TODO Auto-generated method stub
		BaseSimilarityParameters bsmp = (BaseSimilarityParameters) super.getParameters();
		
		AdvancedSimilarityParameters asmp = new AdvancedSimilarityParameters();
		
		asmp.useDictionary = bsmp.useDictionary;
		asmp.useLabels = useLabelinsteaofLocalname;
		return asmp;
	}
}
