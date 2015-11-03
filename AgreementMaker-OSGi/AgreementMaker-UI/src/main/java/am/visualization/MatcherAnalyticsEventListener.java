package am.visualization;

import am.app.mappingEngine.MatchingTask;

public interface MatcherAnalyticsEventListener {

	public void receiveEvent(MatcherAnalyticsEvent e);
	
	public MatchingTask getMatcher();
}
