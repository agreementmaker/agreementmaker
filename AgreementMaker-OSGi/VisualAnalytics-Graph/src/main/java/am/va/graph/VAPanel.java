package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

import am.va.graph.VAVariables.ontologyType;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

//import ensemble.Ensemble2;

public class VAPanel {

	private static JFrame frameMain;
	private static JFrame frameBubble;
	private static JFXPanel fxPanel;
	private static ListView<String> listViewLeft;
	private static Group root;
	private static Scene myScene;
	private static VAGroup rootGroupLeft;
	private static VAGroup rootGroupRight;
	private static VAGroup previousGroup;
	private static VAGroup currentGroup;
	private static int stop = -1;

	private static Button btnRoot;
	private static Button btnUp;
	private static Button btnBubble;

	private static ChoiceBox<String> cbOntology;

	private static Label lblSource;
	private static Label lblTarget;

	private static VAPieChart chartLeft;
	private static VAPieChart chartRight;
	private static Tooltip pieTooltip;
	private static VASearchBox searchBox;

	/**
	 * Init Frame
	 */
	public static void initAndShowGUI() {
		frameMain = new JFrame("VA");
		frameMain.setSize(1200, 550);
		frameMain.setLocation(100, 100);
		fxPanel = new JFXPanel();
		frameMain.add(fxPanel);
		frameMain.setVisible(true);

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
		myScene = new Scene(root);
		setLayout();
		fxPanel.setScene(myScene);

		updatePreviousGroup(rootGroupLeft);
		updateCurrentGroup(rootGroupLeft);
		chartLeft.updatePieChart(ontologyType.Source);
	}

	private static void setLayout() {
		// Main layout: BorderPane
		BorderPane borderPane = new BorderPane();
		initLeftAddList(borderPane);
		initTopToolbar(borderPane);
		initCenterTwoPies(borderPane);

		root.getChildren().add(borderPane);
	}

	private static void initLeftAddList(BorderPane borderPane) {
		listViewLeft = new ListView<String>();
		listViewLeft.setPrefHeight(500);
		listViewLeft.setPrefWidth(100);
		listViewLeft.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		borderPane.setLeft(listViewLeft);
	}

	private static void initTopToolbar(BorderPane borderPane) {
		ToolBar toolbar = new ToolBar();
		Region spacer1 = new Region();
		spacer1.setStyle("-fx-padding: 0 8em 0 0;");
		Region spacer2 = new Region();
		spacer2.setStyle("-fx-padding: 0 8em 0 0;");
		Region spacer3 = new Region();
		spacer3.setStyle("-fx-padding: 0 20em 0 0;");
		HBox buttonBar = new HBox();
		initButtons(buttonBar);
		initChoiceBox();
		BorderPane searchboxborderPane = new BorderPane();
		initSearchBox();
		searchboxborderPane.setRight(searchBox);
		HBox.setMargin(searchBox, new Insets(0, 5, 0, 0));
		toolbar.getItems().addAll(spacer1, buttonBar, spacer2, cbOntology,
				spacer3, searchboxborderPane);

		borderPane.setTop(toolbar);
	}

	private static void initButtons(HBox buttonBar) {
		btnRoot = new Button("Top level");
		btnUp = new Button("Go back");
		btnBubble = new Button("Bubble");
		setButtonActions();
		buttonBar.getChildren().addAll(btnRoot, btnUp, btnBubble);

	}

	private static void initChoiceBox() {
		cbOntology = new ChoiceBox<String>();
		cbOntology.getItems().addAll("Class", "Properity");
		cbOntology.getSelectionModel().selectFirst();
		setChoiceBoxActions();
	}

	private static void initSearchBox() {
		searchBox = new VASearchBox();
		searchBox.getStyleClass().add("search-box");
	}

	private static void initCenterTwoPies(BorderPane borderPane) {
		Group chartGroup = new Group();
		TilePane tilePane = new TilePane();
		tilePane.setPrefColumns(2); // preferred columns

		chartLeft = new VAPieChart(rootGroupLeft);
		chartLeft.getPieChart().setClockwise(false);
		chartRight = new VAPieChart(rootGroupRight);
		chartRight.getPieChart().setClockwise(false);
		lblSource = new Label("Source ontology", chartLeft.getPieChart());
		lblSource.setContentDisplay(ContentDisplay.CENTER);
		lblTarget = new Label("Target ontology", chartRight.getPieChart());
		lblTarget.setContentDisplay(ContentDisplay.CENTER);
		tilePane.getChildren().add(lblSource);
		tilePane.getChildren().add(lblTarget);
		chartGroup.getChildren().add(tilePane);
		borderPane.setCenter(chartGroup);
		initTooltip();
	}

	private static void initTooltip() {
		pieTooltip = new Tooltip("click to view more");
		for (final PieChart.Data currentData : chartLeft.getPieChart()
				.getData()) {
			Tooltip.install(currentData.getNode(), getPieTooltip());
		}
	}

