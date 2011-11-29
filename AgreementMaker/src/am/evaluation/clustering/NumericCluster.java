package am.evaluation.clustering;

public interface NumericCluster extends Comparable<NumericCluster> {

	/**
	 * Merge two clusters.
	 * return A new cluster with all the points of the old one.
	 */
	public void merge(NumericCluster inCluster);
	public ClusterPoint[] points();
	public int size();
	boolean clusterWithinDistance(NumericCluster compCluster, double dist);
	
}
