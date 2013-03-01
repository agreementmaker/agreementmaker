package am.app.mappingEngine;

import java.util.EventListener;

public interface MatcherChangeListener extends EventListener {
	
	public void matcherChanged( MatchingTaskChangeEvent e );
}
