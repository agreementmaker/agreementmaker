package am.va.graph;

import java.util.HashMap;

import javax.swing.SwingUtilities;

import am.app.Core;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;

public class VASyncListener implements MatcherChangeListener {

	private VAGroup rootGroupLeft;
	private VAGroup rootGroupRight;

	public VASyncListener() {
		Core.getInstance().addMatcherChangeListener(this);
	}

	public VAGroup getRootGroup() {
		return rootGroupLeft;
	}

	@Override
	public void matcherChanged(MatchingTaskChangeEvent e) {

		// Build root panel for source ontology
		if (!(e.getTask().matchingAlgorithm instanceof UserManualMatcher)) {
			/**
			 * Set root group left
			 */
			rootGroupLeft = new VAGroup();
			rootGroupLeft.setParent(0);
			VAData rootNodeLeft = VASyncData
					.getRootVAData(VAVariables.ontologyType.Source);
			rootGroupLeft.setRootNode(rootNodeLeft);
			// get all the children data sorted by similarity
			rootGroupLeft.setListVAData(VASyncData.getChildrenData(
					rootGroupLeft.getRootNode(),
					VAVariables.ontologyType.Source));

			/**
			 * Set root group right
			 */
			rootGroupRight = new VAGroup();
			rootGroupRight.setParent(0);
			VAData rootNodeRight = VASyncData
					.getRootVAData(VAVariables.ontologyType.Target);
			rootGroupRight.setRootNode(rootNodeRight);
			rootGroupRight.setListVAData(VASyncData.getChildrenData(
					rootGroupRight.getRootNode(),
					VAVariables.ontologyType.Target));

			Core.getInstance().removeMatcherChangeListener(this);

			/**
			 * Init the JavaFx framework
			 */
			VAPanel.setRootGroupLeft(this.rootGroupLeft);
			VAPanel.setRootGroupRight(this.rootGroupRight);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					VAPanel.initAndShowGUI();
				}
			});
		}
	}
}
