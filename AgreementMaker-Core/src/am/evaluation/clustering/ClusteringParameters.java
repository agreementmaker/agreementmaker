package am.evaluation.clustering;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.MatchingTask;

public abstract class ClusteringParameters {
	
	private List<MatchingTask> matchersToConsider = new ArrayList<MatchingTask>();
	
	public ClusteringParameters() {}
	
	public void addMatcher(MatchingTask abstractMatcher) { matchersToConsider.add(abstractMatcher); }
	public List<MatchingTask> getMatchers() { return matchersToConsider; }
	public void setMatchers( List<MatchingTask> m ) { matchersToConsider = m; }
}
