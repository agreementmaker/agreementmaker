package am.evaluation.clustering;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;

public abstract class ClusteringParameters {
	
	private List<AbstractMatcher> matchersToConsider;
	
	public ClusteringParameters() {
		matchersToConsider = new ArrayList<AbstractMatcher>();
	}
	
	public void addMatcher(AbstractMatcher abstractMatcher) { matchersToConsider.add(abstractMatcher); }
	public List<AbstractMatcher> getMatchers() { return matchersToConsider; }
}
