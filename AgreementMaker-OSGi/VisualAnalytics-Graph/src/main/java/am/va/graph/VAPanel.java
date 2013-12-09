package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

import com.sun.media.jfxmedia.events.NewFrameEvent;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;

public class VAPanel {

	private static JFrame frame;
	private static JFXPanel fxPanel;
	private static ListView<String> listView;
	private static Group root;
	private static VAGroup rootGroup;
	private static VAGroup previousGroup;
	private static VAGroup currentGroup;
	private static int count = 1;
	private static int stop = -1;

	private static Button btnRoot;
	private static Button btnUp;
	private static Button btn3;

	private static VAPieChart chartLeft;
	private static PieChart chartRight;

	/**
	 * Init Frame
	 */
	public static void initAndShowGUI() {
		frame = new JFrame("VA");
		frame.setSize(1100, 550);
		frame.setLocation(100, 100);
		fxPanel = new JFXPanel();
		frame.add(fxPanel);
		frame.setVisible(true);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				InitFX();
			}
		});
	}

	/**
	 * Init JavaFx panel, add mouse click Eventhandler
	 */
	public static void InitFX() {
		root = new Group();

		Scene myScene = new Scene(root);

		// Main layout: BorderPane
		BorderPane borderPane = new BorderPane();

		// left side: listView
		listView = new ListView<String>();
		listView.setPrefHeight(500);
		listView.setPrefWidth(100);
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		borderPane.setLeft(listView);

		// Top side: HBox, contains toolbar
		ToolBar toolbar = new ToolBar();
		Region spacer = new Region();
		spacer.setStyle("-fx-padding: 0 7em 0 0;");
		spacer.getStyleClass().setAll("spacer");
		HBox buttonBar = new HBox();
		btnRoot = new Button("Top level");
		btnUp = new Button("Go up");
		btn3 = new Button("Help");
		setButtonActions();
		buttonBar.getChildren().addAll(btnRoot, btnUp, btn3);
		toolbar.getItems().addAll(spacer, buttonBar);
		borderPane.setTop(toolbar);

		// Center side: two piecharts as a group, tilepane layout is used
		Group chartGroup = new Group();
		TilePane tilePane = new TilePane();
		tilePane.setPrefColumns(2); // preferred columns
		chartLeft = new VAPieChart(rootGroup);
		chartLeft.getPieChart().setClockwise(false);
		chartRight = testPieChart();
		tilePane.getChildren().add(chartLeft.getPieChart());
		tilePane.getChildren().add(chartRight);
		chartGroup.getChildren().add(tilePane);
		borderPane.setCenter(chartGroup);

		root.getChildren().add(borderPane);
		fxPanel.setScene(myScene);
		updateCurrentGroup(rootGroup);
		setLocation(chartLeft);
		// TEST(currentGroup);
		chartLeft.updatePieChart();
	}

	private static PieChart testPieChart() {
		ObservableList<PieChart.Data> pieChartData = FXCollections
				.observableArrayList(new PieChart.Data("Sun", 20),
						new PieChart.Data("IBM", 12), new PieChart.Data("HP",
								25), new PieChart.Data("Dell", 22),
						new PieChart.Data("Apple", 30));
		PieChart chart = new PieChart(pieChartData);
		chart.setClockwise(false);
		return chart;
	}

	/**
	 * Set radius and center point for Pie chart
	 * 
	 * @param chart
	 */
	private static void setLocation(VAPieChart chart) {
		// TODO Auto-generated method stub
		double minX = Double.MAX_VALUE;
		double maxX = Double.MAX_VALUE * -1;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MAX_VALUE * -1;

		for (PieChart.Data d : chart.getPieChart().getData()) {
			minX = Math.min(minX, d.getNode().getBoundsInParent().getMinX());
			maxX = Math.max(maxX, d.getNode().getBoundsInParent().getMaxX());
			minY = Math.min(minY, d.getNode().getBoundsInParent().getMinY());
			maxY = Math.max(maxY, d.getNode().getBoundsInParent().getMaxY());
		}

		double radius = (maxX - minX) / 2;
		chart.setRadius(radius);
		chart.setPieCenter(new Point2D(minX + radius, minY + radius));
		// System.out.println("radius " + radius + " center "+
		// chart.getPieCenter());
	}

	/**
	 * Generate new VAGroup according user's click
	 * 
	 * @param currentGroup
	 * @return
	 */
	public static void getNewGroup() {
		// Need a function here, return value:VAData
		VAData newRootData = VAPieChart.getSelectedVAData();
		System.out.println("New data "
				+ newRootData.getSourceNode().getLocalName());
		count++;
		if (newRootData != null
				&& newRootData.getSourceNode().getChildCount() > 0) { // if
																		// there's
																		// still
																		// new
																		// group
			VAGroup newGroup = new VAGroup();
			if (currentGroup != null)
				newGroup.setParent(currentGroup.getGroupID());
			else
				newGroup.setParent(previousGroup.getGroupID());
			newGroup.setRootNode(newRootData);
			newGroup.setListVAData(VASyncData.getChildrenData(newRootData));
			updateCurrentGroup(newGroup);
		} else {
			updateCurrentGroup(null);
		}
	}

	private static void updatePreviousGroup(VAGroup group) {
		if (group != null) {
			if (btnUp.isDisable()) {
				btnUp.setDisable(false);
			}
			previousGroup = new VAGroup();
			previousGroup.setParent(group.getParent());
			previousGroup.setRootNode(group.getRootNode());
			previousGroup.setListVAData(group.getVADataArray());
		} else {
			System.out.println("- Previous group is empty ?!!");
		}
	}

	/**
	 * Update current group
	 * 
	 * @param group
	 */
	private static void updateCurrentGroup(VAGroup group) {
		updatePreviousGroup(currentGroup);
		if (group != null) {
			currentGroup = new VAGroup();
			currentGroup.setParent(group.getParent());
			currentGroup.setRootNode(group.getRootNode());
			currentGroup.setListVAData(group.getVADataArray());
		} else {
			System.out.println("New group is NULL");
			currentGroup = null;
		}
	}

	/**
	 * Init rootGroup
	 * 
	 * @param group
	 */
	public static void setRootGroup(VAGroup group) {
		rootGroup = group;
	}

	/**
	 * Print info. Testing only.
	 * 
	 * @param rootGroup
	 */
	public static void testVAGroup(VAGroup rootGroup) {
		System.out.println("-----------------------------");
		if (rootGroup != null) {
			String rootNodeName = rootGroup.getRootNode().getSourceNode()
					.getLocalName();
			System.out.println(rootNodeName);
			ArrayList<VAData> vaData = rootGroup.getVADataArray();
			for (VAData d : vaData) {
				System.out.println(d.getSourceNode().getLocalName() + ","
						+ d.getTargetNode().getLocalName() + ","
						+ d.getSimilarity());
			}
			HashMap<String, Integer> slots = rootGroup.getslotCountMap();
			for (String s : VAVariables.thresholdName) {
				if (slots.containsKey(s))
					System.out.println(s + ":" + slots.get(s));
			}
		} else {
			System.out.println("Empty group!");
		}
	}

	public static VAGroup getCurrentGroup() {
		return currentGroup;
	}

	public static int getStop() {
		return stop;
	}

	public static Group getFXGroup() {
		return root;
	}

	public static void setStop(int i) {
		stop = i;
	}

	public static ListView<String> getlistView() {
		return listView;
	}

	public static void setListView(ListView<String> list) {
		listView = list;
	}

	private static void setButtonActions() {
		/**
		 * Go to root panel
		 */
		btnRoot.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				updateCurrentGroup(rootGroup);
				chartLeft.updatePieChart();
				System.out.println("Go to root panel");
			}

		});

		/**
		 * Go to previous panel (can only click once)
		 */
		btnUp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				updateCurrentGroup(previousGroup);
				chartLeft.updatePieChart();
				System.out.println("Go to previous panel");
				btnUp.setDisable(true);
			}

		});
	}
}
