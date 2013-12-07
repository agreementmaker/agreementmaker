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
			rootGroup.setMapVAData(VASyncData.getChildrenData(rootGroup
					.getRootNode()));
			// TEST(rootGroup);
			Core.getInstance().removeMatcherChangeListener(this);

			/**
			 * Init the JavaFx framework
			 */
			VAPanel.setRootGroup(this.rootGroup);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					VAPanel.initAndShowGUI();
				}
			});
		}
	}

	/**
	 * Test only
	 * 
	 * @param rootGroup
	 */
	private void TEST(VAGroup rootGroup) {
		String rootNodeName = rootGroup.getRootNode().getSourceNode()
				.getLocalName();
		System.out.println(rootNodeName);
		HashMap<String, VAData> vaData = rootGroup.getMapVAData();
		for (VAData d : vaData.values()) {
			System.out.println(d.getSourceNode().getLocalName() + ","
					+ d.getTargetNode().getLocalName() + ","
					+ d.getSimilarity());
		}
		HashMap<String, Integer> slots = rootGroup.getSlots();
		for (String s : slots.keySet()) {
			System.out.println(s + ":" + slots.get(s));
		}
	}
}
