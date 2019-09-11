package am.evaluation.disagreement;

import javax.swing.JPanel;

public abstract class DisagreementParametersPanel extends JPanel {

	private static final long serialVersionUID = 6673806906809973695L;

	public abstract String checkParameters();
	public abstract DisagreementParameters getParameters();

}
