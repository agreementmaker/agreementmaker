package am.visualization.ClusteringEvaluation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.localByThreshold.LocalByThresholdMethod;
import am.evaluation.clustering.localByThreshold.LocalByThresholdParameters;
import am.ui.MatchersChooser;
import am.ui.api.AMTab;
import am.ui.api.impl.AMTabSupportPanel;

public class ClusteringEvaluationPanel 	extends AMTabSupportPanel 
										implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = -5491977894442159008L;

	public ClusteringEvaluationPanel() {
		super("Clustering Evaluation", "A panel to evaluate our clustering algorithms.");
		initComponents();
	}
	
	private void initComponents() {
		
		setLayout( new BorderLayout() );
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(this);
		btnStart.setActionCommand("start");
		
		add( btnStart, BorderLayout.NORTH );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getActionCommand() == "start" ) {
			// start button was pressed
			AbstractMatcher reference  = MatchersChooser.getOneMatcher("Please select the reference alignment matcher:");
			List<AbstractMatcher> matchers = MatchersChooser.getManyMatchers("Please select the matchers for computing the clusters:");
			
			evaluateLocalbyThresholdClustering( reference, matchers);
		}
		
	}

	private void evaluateLocalbyThresholdClustering(AbstractMatcher reference, List<AbstractMatcher> matchers) {

		LocalByThresholdMethod clm = new LocalByThresholdMethod(matchers);
		LocalByThresholdParameters clmp = new LocalByThresholdParameters();
		clmp.clusteringThreshold = 0.1;
		clm.setParameters(clmp);
		
		Mapping firstMapping = reference.getClassAlignmentSet().get(0);
		System.out.println("Computing the cluster of mapping: " + firstMapping);
		for( double th = 0.0d; th < 0.5; th += 0.01 ) {
			((LocalByThresholdParameters)(clm.getParameters())).clusteringThreshold = th;
			Cluster<Mapping> cl = clm.getCluster(firstMapping);
			
			ReferenceEvaluationData red = ReferenceEvaluator.compare(cl.getAlignment(), reference.getClassAlignmentSet());

			System.out.println("th="+ th + ": size(" + cl.size() +")" + " inRef(" + red.getCorrect() + ")" );
		}
		
		
		
		
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
}
