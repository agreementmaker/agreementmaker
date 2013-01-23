package am.evaluation.clustering;

import java.util.Arrays;

public class ClusterPointImpl implements ClusterPoint {

	
	private double dataPoints[];
	
	public ClusterPointImpl(double points[]) {
		dataPoints = points;
	}
	
	/**
	 * Calculate the Eucledian distance between this point and p.
	 */
	@Override
	public double getDistance(ClusterPoint p) {
		
		double[] pdata = p.data();
		
		double sum = 0.0;
		for( int i = 0; i < p.dim(); i++ ) {
			double diff = (pdata[i] - dataPoints[i]);
			diff *= diff;
			sum += diff;
		}
		return Math.sqrt(sum);
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof ClusterPoint ) {
			ClusterPoint o = (ClusterPoint) obj;
			return Arrays.equals(dataPoints, o.data());
		} else return false;
	}
	
	@Override public int dim() { return dataPoints.length; }
	@Override public double[] data() { return dataPoints; }
	@Override public double itemAt(int dim) { return dataPoints[dim]; }

	@Override
	public int compareTo(ClusterPoint o) {
		double[] compData = o.data();
		for( int i = 0; i < dataPoints.length; i++ ) {
			if( dataPoints[i] > compData[i] ) return 1;
			if( dataPoints[i] < compData[i] ) return -1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		String s = new String();
		for( int i = 0; i < dataPoints.length; i++ ) {
			s += String.valueOf(dataPoints[i]);
			if( i != dataPoints.length - 1 ) s += ",";
		}
		return s;
	}
}
