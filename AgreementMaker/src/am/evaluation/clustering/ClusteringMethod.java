package am.evaluation.clustering;

import java.util.List;
import java.util.Set;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.evaluation.clustering.ClusterFactory.ClusteringType;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;

public abstract class ClusteringMethod {

	private List<AbstractMatcher> availableMatchers;
	
	public ClusteringMethod(List<AbstractMatcher> availableMatchers) {
		this.setAvailableMatchers(availableMatchers);
	}

	public ClusteringMethod() {	}

	public abstract Cluster<Mapping> getCluster(int row, int col, VisualizationType t);
	public abstract ClusteringParameters getParameters();
	public abstract ClusteringParametersPanel getParametersPanel();
	public abstract void setParameters( ClusteringParameters params );

	public void setAvailableMatchers(List<AbstractMatcher> availableMatchers) {	this.availableMatchers = availableMatchers; }
	public List<AbstractMatcher> getAvailableMatchers() { return availableMatchers; }
}
