package am.evaluation.clustering;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;

/**
 * A clustering method is used to cluster similar mappings.
 * 
 * @author Cosmin Stroe
 */
public abstract class ClusteringMethod {
	
	protected final PropertyChangeSupport propertyChangeSupport;

	protected List<MatchingTask> availableMatchers = new ArrayList<>();
	
	public ClusteringMethod(List<MatchingTask> availableMatchers) {
		
		propertyChangeSupport = new PropertyChangeSupport(this);
		
		if( availableMatchers != null ) 
			this.availableMatchers.addAll(availableMatchers);
	}

	public void setAvailableMatchers(List<MatchingTask> availableMatchers) {	this.availableMatchers = availableMatchers; }
	public List<MatchingTask> getAvailableMatchers() { return availableMatchers; }
	
	public abstract void cluster();
	
	/**
	 * Get the cluster of a mapping that is identified by its row and column in the matrix.
	 * @param row
	 * @param col
	 * @param t Either Classes or Properties Matrix.
	 * @return The cluster of the mapping at (row,col).
	 */
	public abstract Cluster<Mapping> getCluster(int row, int col, alignType t);
	
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

	public void addPropertyChangeListener( PropertyChangeListener listener ) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener( PropertyChangeListener listener ) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
