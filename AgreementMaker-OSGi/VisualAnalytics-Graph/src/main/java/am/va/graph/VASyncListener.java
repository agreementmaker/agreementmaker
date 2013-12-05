package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;

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
			rootGroup
					.setRootNode(getRootVAData(VAVariables.ontologyType.Source));
			rootGroup.setDataList(getChildrenData(rootGroup.getRootNode()));
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
		ArrayList<VAData> vaData = rootGroup.getLstVAData();
		for (VAData d : vaData) {
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
		Group root = new Group();
		Scene myScene = new Scene(root);

		ObservableList<PieChart.Data> pieChartData = FXCollections
				.observableArrayList();
		HashMap<String, Integer> slots = rootGroup.getSlots();
		for (String s : slots.keySet()) {
			if (slots.get(s) > 0)
				pieChartData.add(new PieChart.Data(s, slots.get(s)));
		}

		PieChart chart = new PieChart(pieChartData);
		chart.setClockwise(false);
		root.getChildren().add(chart);
		System.out.println(chart.startAngleProperty());

		fxPanel.setScene(myScene);
	}

	/**
	 * Get the children list of current root node
	 * 
	 * @param rootNodeData
	 * @return
	 */
	private ArrayList<VAData> getChildrenData(VAData rootNodeData) {
		ArrayList<VAData> res = new ArrayList<VAData>();
		Node rootNode = rootNodeData.sourceNode;
		for (Node n : rootNode.getChildren()) {
			// get target node info which best matches this node
			VAData newChildData = getMatchingVAData(n);
			res.add(newChildData);
		}
		return res;
	}

	/**
	 * Generate one child's matching VAData, called by getChildrenData
	 * 
	 * @param n
	 * @return
	 */
	private VAData getMatchingVAData(Node n) {
		Node matchingNode = null;
		double sim = 0.00;
		MatchingTask matchingTask = Core.getInstance()
				.getMatchingTasksWithoutUserManualMatcher().get(0);
		SimilarityMatrix smClass = matchingTask.matcherResult
				.getClassesMatrix();
		Mapping map[] = smClass.getRowMaxValues(n.getIndex(), 1); // bug
		if (map != null) {
			matchingNode = map[0].getEntity2();
			// sim = map[0].getSimilarity();
			sim = Math.random(); // only for testing
		} else {
			System.out.println("mapping data is null ???");
		}
		return new VAData(n, matchingNode, sim);
	}

	// -------------Below functions for root ontologies only --------------
	/**
	 * Get VAData for Root node
	 * 
	 * @param ontologyType
	 * @return
	 */
	private VAData getRootVAData(VAVariables.ontologyType ontologyType) {
		Node sNode = null, tNode = null;
		double Similarity = 0.0;
		if (ontologyType == VAVariables.ontologyType.Source) { // pie chart for
																// source
																// ontology
			sNode = getRootNode(VAVariables.ontologyType.Source);
			tNode = null;
		}
		return new VAData(sNode, tNode, Similarity);
	}

	/**
	 * Get the root Node
	 * 
	 * @param ontologyType
	 * @return
	 */
	private Node getRootNode(VAVariables.ontologyType ontologyType) {
		Node rootNode = null;
		List<MatchingTask> matchingTask = Core.getInstance().getMatchingTasks();
		MatchingTask currentTask = matchingTask.get(0); // for now only select
														// the first task
		if (ontologyType == VAVariables.ontologyType.Source) {
			Ontology sourceOntology = currentTask.matcherResult
					.getSourceOntology();
			rootNode = sourceOntology.getClassesRoot();
		} else {
			Ontology targetOntology = currentTask.matcherResult
					.getTargetOntology();
			rootNode = targetOntology.getClassesRoot();
		}

		return rootNode;
	}

}
