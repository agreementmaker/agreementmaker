package am.evaluation.clustering.gvm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.ClusteringMethod;
import am.evaluation.clustering.ClusteringParameters;
import am.evaluation.clustering.ClusteringParametersPanel;

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

	int numClusters;
	private SimilarityMatrix[] matrices;

	public GVM_Clustering(List<MatchingTask> matchers, int numClusters) {
		super(matchers);
		this.numClusters = numClusters;
	}

	public GVM_Clustering(SimilarityMatrix[] matrices, int numClusters){
		super(null);
		this.matrices = matrices; 
		this.numClusters = numClusters;

	}

	@Override
	public void cluster() {
		if (availableMatchers.size() == 0){
			int rows = matrices[0].getRows();
			int cols = matrices[0].getColumns();

			clusters = new DblClusters<List<double[]>>(matrices.length, numClusters);
			clusters.setKeyer(new DblListKeyer<double[]>());

			int progress = 0, newProgress = 0;
			propertyChangeSupport.firePropertyChange("progress", progress, newProgress);

			for( int i = 0; i < rows; i++ ) {
				for( int j = 0; j < cols; j++ ) {

					double[] currentPoint = new double[ matrices.length];
					double[] currentKey = new double[2];

					// fill in the current point
					for( int k = 0; k < matrices.length; k++ ) {
						currentPoint[k] = matrices[k].getSimilarity(i, j);
					}
					currentKey[0] = i;
					currentKey[1] = j;

					List<double[]> key = new ArrayList<double[]>();
					key.add(currentKey);
					clusters.add(1.0, currentPoint, key);
				}
				// Take care of the progress.
				newProgress = (i*100)/rows;
				propertyChangeSupport.firePropertyChange("progress", progress, newProgress);
				progress = newProgress;
			}

			propertyChangeSupport.firePropertyChange("progress", progress, 100);

			results = clusters.results();

		}else{
			Collections.shuffle(availableMatchers);

			int rows = availableMatchers.get(0).matcherResult.getClassesMatrix().getRows();
			int cols = availableMatchers.get(0).matcherResult.getClassesMatrix().getColumns();

			clusters = new DblClusters<List<double[]>>( availableMatchers.size(), numClusters);

			clusters.setKeyer(new DblListKeyer<double[]>());

			int progress = 0, newProgress = 0;
			propertyChangeSupport.firePropertyChange("progress", progress, newProgress);

			// create the points and add them to the cluster
			for( int i = 0; i < rows; i++ ) {
				for( int j = 0; j < cols; j++ ) {

					double[] currentPoint = new double[ availableMatchers.size()];
					double[] currentKey = new double[2];

					// fill in the current point
					for( int k = 0; k < availableMatchers.size(); k++ ) {
						currentPoint[k] = availableMatchers.get(k).matcherResult.getClassesMatrix().getSimilarity(i, j);
					}
					currentKey[0] = i;
					currentKey[1] = j;

					List<double[]> key = new ArrayList<double[]>();
					key.add(currentKey);
					clusters.add(1.0, currentPoint, key);
				}

				// Take care of the progress.
				newProgress = (i*100)/rows;
				propertyChangeSupport.firePropertyChange("progress", progress, newProgress);
				progress = newProgress;
			}

			propertyChangeSupport.firePropertyChange("progress", progress, 100);

			results = clusters.results();
		}
	}

	@Override
	public Cluster<Mapping> getCluster(int row, int col, alignType t) {

		/*		try {
			FileWriter writer = new FileWriter("clustered.txt");
			final List<DblResult<List<double[]>>> results = clusters.results();
			for (int i = 0; i < results.size(); i++) {
				for( double[] pt : results.get(i).getKey() ) {
					for( int k = 0; k < availableMatchers.size(); k++ ) 
						writer.write(String.format("%3.3f ", pt[k]));
					writer.write(String.format("%.0f %.0f %d%n", 
							pt[availableMatchers.size()], pt[availableMatchers.size()+1], i+1));
				}
			}

			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/



		return null;
	}

	@Override public Cluster<Mapping> getCluster(Mapping mapping) { return null; }
	@Override public ClusteringParameters getParameters() { return null; }
	@Override public ClusteringParametersPanel getParametersPanel() { return null; }
	@Override public void setParameters(ClusteringParameters params) {}

	public List<DblResult<List<double[]>>> getClusters() { return clusters.results(); }

	public static void main(String[] args) {




	}

}
