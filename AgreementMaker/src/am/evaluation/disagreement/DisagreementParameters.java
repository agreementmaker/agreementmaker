package am.evaluation.disagreement;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;

public abstract class DisagreementParameters {

	private List<AbstractMatcher> matchersToConsider;
	
	public DisagreementParameters() {
		matchersToConsider = new ArrayList<AbstractMatcher>();
	}
	
	public void addMatcher(AbstractMatcher abstractMatcher) { matchersToConsider.add(abstractMatcher); }
	public List<AbstractMatcher> getMatchers() { return matchersToConsider; }

}
