package am.evaluation.clustering.gvm;

import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.ClusterPoint;
import am.evaluation.clustering.ClusteringMethod;
import am.evaluation.clustering.ClusteringParameters;
import am.evaluation.clustering.ClusteringParametersPanel;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;

import com.tomgibara.cluster.gvm.dbl.DblClusters;
import com.tomgibara.cluster.gvm.dbl.DblResult;

/**
 * Implements mapping clustering using the GVM clustering algorithm.
 * (GVM == Greedy Variance Minimization)
 * 
 * @author Cosmin Stroe
 *
 */
public class GVM_Clustering extends ClusteringMethod {
	
	DblClusters<Point> clusters;
	List<DblResult<Point>> results;

	
	public GVM_Clustering(List<AbstractMatcher> matchers) {
		super(matchers);
		
		Collections.shuffle(availableMatchers);
		
		clusters = new DblClusters<Point>( availableMatchers.size(), 20);
		
		double[] currentPoint = new double[ availableMatchers.size() ];
		
		int rows = availableMatchers.get(0).getClassesMatrix().getRows();
		int cols = availableMatchers.get(0).getClassesMatrix().getColumns();
		
		// create the clusters
		for( int i = 0; i < rows; i++ ) {
			for( int j = 0; j < cols; j++ ) {
				for( int k = 0; k < availableMatchers.size(); k++ ) {
					currentPoint[k] = availableMatchers.get(k).getClassesMatrix().getSimilarity(i, j);
				}
				clusters.add(1.0, currentPoint, new Point(i,j) );
			}
		}
		
		results = clusters.results();
	}

	@Override
	public Cluster<Mapping> getCluster(int row, int col, VisualizationType t) {
		
		try {
			FileWriter writer = new FileWriter("clustered.txt");
			final List<DblResult<Point>> results = clusters.results();
			for (int i = 0; i < results.size(); i++) {
				DblResult<Point> p = results.get(i);

				writer.write(String.format("%3.3f %3.3f %d%n", p.getKey().x, p.getKey().y, i+1));
			}

			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		return null;
	}

	@Override public Cluster<Mapping> getCluster(Mapping mapping) { return null; }
	@Override public ClusteringParameters getParameters() { return null; }
	@Override public ClusteringParametersPanel getParametersPanel() { return null; }
	@Override public void setParameters(ClusteringParameters params) {}
	
	public static void main(String[] args) {
		
		
		DblClusters<ClusterPoint> clusters = new DblClusters<ClusterPoint>(5, 3000);
		
		
		
		
	}
	
}
