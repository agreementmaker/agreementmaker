package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

import am.va.graph.VAVariables.ontologyType;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
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

	private static JFrame frame;
	private static JFXPanel fxPanel;
	private static ListView<String> listView;
	private static Group root;
	private static VAGroup rootGroupLeft;
	private static VAGroup rootGroupRight;
	private static VAGroup previousGroup;
	private static VAGroup currentGroup;
	private static int stop = -1;

	private static Button btnRoot;
	private static Button btnUp;
	private static Button btnHelp;

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

		// Top side: HBox, contains toolbar (buttons & choiceBox & searchBox)
		ToolBar toolbar = new ToolBar();
		Region spacer1 = new Region();
		spacer1.setStyle("-fx-padding: 0 8em 0 0;");
		Region spacer2 = new Region();
		spacer2.setStyle("-fx-padding: 0 8em 0 0;");
		Region spacer3 = new Region();
		spacer3.setStyle("-fx-padding: 0 20em 0 0;");
		HBox buttonBar = new HBox();

		// set three buttons
		btnRoot = new Button("Top level");
		btnUp = new Button("Go up");
		btnHelp = new Button("Help");
		setButtonActions();
		buttonBar.getChildren().addAll(btnRoot, btnUp, btnHelp);

		// set choice box
		cbOntology = new ChoiceBox<String>();
		cbOntology.getItems().addAll("Class", "Properity");
		cbOntology.getSelectionModel().selectFirst();
		setChoiceBoxActions();

		// set search box
		BorderPane searchboxborderPane = new BorderPane();
		searchBox = new VASearchBox();
		searchBox.getStyleClass().add("search-box");
		searchboxborderPane.setRight(searchBox);
		HBox.setMargin(searchBox, new Insets(0, 5, 0, 0));
		toolbar.getItems().addAll(spacer1, buttonBar, spacer2, cbOntology,
				spacer3, searchboxborderPane);
		borderPane.setTop(toolbar);

		// Center side: two piecharts as a group, tilepane layout is used
		Group chartGroup = new Group();
		TilePane tilePane = new TilePane();
		tilePane.setPrefColumns(2); // preferred columns

		// set two pies
		chartLeft = new VAPieChart(rootGroupLeft);
		chartLeft.getPieChart().setClockwise(false);
		chartRight = new VAPieChart(rootGroupRight);
		chartRight.getPieChart().setClockwise(false);
		lblSource = new Label("Source ontology", chartLeft.getPieChart());
		lblSource.setContentDisplay(ContentDisplay.CENTER);
		lblTarget = new Label("Target ontology", chartRight.getPieChart());
		lblTarget.setContentDisplay(ContentDisplay.CENTER);

		// tooltip
		pieTooltip = new Tooltip("click to view more");
		for (final PieChart.Data currentData : chartLeft.getPieChart()
				.getData()) {
			Tooltip.install(currentData.getNode(), getPieTooltip());
		}
		tilePane.getChildren().add(lblSource);
		tilePane.getChildren().add(lblTarget);
		chartGroup.getChildren().add(tilePane);
		borderPane.setCenter(chartGroup);

		root.getChildren().add(borderPane);
		fxPanel.setScene(myScene);

		// update pie data
		updatePreviousGroup(rootGroupLeft);
		updateCurrentGroup(rootGroupLeft);
		chartLeft.updatePieChart(ontologyType.Source);

		// myScene.getStylesheets().add(Ensemble2.class.getResource("ensemble2.css").toExternalForm());
		// myScene.getStylesheets().add(VAPanel.class.getResource("VA.css").toExternalForm());
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
		return listView;
	}

	public static void setListView(ListView<String> list) {
		listView = list;
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

	public static void setTargetLabel(String label, int empty) {
		lblTarget.setText(label);
		if (empty == 1) {
			lblTarget.setFont(Font.font("Verdana", 20));
			lblTarget.setTextFill(Color.RED);
		} else {
			lblTarget.setFont(Font.font("Verdana", 15));
			lblTarget.setTextFill(Color.WHITESMOKE);
		}
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
	}
}
