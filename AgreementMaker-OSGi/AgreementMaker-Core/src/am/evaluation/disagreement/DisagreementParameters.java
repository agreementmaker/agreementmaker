package am.evaluation.disagreement;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.MatchingTask;

public abstract class DisagreementParameters {

	/* Fields */
	
	private List<MatchingTask> matchersToConsider;
	
	
	
	/* Constructor */
	
	public DisagreementParameters() {
		matchersToConsider = new ArrayList<>();
	}
	
	/* Methods */
	
	public void addMatcher(MatchingTask abstractMatcher) { matchersToConsider.add(abstractMatcher); }
	public List<MatchingTask> getMatchers() { return matchersToConsider; }
	
	public void setMatchers(List<MatchingTask> matcherList ) { matchersToConsider = matcherList; }
	
}
