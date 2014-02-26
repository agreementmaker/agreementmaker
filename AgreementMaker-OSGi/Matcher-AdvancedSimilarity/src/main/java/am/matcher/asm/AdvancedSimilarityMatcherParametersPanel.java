package am.matcher.asm;

import javax.swing.JCheckBox;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.matcher.bsm.BaseSimilarityMatcherParametersPanel;

public class AdvancedSimilarityMatcherParametersPanel extends BaseSimilarityMatcherParametersPanel {

	protected JCheckBox useLabelCheckbox; 

	private static final long serialVersionUID = -126546212340501447L;

	public AdvancedSimilarityMatcherParametersPanel() {
		super();

		useLabelCheckbox = new JCheckBox("Use labels instead of localnames.");
		contentBox.add(useLabelCheckbox);
	}
	
	@Override
	public DefaultMatcherParameters getParameters() {
		AdvancedSimilarityParameters asmp = new AdvancedSimilarityParameters();
		asmp.useLabel = useLabelCheckbox.isSelected();
		return asmp;
	}
}
