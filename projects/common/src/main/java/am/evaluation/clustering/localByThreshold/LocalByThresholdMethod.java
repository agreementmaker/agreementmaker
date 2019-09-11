package am.evaluation.clustering.localByThreshold;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.ClusteringMethod;
import am.evaluation.clustering.ClusteringParameters;
import am.evaluation.clustering.ClusteringParametersPanel;
import am.utility.PointComparator;

/**
 * This method implements the local-by-threshold clustering method.
 * 
 * @author Cosmin Stroe
 */
public class LocalByThresholdMethod extends ClusteringMethod {

	protected LocalByThresholdParameters 		params;
	
	public LocalByThresholdMethod(List<MatchingTask> availableMatchers) {
		super(availableMatchers);
		
	}
	
	@Override 
	public void cluster() {}
	
	@Override public void setParameters(ClusteringParameters params) { this.params = (LocalByThresholdParameters) params; }
	@Override public ClusteringParameters getParameters() { return params; }
	@Override public ClusteringParametersPanel getParametersPanel() { return new LocalByThresholdPanel(); }

	@Override
	public Cluster<Mapping> getCluster(int row, int col, alignType t) {

		// build all the sets
		ArrayList<TreeSet<Point>> setList = new ArrayList<TreeSet<Point>>();
		
		List<MatchingTask> matcherList = params.getMatchers();
		for( MatchingTask m : matcherList ) {
			setList.add( buildSet( m, t, row, col ) );
		}
		
		// find the intersection of the sets
		TreeSet<Point> intersectionSet = null;
		
		if( setList.size() == 0 ) {
			// no sets, leave intersectionSet == null
		}
		if( setList.size() == 1 ) {
			// one set
			intersectionSet = setList.get(0);
		} else {
			// more than one set, must compute intersection.
			intersectionSet = computeIntersection( setList.get(0), setList.get(1) ); // the first two sets
			
			// the rest of the sets
			for( int i = 2; i < setList.size(); i++ ) {
				intersectionSet = computeIntersection( intersectionSet, setList.get(i) ); 
			}
			
		}
		
		Cluster<Mapping> c = null;
		
		if( intersectionSet != null ) {
			// create the cluster
			c = new Cluster<Mapping>(intersectionSet, 
					matcherList.get(0).matcherParameters.getSourceOntology(), 
					matcherList.get(0).matcherParameters.getTargetOntology(), t );
		}
		
		return c;
	}

	@Override
	public Cluster<Mapping> getCluster(Mapping m ) {		
		return getCluster(m.getSourceKey(), m.getTargetKey(), m.getAlignmentType());
	}
	
	


	private TreeSet<Point> buildSet(MatchingTask m, alignType t,
			int row, int col) {
		
		TreeSet<Point> currentSet = new TreeSet<Point>( new PointComparator() );
		
		SimilarityMatrix mtx = null;
		
		if( t == alignType.aligningClasses ) {
			mtx = m.matcherResult.getClassesMatrix();
		} else if ( t == alignType.aligningProperties ) {
			mtx = m.matcherResult.getPropertiesMatrix();
		}
		
		// get the similarity of the selected mapping
		double candidateSim = mtx.getSimilarity(row, col);
		
		for( int i = 0; i < mtx.getRows(); i++ ) {
			for( int j = 0; j < mtx.getColumns(); j++ ) {
				double currentSim = mtx.getSimilarity(i, j);
				if( currentSim >= candidateSim - params.clusteringThreshold &&
					currentSim <= candidateSim + params.clusteringThreshold ) {
					// the currentSim is within the clustering threshold range of the candidateSim
					// add it to the set
					//System.out.println("candidateSim: " + candidateSim + ", currentSim: " + )
					currentSet.add( new Point(i,j) );
				}
			}
		}
		
		
		return currentSet;
	}


	private TreeSet<Point> computeIntersection(TreeSet<Point> treeSet,
			TreeSet<Point> treeSet2) {
	
		TreeSet<Point> intersection = new TreeSet<Point>( new PointComparator() );
		
		for( Point p : treeSet ) {
			if( treeSet2.contains(p) )	intersection.add(p);
		}
		
		return intersection;
	}


}
