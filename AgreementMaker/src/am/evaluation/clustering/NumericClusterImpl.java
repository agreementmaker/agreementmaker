package am.evaluation.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class NumericClusterImpl implements NumericCluster {
	
	private ClusterPoint[] points;
	
	
	public NumericClusterImpl(int dim) {
		points = new ClusterPoint[dim];
	}
	
	@Override
	public void merge( NumericCluster inCluster ) {
		
		ClusterPoint[] inPoints = inCluster.points();
		int newDim = points.length + inPoints.length;
		
		ClusterPoint[] newPoints = Arrays.copyOf(points, newDim);
		for(int i = points.length; i < newDim; i++ ) 
			newPoints[i] = inPoints[i-points.length];
		
		points = newPoints;
	}
	
	@Override
	public boolean clusterWithinDistance(NumericCluster compCluster, double dist) {
		
		ClusterPoint[] compPoints = compCluster.points();
		
		// shortcut for removing duplicates
		if( dist == 0.0d && compPoints[0].equals(points[0])) {
			return true;
		}
		
		for( ClusterPoint p : points ) {
			for( ClusterPoint q : compPoints ) {
				if( p.getDistance(q) > dist ) return false;
			}
		}
		
		return true;
	}
	
	@Override public ClusterPoint[] points() { return points; }
	@Override public int size() { return points.length; }
	
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
		
		Queue<NumericCluster> clusters = new PriorityQueue<NumericCluster>(lines);
		
		// create the clusters
		while( points.size() > 0 ) {
			ClusterPoint p = points.poll();
			NumericCluster cl = new NumericClusterImpl(1);
			cl.points()[0] = p;
			clusters.add(cl);
			while( points.peek() != null && points.peek().equals(p) ) points.poll();  // ignore all the duplicate points.
		}
		
		
		endTime = System.currentTimeMillis();
		
		log.debug("Created " + clusters.size() + " unique clusters, " + (endTime-startTime)/1000d + " sec.");
		
		double dist = 0.0d;
		
		List<NumericCluster> tempiClusters = new ArrayList<NumericCluster>();
		List<NumericCluster> tempjClusters = new ArrayList<NumericCluster>();
		
		while( clusters.size() > 1 ) {
			boolean mergeHappened = false;
			
			while( clusters.size() > 0 ) {
				// pop the smallest cluster and try to merge it with every other cluster
				NumericCluster iCluster = clusters.poll();
				
				while( clusters.size() > 0 ) {
					NumericCluster jCluster = clusters.poll();
					if( iCluster.clusterWithinDistance(jCluster, dist) ) {
						log.debug("iClusters: " + tempiClusters.size() + ", Clusters: " + clusters.size() + ". Merging " + iCluster.size() + " with " + jCluster.size() + ".");
						iCluster.merge(jCluster);
						//tempiClusters.add(iCluster);
						//clusters.addAll(tempjClusters);
						//tempjClusters.clear();
						//iCluster = clusters.poll();
						mergeHappened = true;
					} else {
						// jCluster was not merged, save it.
						tempjClusters.add(jCluster);
					}
				}

				int num = 0, clnum = 0;
				for( NumericCluster cl : tempjClusters ) { num += cl.size(); }
				for( NumericCluster cl : tempiClusters ) { num += cl.size(); }
				num += iCluster.size();
				clnum = tempjClusters.size() + tempiClusters.size() + 1;
				log.debug("Total clusters: " + clnum + ".");
				log.debug("Total points in the clusters: " + num + ".");
				clusters.addAll(tempjClusters);
				tempjClusters.clear();
				tempiClusters.add(iCluster);
				
			}

			clusters.addAll(tempiClusters);
			tempiClusters.clear();
			
			if( !mergeHappened ) {
				// no merge happened, so we need to add back the iClusters.				
				log.debug("Distance " + dist + ", " + clusters.size() + " clusters.");
				dist += 0.01;
			}
		}
		
		
		
	}
}
