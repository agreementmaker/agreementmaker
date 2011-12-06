package am.evaluation.clustering;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;

/**
 * A clustering method is used to cluster similar mappings.
 * 
 * @author Cosmin Stroe
 */
public abstract class ClusteringMethod {

	protected List<AbstractMatcher> availableMatchers = new ArrayList<AbstractMatcher>();
	
	public ClusteringMethod(List<AbstractMatcher> availableMatchers) {
		if( availableMatchers != null ) 
			this.availableMatchers.addAll(availableMatchers);
	}

	public void setAvailableMatchers(List<AbstractMatcher> availableMatchers) {	this.availableMatchers = availableMatchers; }
	public List<AbstractMatcher> getAvailableMatchers() { return availableMatchers; }
	
	/**
	 * Get the cluster of a mapping that is identified by its row and column in the matrix.
	 * @param row
	 * @param col
	 * @param t Either Classes or Properties Matrix.
	 * @return The cluster of the mapping at (row,col).
	 */
	public abstract Cluster<Mapping> getCluster(int row, int col, VisualizationType t);
	
	/**
	 * Get the cluster of a mapping.
	 */
	public abstract Cluster<Mapping> getCluster(Mapping mapping);
	
	/**
	 * ClusteringParameters are set if the clustering method needs extra parameters.
	 */
	public abstract void setParameters( ClusteringParameters params );

	/**
	 * Return the clustering parameters of this method.
	 */
	public abstract ClusteringParameters getParameters();
	
	/**
	 * Return a panel which is used by the UI to display to the user and allow them
	 * to set parameters of this clustering method.
	 */
	public abstract ClusteringParametersPanel getParametersPanel();

	
}
