package am.extension.batchmode.matchingTask;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.SelectionAlgorithm;

/**
 * Responsible for correctly running a matching task. Allows for the ability
 * "shortcut": to save results to disk and retrieve them later instead of
 * running the computation again.
 * 
 * The setter methods allow for "fluent coding" usage by returning the task
 * runner.  Their naming also uses the "with" prefix instead of "set".
 */
public interface MatchingTaskRunner {

	public void setMatcher(AbstractMatcher matcher);
	public void setMatcherParameters(DefaultMatcherParameters params);
	
	public void setSelector(SelectionAlgorithm selector);
	public void setSelectorParameters(DefaultSelectionParameters params);
	
	public void setShortcutFile(String filePath);
	
	public void run();
	
	public MatchingTask getTask();
}
