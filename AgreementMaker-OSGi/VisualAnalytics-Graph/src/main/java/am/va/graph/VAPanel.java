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
	private static VAGroup rootGroupLeft;
	private static VAGroup rootGroupRight;
	private static VAGroup previousGroup;
	private static VAGroup currentGroup;
	private static int stop = -1;

	private static Button btnRoot;
	private static Button btnUp;
	private static Button btnUFB;
	private static Button btnPages[] = new Button[7];

	private static ChoiceBox<String> cbOntology;

	private static Label lblSource;
	private static Label lblTarget;

	private static VAPieChart chartLeft;
	private static VAPieChart chartRight;
	private static Tooltip pieTooltip;
	private static VASearchBox searchBox;

	static double screenWidth;
	static double screenHeight;

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
		updatePreviousGroup(rootGroupLeft);
		updateCurrentGroup(rootGroupLeft);
		chartLeft.updatePieChart(ontologyType.Source);
		generateParentGroup();
	}

	// ==================================Graphic User
	// Interface=======================================================

	/**
	 * Set the main panel layout, here we use BorderPane
	 */
	private static void setLayout() {
		BorderPane borderPane = new BorderPane();
		borderPane.setPrefSize(screenWidth, screenHeight);
		initTopToolbar(borderPane); // tool bar panel
		initCenterSplitPanel(borderPane); // pie chart & lists panel
		initDownArrayPanel(borderPane); // preview panel
		root.getChildren().add(borderPane);
	}

	/**
	 * Init center panel, including two lists and two pie charts
	 * 
	 * @param borderPane
	 */
	private static void initCenterSplitPanel(BorderPane borderPane) {
		SplitPane splitPane = new SplitPane();
		splitPane.getItems().addAll(getSplitPane(), getCenterGroup());
		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.setDividerPosition(0, 0.7);
		borderPane.setCenter(splitPane);
	}

	private static SplitPane getSplitPane() {
		SplitPane splitPane = new SplitPane();
		splitPane.getItems().addAll(getTreeView(), getListView());
		splitPane.setOrientation(Orientation.VERTICAL);
		return splitPane;
	}

	private static Group getCenterGroup() {
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
		initTooltip();
		return chartGroup;
	}

	/**
	 * Add listview and treeview to the TilePane
	 * 
	 * @return
	 */
	private static TreeView<String> getTreeView() {
		listTreeLeft = new TreeView<String>();
		return listTreeLeft;
	}

	private static ListView<String> getListView() {
		listViewLeft = new ListView<String>();
		listViewLeft.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		// tilePane.getChildren().add(listViewLeft);
		return listViewLeft;
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

	private static void initButtons(HBox buttonBar) {
		btnRoot = new Button("Top level");
		btnUp = new Button("Go back");
		btnUFB = new Button("User feedback");
		setToolButtonActions();
		buttonBar.getChildren().addAll(btnRoot, btnUp, btnUFB);
	}

	private static void initChoiceBox() {
		cbOntology = new ChoiceBox<String>();
		cbOntology.getItems().addAll("Class", "Properity");
		cbOntology.getSelectionModel().selectFirst();
		setChoiceBoxActions();
	}

	private static void initSearchBox() {
		searchBox = new VASearchBox();
		searchBox.setId("SearchBox");
	}

	/**
	 * Init bottom panel, testing
	 * 
	 * @return
	 */
	public static FlowPane addFlowPane() {
		FlowPane flow = new FlowPane(Orientation.VERTICAL);
		flow.setPadding(new Insets(5, 0, 5, 0));
		flow.setVgap(8);
		flow.setHgap(4);
		flow.setPrefWrapLength(170); // preferred width allows for two columns
		flow.setStyle("-fx-background-color: DAE6F3;");
		int size = 50;
		for (int i = 0; i < btnPages.length; i++) {
			btnPages[i] = new Button("AL" + String.valueOf(i + 1));
			btnPages[i].setStyle("-fx-font-size: " + size + "pt;");
			if (i >= VASyncData.getTotalDisplayNum())//init visibility
				btnPages[i].setVisible(false);
			flow.getChildren().add(btnPages[i]);
		}
		setPageButtonActions();
		return flow;
	}

	private static void initDownArrayPanel(BorderPane borderPane) {

		borderPane.setRight(addFlowPane());
	}

	/**
	 * Add tooltip to the chart.
	 */
	private static void initTooltip() {
		pieTooltip = new Tooltip("click to view more");
		for (final PieChart.Data currentData : chartLeft.getPieChart()
				.getData()) {
			Tooltip.install(currentData.getNode(), getPieTooltip());
		}
	}

	// =====================================Pie Chart related
	// logic====================================================

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
		chartLeft.updatePieChart(ontologyType);
	}

	/**
	 * Generate parent group and update tree view
	 */
	public static void generateParentGroup() {
		VAGroup parentGroup = null;
		if (currentGroup.getCurrentLevel() < 1) {
			// System.out.println("Generate Parent: parent=root");
			parentGroup = rootGroupLeft;
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
		rootGroupLeft = group;
	}

	public static void setRootGroupRight(VAGroup group) {
		rootGroupRight = group;
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
		lblSource.setText(label);
		if (empty == 1) {
			lblSource.setFont(Font.font("Verdana", 20));
			lblSource.setTextFill(Color.RED);
		} else {
			lblSource.setFont(Font.font("Verdana", 15));
			lblSource.setTextFill(Color.WHITESMOKE);
		}
	}

	/**
	 * Set target pie chart's label
	 * 
	 * @param label
	 */
	public static void setTargetLabel(String label) {
		lblTarget.setText(label);
		lblTarget.setFont(Font.font("Verdana", 20));
		lblTarget.setTextFill(Color.RED);
	}

	/**
	 * Get the right pie chart (display only)
	 * 
	 * @return
	 */
	public static VAPieChart getRightPie() {
		return chartRight;
	}

	/**
	 * Get the right pie chart's root group (display only)
	 * 
	 * @return
	 */
	public static VAGroup getRightRootGroup() {
		return rootGroupRight;
	}

	/**
	 * Update the pie chart panel with a new group
	 * 
	 * @param newGroup
	 */
	public static void updateAllWithNewGroup(VAGroup newGroup) {
		updateCurrentGroup(newGroup);
		chartLeft.updatePieChart(ontologyType.Source);
		generateParentGroup();
		chartLeft.clearList();
	}

	// ==============================Button & List
	// Event=====================================================

	/**
	 * Haven't been used yet
	 * @param n
	 */
	public static void setButtonVisible(int n) {
		if (n < btnPages.length && btnPages!=null && btnPages[n]!=null)
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
						updateAllWithNewGroup(rootGroupLeft);
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
					updateAllWithNewGroup(rootGroupLeft);
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
				updateAllWithNewGroup(rootGroupLeft);
			}

		});

		/**
		 * Go to previous panel (can only click once)
		 */
		btnUp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				updateAllWithNewGroup(rootGroupLeft);
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
