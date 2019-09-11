package am.extension.batchmode.matchingTask;

import java.util.LinkedList;

import am.app.mappingEngine.MatchingTask;

public class SimpleMatchingTaskRunner extends FluentMatchingTaskRunner {

	// TODO: Make shortcutting work.
	private String shortcutFilePath;
	
	private MatchingTask task;
	
	@Override
	public void run() {		
		task = new MatchingTask(matcher, matcherParams, selector, selectorParams);
		
		if (this.inputMatchers != null) {
			if (task.inputMatchingTasks == null) {
				task.inputMatchingTasks = new LinkedList<>();
			}
		}
		
		matcher.setParameters(matcherParams);
		try {
			matcher.match();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		task.matcherResult = matcher.getResult();
		
		selectorParams.inputResult = task.matcherResult;
		selector.setParameters(selectorParams);
		selector.select();
		task.selectionResult = selector.getResult();
	}
	
	@Override
	public MatchingTask getTask() {
		return task;
	}
}
