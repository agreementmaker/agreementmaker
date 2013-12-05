package am.evaluation.clustering;

import javax.swing.JPanel;

public abstract class ClusteringParametersPanel extends JPanel {
	
	private static final long serialVersionUID = -8492812525205151749L;

	public abstract ClusteringParameters getParameters();
	public abstract String checkParameters(); // return null if parameters have been entered correctly, otherwise an error message string.
}
