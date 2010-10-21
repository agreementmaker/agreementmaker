package am.evaluation.clustering.globalfixed;

import java.util.List;
import java.util.Set;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.ClusteringMethod;
import am.evaluation.clustering.ClusteringParameters;
import am.evaluation.clustering.ClusteringParametersPanel;
import am.evaluation.clustering.ClusterFactory.ClusteringType;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;

public class GlobalFixedMethod extends ClusteringMethod {

	public GlobalFixedMethod(List<AbstractMatcher> availableMatchers) {
		super(availableMatchers);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ClusteringParameters getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClusteringParametersPanel getParametersPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cluster<Mapping> getCluster(int row, int col, VisualizationType t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParameters(ClusteringParameters params) {
		// TODO Auto-generated method stub
		
	}

}
