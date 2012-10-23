package am.visualization;

import am.app.mappingEngine.AbstractMatcher;

public interface MatcherAnalyticsEventListener {

	public void receiveEvent(MatcherAnalyticsEvent e);
	
	public AbstractMatcher getMatcher();
}