	/**
	 * Generate new VAGroup according user's click
	 * 
	 * @param currentGroup
	 * @return
	 */
	public static void getNewGroup(VAVariables.ontologyType ontologyType) {
		// Need a function here, return value:VAData
		VAData newRootData = VAPieChart.getSelectedVAData();
		System.out.println("New data " + newRootData.getNodeName());
		VAGroup newGroup = new VAGroup();
		newGroup.setRootNode(newRootData);
		if (newRootData != null && newRootData.hasChildren()) {
			newGroup.setParent(currentGroup.getGroupID());
			newGroup.setListVAData(VASyncData.getChildrenData(newRootData,
					ontologyType));
		} else {
			newGroup.setParent(previousGroup.getGroupID());
		}
		updateCurrentGroup(newGroup);
	}

	private static void updatePreviousGroup(VAGroup group) {
		if (group != null) {
			if (btnUp.isDisable()) {
				btnUp.setDisable(false);
			}
			previousGroup = group;

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
		if (stop != -1)
			updatePreviousGroup(currentGroup);
		currentGroup = group;
	}

	/**
	 * Init rootGroup
	 * 
	 * @param group
	 */
	public static void setRootGroupLeft(VAGroup group) {
		rootGroupLeft = group;
	}

	public static void setRootGroupRight(VAGroup group) {
		rootGroupRight = group;
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

	public static Tooltip getPieTooltip() {
		return pieTooltip;
	}

	public static void setPieTooltip(Tooltip pieTooltip) {
		VAPanel.pieTooltip = pieTooltip;
	}

	public static void setStop(int i) {
		stop = i;
	}

	public static ListView<String> getlistView() {
		return listViewLeft;
	}

	public static void setListView(ListView<String> list) {
		listViewLeft = list;
	}

	public static void setSourceLabel(String label, int empty) {
		lblSource.setText(label);
		if (empty == 1) {
			lblSource.setFont(Font.font("Verdana", 20));
			lblSource.setTextFill(Color.RED);
		} else {
			lblSource.setFont(Font.font("Verdana", 15));
			lblSource.setTextFill(Color.WHITESMOKE);
		}
	}

	public static void setTargetLabel(String label) {
		lblTarget.setText(label);
		lblTarget.setFont(Font.font("Verdana", 20));
		lblTarget.setTextFill(Color.RED);
	}

	public static VAPieChart getRightPie() {
		return chartRight;
	}

	public static VAGroup getRightRootGroup() {
		return rootGroupRight;
	}

	/**
	 * Add event for choice box
	 */
	private static void setChoiceBoxActions() {
		cbOntology.getSelectionModel().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(
							ObservableValue<? extends Number> observableValue,
							Number number, Number number2) {
						String choice = cbOntology.getItems().get(
								(Integer) number2);
						if (choice.equals(VAVariables.nodeType.Class.toString())) {
							VASyncListener
									.setNodeType(VAVariables.nodeType.Class);
						} else {
							VASyncListener
									.setNodeType(VAVariables.nodeType.Property);
						}
						VASyncListener.InitData();
						updateCurrentGroup(rootGroupLeft);
						chartLeft.updatePieChart(ontologyType.Source);
					}
				});
	}

	/**
	 * Add event for buttons
	 */
	private static void setButtonActions() {
		/**
		 * Go to root panel
		 */
		btnRoot.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				updateCurrentGroup(rootGroupLeft);
				chartLeft.updatePieChart(ontologyType.Source);
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
				chartLeft.updatePieChart(ontologyType.Source);
				System.out.println("Go to previous panel");
				btnUp.setDisable(true);
			}

		});

		btnBubble.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				frameBubble = new JFrame("Bubble");
				frameBubble.setSize(500, 550);
				frameBubble.setLocation(150, 150);
				JFXPanel fxPanel = new JFXPanel();
				frameBubble.add(fxPanel);
				frameBubble.setVisible(true);
				Group root = new Group();
				Scene scene = new Scene(root);

				NumberAxis xAxis = new NumberAxis("X", 0d, 150d, 20d);

				NumberAxis yAxis = new NumberAxis("Y", 0d, 150d, 20d);

				@SuppressWarnings({ "rawtypes", "unchecked" })
				ObservableList<BubbleChart.Series> bubbleChartData = FXCollections.observableArrayList(
						new BubbleChart.Series("Series 1", FXCollections
								.observableArrayList(new XYChart.Data(30d, 40d,
										10d), new XYChart.Data(60d, 20d, 13d),
										new XYChart.Data(10d, 90d, 7d),
										new XYChart.Data(100d, 40d, 10d),
										new XYChart.Data(50d, 23d, 5d))),
						new BubbleChart.Series("Series 2", FXCollections
								.observableArrayList(new XYChart.Data(20d,
										100d, 5d))));

				BubbleChart chart = new BubbleChart(xAxis, yAxis,
						bubbleChartData);
				xAxis.setVisible(false);
				fxPanel.setScene(scene);
				root.getChildren().add(chart);
			}
		});
	}
}
