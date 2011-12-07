package am.evaluation.clustering.gvm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.ClusteringMethod;
import am.evaluation.clustering.ClusteringParameters;
import am.evaluation.clustering.ClusteringParametersPanel;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;

import com.tomgibara.cluster.gvm.dbl.DblClusters;
import com.tomgibara.cluster.gvm.dbl.DblListKeyer;
import com.tomgibara.cluster.gvm.dbl.DblResult;

/**
 * Implements mapping clustering using the GVM clustering algorithm.
 * (GVM == Greedy Variance Minimization)
 * 
 * @author Cosmin Stroe
 *
 */
public class GVM_Clustering extends ClusteringMethod {
	
	DblClusters<List<double[]>> clusters;
	List<DblResult<List<double[]>>> results;

	
	public GVM_Clustering(List<AbstractMatcher> matchers) {
		super(matchers);
		
		Collections.shuffle(availableMatchers);
		
		int rows = availableMatchers.get(0).getClassesMatrix().getRows();
		int cols = availableMatchers.get(0).getClassesMatrix().getColumns();
		
		DblClusters<List<double[]>> clusters = new DblClusters<List<double[]>>( availableMatchers.size(), 20);
		
		clusters.setKeyer(new DblListKeyer<double[]>());
		
		
		// create the points and add them to the cluster
		for( int i = 0; i < rows; i++ ) {
			for( int j = 0; j < cols; j++ ) {

				double[] currentPoint = new double[ availableMatchers.size()];
				double[] currentKey = new double[ availableMatchers.size() + 2];
				
				// fill in the current point
				for( int k = 0; k < availableMatchers.size(); k++ ) {
					currentPoint[k] = availableMatchers.get(k).getClassesMatrix().getSimilarity(i, j);
					currentKey[k] = availableMatchers.get(k).getClassesMatrix().getSimilarity(i, j);
				}
				currentKey[availableMatchers.size()] = i;
				currentKey[availableMatchers.size()+1] = j;
				
				List<double[]> key = new ArrayList<double[]>();
				key.add(currentKey);
				clusters.add(1.0, currentPoint, key);
			}
		}
		
		results = clusters.results();
	}

	@Override
	public Cluster<Mapping> getCluster(int row, int col, VisualizationType t) {
		
		try {
			FileWriter writer = new FileWriter("clustered.txt");
			final List<DblResult<List<double[]>>> results = clusters.results();
			for (int i = 0; i < results.size(); i++) {
				for( double[] pt : results.get(i).getKey() ) {
					for( int k = 0; k < availableMatchers.size(); k++ ) 
						writer.write(String.format("%3.3f ", pt[k]));
					writer.write(String.format("%d %d %d%n", 
							pt[availableMatchers.size()], pt[availableMatchers.size()+1], i+1));
				}
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
		
		
				
		
	}
	
}
