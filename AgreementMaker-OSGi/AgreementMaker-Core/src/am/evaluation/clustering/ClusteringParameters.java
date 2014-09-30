package am.evaluation.clustering;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.MatcherResult;

public abstract class ClusteringParameters {
	
	private List<MatcherResult> matchersToConsider = new ArrayList<MatcherResult>();
	
	public ClusteringParameters() {}
	
	public void addMatcher(MatcherResult abstractMatcher) { matchersToConsider.add(abstractMatcher); }
	public List<MatcherResult> getMatchers() { return matchersToConsider; }
	public void setMatchers( List<MatcherResult> m ) { matchersToConsider = m; }
}
