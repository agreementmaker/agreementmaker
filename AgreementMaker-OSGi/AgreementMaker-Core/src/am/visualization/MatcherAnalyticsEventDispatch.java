package am.visualization;

import am.evaluation.clustering.ClusterFactory.ClusteringType;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;

/**
 * This interface is for objects that act as a dispatch for messages between MatrixPlot objects.
 * This allows the user to select the same mapping on multiple MatrixPlot objects, just by clicking
 * on one of the MatrixPlot objects.
 * @author cosmin
 *
 */
public interface MatcherAnalyticsEventDispatch {
	
	public void broadcastEvent( MatcherAnalyticsEvent e );
	public VisualizationType getType();
	public void buildClusters( ClusteringType t );
	//public void setMappingLabel(String label);
	
}
