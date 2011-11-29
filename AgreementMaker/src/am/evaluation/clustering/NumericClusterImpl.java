package am.evaluation.clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class NumericClusterImpl implements NumericCluster {
	
	private ClusterPoint[] points;	
	private int clusterRank;
	
	public NumericClusterImpl(int rank, int dim) {
		clusterRank = rank;
		points = new ClusterPoint[dim];
	}
	
	@Override
	public void absorb( NumericCluster inCluster ) {
		
		ClusterPoint[] inPoints = inCluster.points();
		int newDim = points.length + inPoints.length;
		
		ClusterPoint[] newPoints = Arrays.copyOf(points, newDim);
		for(int i = points.length; i < newDim; i++ ) { 
			newPoints[i] = inPoints[i-points.length];
		}
		
		points = newPoints;
	}
	
	@Override
	public boolean clusterWithinDistance(NumericCluster compCluster, double dist) {
		
		ClusterPoint[] compPoints = compCluster.points();
		
		// shortcut for removing duplicates
		if( dist == 0.0d && compPoints[0] == (points[0])) {
			return true;
		}
		
		for( int i = 0; i < points.length; i++ ) {
			for( int j = 0; j < compPoints.length; j++ ) {
				ClusterPoint p1 = points[i];
				ClusterPoint p2 = compPoints[j];
				if( p1.getDistance(p2) > dist ) return false;
			}
		}
		
		return true;
	}
	
	@Override public ClusterPoint[] points() { return points; }
	@Override public int size() { return points.length; }
	@Override public int rank() { return clusterRank; }
	
	@Override
	public int compareTo(NumericCluster o) {
		return points.length - o.size();
	}
	
	public static void main(String[] args) throws Exception{
		
		Logger log = Logger.getLogger(NumericClusterImpl.class);
		log.setLevel(Level.DEBUG);
		
		// read in our data file
		long startTime = System.currentTimeMillis();
		
		BufferedReader bf = new BufferedReader(new FileReader(new File("/home/cosmin/Desktop/clustering/merged.csv")));
		
	
		int lines = 0;
		while( (bf.readLine()) != null ) { lines++; }
		bf.close();

		log.debug("Datafile contains " + lines + " lines.  Reading in points.");
		
		// read in datapoints
		Queue<ClusterPoint> points = new PriorityQueue<ClusterPoint>(lines);
		
		bf = new BufferedReader(new FileReader(new File("/home/cosmin/Desktop/clustering/merged.csv")));
		String ls;
		for( int i = 0; i < lines; i++) {
			ls = bf.readLine();
			String[] split = ls.split(",");
			double[] data = new double[split.length];
			for( int j = 0; j < split.length; j++ ) {
				data[j] = Double.parseDouble(split[j]);
			}
			
			points.add(new ClusterPointImpl(data));
		};
		
		long endTime = System.currentTimeMillis();
		
		log.debug("Read data file in: " + points.size() + " data points, " + (endTime-startTime)/1000d + " sec.");
		
				
	
		startTime = System.currentTimeMillis();
		
		List<ClusterPoint> pointList = new ArrayList<ClusterPoint>();
		List<NumericCluster> clusterList = new ArrayList<NumericCluster>();
		
		// remove duplicate points
		while( points.size() > 0 ) {
			ClusterPoint p = points.poll();
			pointList.add(p);
			
			NumericCluster cl = new NumericClusterImpl(clusterList.size(), 1);
			cl.points()[0] = p;
			clusterList.add(cl);
			
			while( points.peek() != null && points.peek().equals(p) ) points.poll();  // ignore all the duplicate points.
		}
		
		points = null;
		
		endTime = System.currentTimeMillis();
		
		log.debug("Read " + pointList.size() + " unique points, " + (endTime-startTime)/1000d + " sec.");
		
		
		
		// start clustering
		double dist = 0.0d;
		while( clusterList.size() > 1 ) {
			
			Comparator<NumericCluster> cmp = new Comparator<NumericCluster>() {
				@Override
				public int compare(NumericCluster o1, NumericCluster o2) {
					
					ClusterPoint[] p1 = o1.points();
					ClusterPoint[] p2 = o2.points();
					
					
					
					return 0;
				}
			};
			Queue<NumericCluster> clusters = new PriorityQueue<NumericCluster>(clusterList.size(), cmp);
			
			boolean absorbHappened = false;
			for( int i = 0; i < clusterList.size(); i++ ) {
				NumericCluster icl = clusterList.get(i);
				for( int j = i+1; j < clusterList.size(); j++ ) {
					NumericCluster jcl = clusterList.get(j);
					
					if( icl.clusterWithinDistance(jcl, dist) ) {
						icl.absorb(jcl);
						clusterList.remove(j);
						j--;
						absorbHappened = true;
					}
				}
				log.debug("i = " + i);
			}
			
			if( !absorbHappened ) {
				log.debug("Distance " + dist + ": " + clusterList.size() + " clusters.");
				dist += 0.01d;
			}
			
		}
		
		
		
		
		//writeOut( clusters, "/home/cosmin/Desktop/clustering/merged-woDup.csv");
		
		
	}
	
	/* TODO: Implement this */
	public void saveClusters( Collection<NumericCluster> clusters, String filename ) {
		
	}

}
