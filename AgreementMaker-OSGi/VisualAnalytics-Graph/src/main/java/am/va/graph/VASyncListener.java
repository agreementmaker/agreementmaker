package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.input.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import am.app.Core;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.MatchingTaskChangeEvent.EventType;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class VASyncListener implements MatcherChangeListener {

	private VAGroup rootGroup;
	private JFrame frame = null;
	private JFXPanel fxPanel = null;

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
			TEST(rootGroup);
			Core.getInstance().removeMatcherChangeListener(this);

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					testInitFrame();
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

	public void testInitFrame() {
		frame = new JFrame("VA");
		frame.setSize(500, 500);
		fxPanel = new JFXPanel();
		frame.add(fxPanel);
		frame.setVisible(true);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				testInitFX();
			}
		});
	}

	public void testInitFX() {
		final Group root = new Group();
		final Scene myScene = new Scene(root);

		VAPieChart chart = new VAPieChart(this.rootGroup);
		chart.getPieChart().setClockwise(false);
		root.getChildren().add(chart.getPieChart());

		fxPanel.setScene(myScene);

		for (PieChart.Data currentData : chart.getPieChart().getData()) {
			currentData.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent arg0) {
							// TODO Auto-generated method stub
							VAGroup newGroup = getNewGroup(rootGroup);
							VAPieChart chart = new VAPieChart(newGroup);
							chart.getPieChart().setClockwise(false);
							root.getChildren().add(chart.getPieChart());
							fxPanel.removeAll();
							fxPanel.updateUI();//?
						}

					});
		}
	}

	public VAGroup getNewGroup(VAGroup currentGroup) {
		VAData newRootData = rootGroup.getMapVAData().get("Reference");
		VAGroup newGroup = new VAGroup();
		newGroup.setParent(currentGroup.getGroupID());
		newGroup.setRootNode(newRootData);
		newGroup.setMapVAData(VASyncData.getChildrenData(newRootData));
		TEST(newGroup);
		return newGroup;
	}
}
