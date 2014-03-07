package am.va.graph;

import java.util.ArrayList;
import javax.swing.SwingUtilities;
import am.va.graph.VAVariables.ontologyType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;

//import ensemble.Ensemble2;

/**
 * The main panel of the system
 * 
 * @author Yiting
 * 
 */
public class VAPanel {

	private static JFXPanel fxPanel;
	private static ListView<String> listViewLeft;
	private static TreeView<String> listTreeLeft;
	private static Group root;
	private static Scene myScene;

	static double screenWidth;
	static double screenHeight;

	private static Button btnRoot;
	private static Button btnUp;
	private static Button btnUFB;
	private static ToggleButton btnPages[] = new ToggleButton[7];
	private static ChoiceBox<String> cbOntology;
	private static Label lblSource1;
	private static Label lblTarget1;
	private static VASearchBox searchBox;
	private static Tooltip pieTooltip;

	private static VAPieChart chartLeft1;
	private static VAPieChart chartRight1;

	private static VAGroup rootGroupLeft1;
	private static VAGroup rootGroupRight1;
	private static VAGroup previousGroup;
	private static VAGroup currentGroup;
	private static int stop = -1;

	/**
	 * Start showing the panel on the tab
	 */
	public static void initButNotShow() {
		fxPanel = new VATab();
		screenWidth = Screen.getPrimary().getVisualBounds().getWidth() - 25;
		screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				InitFX();
			}
		});
	}

	public static JFXPanel getFxPanel() {
		return fxPanel;
	}

	/**
	 * Init JavaFx panel, add mouse click Event handler
	 */
	public static void InitFX() {
		root = new Group();
		myScene = new Scene(root);
		String sceneCss = VAPanel.class.getResource("VA.css").toExternalForm();
		myScene.getStylesheets().add(sceneCss);
		setLayout();
		fxPanel.setScene(myScene);
		updatePreviousGroup(rootGroupLeft1);
		updateCurrentGroup(rootGroupLeft1);
		chartLeft1.updatePieChart(ontologyType.Source);
		generateParentGroup();
	}

	// =====================Graphic User Interface========================

	/**
	 * Set the main panel layout, here we use BorderPane
	 */
	private static void setLayout() {
		BorderPane borderPane = new BorderPane();
		borderPane.setPrefSize(screenWidth, screenHeight);
		initCenterSplitPanel(borderPane); // pie chart & lists panel
		initRightFlowPane(borderPane); // preview panel
		initTopToolbar(borderPane); // tool bar panel
		root.getChildren().add(borderPane);
	}

	/**
	 * Init center panel, including two lists, two sets of pie charts
	 * 
	 * @param borderPane
	 */
	private static void initCenterSplitPanel(BorderPane borderPane) {
		SplitPane splitPane = new SplitPane();
		// splitPane.getItems().addAll(getLeftSplitPane(), getCenterGroup());
		splitPane.getItems().addAll(getLeftSplitPane(), getCenterSplitPane());
		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.setDividerPosition(0, 0.2);
		borderPane.setCenter(splitPane);
	}

	/**
	 * Left split panel, contains a treeview and a listview
	 * @return
	 */
	private static SplitPane getLeftSplitPane() {
		SplitPane splitPane = new SplitPane();
		splitPane.getItems().addAll(getTreeView(), getListView());
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPosition(0, 0.5);
		return splitPane;
	}
	
	/**
	 * The treeview (up)
	 * 
	 * @return
	 */
	private static TreeView<String> getTreeView() {
		listTreeLeft = new TreeView<String>();
		return listTreeLeft;
	}

	/**
	 * The listview (down)
	 * @return
	 */
	private static ListView<String> getListView() {
		listViewLeft = new ListView<String>();
		listViewLeft.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		// tilePane.getChildren().add(listViewLeft);
		return listViewLeft;
	}

	/**
	 * Center split panel, contains two sets of pie charts
	 * @return
	 */
	private static SplitPane getCenterSplitPane() {
		SplitPane splitPane = new SplitPane();
		splitPane.getItems().addAll(getCenterGroupUp(), getCenterGroupDown());
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPosition(0, 0.3);
		return splitPane;
	}

	/**
	 * Up Tile panel, contains the first set of pie charts
	 * @return
	 */
	private static Group getCenterGroupUp() {
		Group chartGroup = new Group();
		TilePane tilePane = new TilePane();
		tilePane.setPrefColumns(2); // preferred columns

		chartLeft1 = new VAPieChart(rootGroupLeft1);
		chartLeft1.getPieChart().setClockwise(false);
		chartRight1 = new VAPieChart(rootGroupRight1);
		chartRight1.getPieChart().setClockwise(false);
		lblSource1 = new Label("Source ontology", chartLeft1.getPieChart());
		lblSource1.setContentDisplay(ContentDisplay.CENTER);
		lblTarget1 = new Label("Target ontology", chartRight1.getPieChart());
		lblTarget1.setContentDisplay(ContentDisplay.CENTER);

		tilePane.getChildren().addAll(lblSource1, lblTarget1);
		chartGroup.getChildren().add(tilePane);
		initTooltip(chartLeft1);
		return chartGroup;
	}

	/**
	 * Down Tile panel, contains the second set of pie charts
	 * @return
	 */
	private static Group getCenterGroupDown() {
		Group chartGroup = new Group();
		TilePane tilePane = new TilePane();
		tilePane.setPrefColumns(2); // preferred columns

		chartLeft1 = new VAPieChart(rootGroupLeft1);
		chartLeft1.getPieChart().setClockwise(false);
		chartRight1 = new VAPieChart(rootGroupRight1);
		chartRight1.getPieChart().setClockwise(false);
		lblSource1 = new Label("Source ontology", chartLeft1.getPieChart());
		lblSource1.setContentDisplay(ContentDisplay.CENTER);
		lblTarget1 = new Label("Target ontology", chartRight1.getPieChart());
		lblTarget1.setContentDisplay(ContentDisplay.CENTER);

		tilePane.getChildren().addAll(lblSource1, lblTarget1);
		chartGroup.getChildren().add(tilePane);
		initTooltip(chartLeft1);
		return chartGroup;
	}
	
	/**
	 * Add tooltip to the chart.
	 */
	private static void initTooltip(VAPieChart chartLeft) {
		pieTooltip = new Tooltip("click to view more");
		for (final PieChart.Data currentData : chartLeft.getPieChart()
				.getData()) {
			Tooltip.install(currentData.getNode(), getPieTooltip());
		}
	}

	

	/**
	 * Init Top panel, including a toolbar that consists of buttons, a choice
	 * box and a search box.
	 * 
	 * @param borderPane
	 */
	private static void initTopToolbar(BorderPane borderPane) {
		ToolBar toolbar = new ToolBar();
		// toolbar.setPrefWidth(screenWidth);
		Region spacer1 = new Region();
		spacer1.setStyle("-fx-padding: 0 20em 0 0;");
		Region spacer2 = new Region();
		spacer2.setStyle("-fx-padding: 0 10em 0 0;");
		Region spacer3 = new Region();
		spacer3.setStyle("-fx-padding: 0 10em 0 0;");
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

	/**
	 * Init buttons in the HBox
	 * @param buttonBar
	 */
	private static void initButtons(HBox buttonBar) {
		btnRoot = new Button("Top level");
		btnUp = new Button("Go back");
		btnUFB = new Button("User feedback");
		setToolButtonActions();
		buttonBar.getChildren().addAll(btnRoot, btnUp, btnUFB);
	}

	/**
	 * Init choice box
	 */
	private static void initChoiceBox() {
		cbOntology = new ChoiceBox<String>();
		cbOntology.getItems().addAll("Class", "Properity");
		cbOntology.getSelectionModel().selectFirst();
		setChoiceBoxActions();
	}

	/**
	 * Init search box
	 */
	private static void initSearchBox() {
		searchBox = new VASearchBox();
		searchBox.setId("SearchBox");
	}

	/**
	 * Init right panel, using buttons to represent different matching algorithms
	 * 
	 * @return
	 */
	public static void initRightFlowPane(BorderPane borderPane) {
		FlowPane flow = new FlowPane(Orientation.VERTICAL);
		flow.setPadding(new Insets(5, 0, 5, 0));
		flow.setVgap(8);
		flow.setHgap(4);
		flow.setPrefWrapLength(170); // preferred width allows for two columns
		flow.setStyle("-fx-background-color: DAE6F3;");
		int size = 50;
		final ToggleGroup group = new ToggleGroup();
		for (int i = 0; i < btnPages.length; i++) {
			btnPages[i] = new ToggleButton("AL" + String.valueOf(i + 1));
			btnPages[i].setStyle("-fx-font-size: " + size + "pt;");
			btnPages[i].setToggleGroup(group);
			if (i >= VASyncData.getTotalDisplayNum()) {// init visibility
				btnPages[i].setVisible(false);
				System.out.println("[test]-display num="
						+ VASyncData.getTotalDisplayNum() + ", i=" + i);
			}
			flow.getChildren().add(btnPages[i]);
		}
		setPageButtonActions();
		btnPages[0].setSelected(true);
		borderPane.setRight(flow);
	}

	

	// =================Pie Chart related logic=================

	/**
	 * Generate new VAGroup according to user's click
	 * 
	 * @param currentGroup
	 * @return
	 */
	public static void generateNewGroup(VAVariables.ontologyType ontologyType,
			VAData newRootData) {
		// Need a function here, return value:VAData
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
		/**
		 * Update list, being called twice ?!
		 */
		if (ontologyType == VAVariables.ontologyType.Source)
			generateParentGroup();
		chartLeft1.updatePieChart(ontologyType);
	}

	/**
	 * Generate parent group and update tree view
	 */
	public static void generateParentGroup() {
		VAGroup parentGroup = null;
		if (currentGroup.getCurrentLevel() < 1) {
			// System.out.println("Generate Parent: parent=root");
			parentGroup = rootGroupLeft1;
		} else {
			// System.out.println("Generate Parent: new parent");
			parentGroup = new VAGroup();
			VAData parentData = VASyncData.getParentVAData(currentGroup
					.getRootNode());
			parentGroup.setRootNode(parentData);
			parentGroup.setListVAData(VASyncData.getChildrenData(parentData,
					VAVariables.ontologyType.Source));
		}
		generateNewTree(parentGroup);
	}

	/**
	 * Generate new Tree view
	 * 
	 * @param parentGroup
	 */
	private static void generateNewTree(VAGroup parentGroup) {
		TreeItem<String> listTreeLeftRoot = null;
		String label = "";
		if (parentGroup.getParent() == 0)
			label = "Source Root";
		else
			label = parentGroup.getRootNodeName();
		// System.out.println("Generate Parent: label=" + label);
		listTreeLeftRoot = new TreeItem<String>(label);
		ArrayList<VAData> data = parentGroup.getVADataArray();
		for (VAData d : data) {
			listTreeLeftRoot.getChildren().add(
					new TreeItem<String>(d.getNodeName()));
		}

		listTreeLeft.setShowRoot(true);
		listTreeLeft.setRoot(listTreeLeftRoot);
		listTreeLeftRoot.setExpanded(true);
		treeviewAction(data);
	}

	/**
	 * Add tree view actions (refresh pie chart and related data)
	 * 
	 * @param data
	 */
	private static void treeviewAction(final ArrayList<VAData> data) {
		listTreeLeft.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Object>() {

					@Override
					public void changed(ObservableValue<?> observable,
							Object oldValue, Object newValue) {

						TreeItem<String> selectedItem = (TreeItem<String>) newValue;
						if (selectedItem != null) {
							String selected = selectedItem.getValue();
							for (VAData da : data) {
								if (da.getNodeName().equals(selected)) {
									VAGroup newGroup = new VAGroup();
									newGroup.setRootNode(da);
									newGroup.setParent(currentGroup
											.getGroupID());
									newGroup.setListVAData(VASyncData
											.getChildrenData(da,
													ontologyType.Source));
									updateAllWithNewGroup(newGroup);
								}
							}
						}
					}

				});
	}

	/**
	 * Update previous group
	 * 
	 * @param group
	 */
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
		rootGroupLeft1 = group;
	}

	public static void setRootGroupRight(VAGroup group) {
		rootGroupRight1 = group;
	}

	/**
	 * Get current group
	 * 
	 * @return
	 */
	public static VAGroup getCurrentGroup() {
		return currentGroup;
	}

	/**
	 * The stop is just a on and off variable
	 * 
	 * @param i
	 */
	public static void setStop(int i) {
		stop = i;
	}

	public static int getStop() {
		return stop;
	}

	public static Tooltip getPieTooltip() {
		return pieTooltip;
	}

	public static void setPieTooltip(Tooltip pieTooltip) {
		VAPanel.pieTooltip = pieTooltip;
	}

	public static ListView<String> getlistView() {
		return listViewLeft;
	}

	public static void setListView(ListView<String> list) {
		listViewLeft = list;
	}

	/**
	 * Set source pie chart's label
	 * 
	 * @param label
	 * @param empty
	 */
	public static void setSourceLabel(String label, int empty) {
		lblSource1.setText(label);
		if (empty == 1) {
			lblSource1.setFont(Font.font("Verdana", 20));
			lblSource1.setTextFill(Color.RED);
		} else {
			lblSource1.setFont(Font.font("Verdana", 15));
			lblSource1.setTextFill(Color.WHITESMOKE);
		}
	}

	/**
	 * Set target pie chart's label
	 * 
	 * @param label
	 */
	public static void setTargetLabel(String label) {
		lblTarget1.setText(label);
		lblTarget1.setFont(Font.font("Verdana", 20));
		lblTarget1.setTextFill(Color.RED);
	}

	/**
	 * Get the right pie chart (display only)
	 * 
	 * @return
	 */
	public static VAPieChart getRightPie() {
		return chartRight1;
	}

	/**
	 * Get the right pie chart's root group (display only)
	 * 
	 * @return
	 */
	public static VAGroup getRightRootGroup() {
		return rootGroupRight1;
	}

	/**
	 * Update the pie chart panel with a new group
	 * 
	 * @param newGroup
	 */
	public static void updateAllWithNewGroup(VAGroup newGroup) {
		updateCurrentGroup(newGroup);
		chartLeft1.updatePieChart(ontologyType.Source);
		generateParentGroup();
		chartLeft1.clearList();
	}

	// ==============================Button & List
	// Event=====================================================

	/**
	 * Haven't been used yet
	 * 
	 * @param n
	 */
	public static void setButtonVisible(int n) {
		if (n < btnPages.length && btnPages != null && btnPages[n] != null)
			btnPages[n].setVisible(true);
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
						updateAllWithNewGroup(rootGroupLeft1);
					}
				});
	}

	private static void setPageButtonActions() {
		int num = btnPages.length;
		for (int i = 0; i < num; i++) {
			final int cur = i + 1;
			btnPages[i].setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					// TODO Auto-generated method stub
					VASyncData.setCurrentDisplayNum(cur);
					VASyncListener.InitData();
					updateAllWithNewGroup(rootGroupLeft1);
					// set colors here

				}

			});
		}
	}

	/**
	 * Add event for buttons
	 */
	private static void setToolButtonActions() {
		/**
		 * Go to root panel
		 */
		btnRoot.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				updateAllWithNewGroup(rootGroupLeft1);
			}

		});

		/**
		 * Go to previous panel (can only click once)
		 */
		btnUp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				updateAllWithNewGroup(rootGroupLeft1);
				btnUp.setDisable(true);
			}

		});

		btnUFB.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// VAUserFeedBack ufbFrame = new VAUserFeedBack();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new VAUserFeedBack();
					}
				});
			}

		});
	}
}
