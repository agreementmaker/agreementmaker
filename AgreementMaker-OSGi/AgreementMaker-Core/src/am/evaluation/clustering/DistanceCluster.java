package am.evaluation.clustering;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;

public class DistanceCluster extends ClusteringMethod{

	public DistanceCluster(List<AbstractMatcher> availableMatchers) {
		super(availableMatchers);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void cluster() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Cluster<Mapping> getCluster(int row, int col, alignType t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cluster<Mapping> getCluster(Mapping mapping) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParameters(ClusteringParameters params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ClusteringParameters getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClusteringParametersPanel getParametersPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
