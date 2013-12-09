package am.va.graph;

import java.util.HashMap;

import javax.swing.SwingUtilities;

import am.app.Core;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;

public class VASyncListener implements MatcherChangeListener {

	private VAGroup rootGroup;

	public VASyncListener() {
		Core.getInstance().addMatcherChangeListener(this);
	}

	public VAGroup getRootGroup() {
		return rootGroup;
	}


	@Override
	public void matcherChanged(MatchingTaskChangeEvent e) {

		// Build root panel for source ontology
		if (!(e.getTask().matchingAlgorithm instanceof UserManualMatcher)) {
			rootGroup = new VAGroup();
			rootGroup.setParent(0);
			rootGroup.setRootNode(VASyncData
					.getRootVAData(VAVariables.ontologyType.Source));
			//rootGroup.setMapVAData(VASyncData.getChildrenData(rootGroup.getRootNode()));
			// get all the children data sorted by similarity
			rootGroup.setListVAData(VASyncData.getChildrenData(rootGroup
					.getRootNode()));
			// TEST(rootGroup);
			Core.getInstance().removeMatcherChangeListener(this);

			/**
			 * Init the JavaFx framework
			 */
			VAPanel.setRootGroupLeft(this.rootGroup);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					VAPanel.initAndShowGUI();
				}
			});
		}
	}
}
