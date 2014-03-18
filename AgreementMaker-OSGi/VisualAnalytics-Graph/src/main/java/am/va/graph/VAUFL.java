package am.va.graph;

import java.util.List;

import am.app.Core;
import am.app.mappingEngine.MatcherResult;
import am.app.mappingEngine.MatchingTask;

public class VAUFL {
	List<MatchingTask> matchingTask;
	MatchingTask userTask;

	public VAUFL() {
		matchingTask = Core.getInstance().getMatchingTasks();
		userTask = matchingTask.get(0);
	}

	private boolean setBestMatchingGroup() {
		int len = matchingTask.size() - 1;
		int res = 0, best = -1;
		for (int i = 0; i < len; i++) {
			MatchingTask m = matchingTask.get(i);
			int tmp = m.selectionResult.classesAlignment.size() + m.selectionResult.propertiesAlignment.size();
			if (tmp > res) {
				best = i;
				res = tmp;
			}
		}
		if (best == -1)
			return false;
		MatcherResult userResult = userTask.matcherResult;
		//copy the 'best' result to userResult
		return true;
	}
	
	
	
}
