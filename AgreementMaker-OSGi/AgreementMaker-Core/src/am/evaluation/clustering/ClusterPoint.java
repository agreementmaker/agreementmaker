package am.evaluation.clustering;

public interface ClusterPoint extends Comparable<ClusterPoint> {

	public int dim();
	public double itemAt(int dim);
	public double[] data();
	public double getDistance( ClusterPoint p );
}
