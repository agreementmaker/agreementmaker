package am.evaluation.disagreement;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;

public abstract class DisagreementParameters {

	/* Fields */
	
	private List<AbstractMatcher> matchersToConsider;
	
	
	
	/* Constructor */
	
	public DisagreementParameters() {
		matchersToConsider = new ArrayList<AbstractMatcher>();
	}
	
	/* Methods */
	
	public void addMatcher(AbstractMatcher abstractMatcher) { matchersToConsider.add(abstractMatcher); }
	public List<AbstractMatcher> getMatchers() { return matchersToConsider; }
	
	public void setMatchers(List<AbstractMatcher> matcherList ) { matchersToConsider = matcherList; } 

}
