package am.evaluation.disagreement.variance;

import am.evaluation.disagreement.DisagreementParameters;
import am.evaluation.disagreement.DisagreementParametersPanel;

public class VarianceDisagreementPanel extends DisagreementParametersPanel {

	private static final long serialVersionUID = -1236124281785413448L;

	public VarianceDisagreementPanel() {
		super();
	}
	
	@Override
	public String checkParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DisagreementParameters getParameters() {
		VarianceDisagreementParameters p = new VarianceDisagreementParameters();
		return p;
	}

}
