package am.evaluation.clustering.localByThreshold;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import am.evaluation.clustering.ClusteringParameters;
import am.evaluation.clustering.ClusteringParametersPanel;


public class LocalByThresholdPanel extends ClusteringParametersPanel {

	private static final long serialVersionUID = -8863262788289684604L;

	private JLabel lblThreshold;
	private JTextField txtThreshold;
	
	public LocalByThresholdPanel() {
		super();
		
		lblThreshold = new JLabel("Clustering Threshold:");
		
		txtThreshold = new JTextField();
		
		txtThreshold.setPreferredSize(new Dimension( 70, lblThreshold.getPreferredSize().height));
		
		setLayout(new FlowLayout());
		
		add(lblThreshold);
		add(txtThreshold);
		
	}

	@Override
	public String checkParameters() {
		String thS = txtThreshold.getText();
		thS.trim();
		if( thS.isEmpty() ) return "Please enter a clustering threshold.";
		double thD = Double.parseDouble(thS);
		if( thD < 0 || thD > 1.0 ) return "Threshold must be between 0.0 and 1.0.";
		return null;
	}

	@Override
	public ClusteringParameters getParameters() {
		LocalByThresholdParameters p = new LocalByThresholdParameters();
		
		String thS = txtThreshold.getText();
		thS.trim();
		double thD = Double.parseDouble(thS);
		
		p.clusteringThreshold = thD;  
		
		return p;
	}
	
}
