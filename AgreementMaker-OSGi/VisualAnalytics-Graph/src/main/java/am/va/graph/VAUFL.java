package am.va.graph;

import java.util.List;

import am.app.Core;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Ontology;

public class VAUFL {
	List<MatchingTask> matchingTask;
	MatchingTask userTask;

	public VAUFL() {
		matchingTask = Core.getInstance().getMatchingTasks();
		userTask = matchingTask.get(0);
		setBestMatchingGroup();
	}

	/**
	 * Set the user manual matcher to the best matching task
	 * 
	 * @return
	 */
	private boolean setBestMatchingGroup() {
		int len = matchingTask.size();
		int res = 0, best = -1;
		for (int i = 1; i < len; i++) {
			MatchingTask m = matchingTask.get(i);
			int tmp = 0;
			if (m.selectionResult != null && m.selectionResult.classesAlignment != null)
				tmp += m.selectionResult.classesAlignment.size();
			if (m.selectionResult != null && m.selectionResult.propertiesAlignment != null)
				tmp += m.selectionResult.propertiesAlignment.size();
			// System.out.println("number of alignments=" + tmp);
			if (tmp > res) {
				best = i;
				res = tmp;
			}
		}
		if (best == -1)
			return false;
		// update the 'best' result to userResult
		updateUserTask(matchingTask.get(best));
		return true;
	}

	/**
	 * Update the user manual matcher, called by setBestMatchingGroup()
	 * 
	 * @param bestTest
	 */
	private void updateUserTask(MatchingTask bestTest) {

		// DefaultMatcherParameters mParam = new DefaultMatcherParameters();

		UserManualMatcher m = new UserManualMatcher();
		m.setSourceOntology(Core.getInstance().getSourceOntology());
		m.setTargetOntology(Core.getInstance().getTargetOntology());

		userTask = new MatchingTask(m, bestTest.matcherParameters, bestTest.selectionAlgorithm,
				bestTest.selectionParameters);

		userTask.match();
		userTask.select();
		// System.out.println("set best matcher: class=" +
		// userTask.selectionResult.classesAlignment.size());
		// System.out.println("set best matcher: properity=" +
		// userTask.selectionResult.propertiesAlignment.size());
	}

	private void getAbiMatchings(VAVariables.ontologyType type) {
		// iterate the source ontology concepts
		int len = matchingTask.size();
		SimilarityMatrix sMatrix;
		for (int i = 1; i <= len; i++) {
			if (type == VAVariables.ontologyType.Source)
				sMatrix = matchingTask.get(i).matcherResult.getClassesMatrix();
		}
	}
}
