package am.va.graph;

import java.util.ArrayList;
import javax.swing.SwingUtilities;

import am.va.graph.VAVariables.ChartType;
import am.va.graph.VAVariables.ontologyType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Toggle;
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

	private int addTreeViewListener = 1;

	private JFXPanel fxPanel;
	private ListView<String> listViewLeft;
	private TreeView<String> listTreeLeft;
	private Group root;
	private Scene myScene;

	static double screenWidth;
	static double screenHeight;

	private Button btnRoot;
	private Button btnUp;
	private static Button btnUFL;
	private ToggleButton btnPages[] = new ToggleButton[5];
	private ToggleButton btnClusters = new ToggleButton();

	private RadioButton rbClass;
	private RadioButton rbProperity;
	private Label lblSource[] = new Label[2];
	private Label lblTarget[] = new Label[2];
	private VASearchBox searchBox;
	private Tooltip pieTooltip;

	private VAGraph chartLeft[] = new VAGraph[2];
	private VAGraph chartRight[] = new VAGraph[2];

	private int stop = -1;
	private VAVariables.currentSetStatus status;

	private VAPanelLogic val;

	private VAUFLPanel UFLPanel;
	private VAClustersPanel CLPanel[] = new VAClustersPanel[2];

	public VAPanel(VAPanelLogic v) {
		this.val = v;
	}

	public VAPanelLogic getVal() {
		return val;
	}

	public void setVal(VAPanelLogic val) {
		this.val = val;
	}

	/**
	 * Start showing the panel on the tab
	 */
	public void initButNotShow() {
		fxPanel = new VATab();
		screenWidth = Screen.getPrimary().getVisualBounds().getWidth() - 20;
		screenHeight = Screen.getPrimary().getVisualBounds().getHeight() - 30;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				InitFX();
			}
		});
	}

	public JFXPanel getFxPanel() {
		return fxPanel;
	}

	/**
	 * Init JavaFx panel, add mouse click Event handler
	 */
	public void InitFX() {
		root = new Group();
		myScene = new Scene(root);
		String sceneCss = VAPanel.class.getResource("VA.css").toExternalForm();
		myScene.getStylesheets().add(sceneCss);
		setLayout();
		fxPanel.setScene(myScene);
		// bug here
		// for (int i = 0; i < 2; i++)
		// chartLeft[i].updateMainPieChart(ontologyType.Source);
		for (int i = 0; i < 2; i++) {
			updateAllWithNewGroup(val.getRootGroupLeft(i), i, true);
		}
		generateNewTreeView();

		// test set status
	}

	// =====================Graphic User Interface========================

	/**
	 * Set the main panel layout, here we use BorderPane
	 */
	private void setLayout() {
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
	private void initCenterSplitPanel(BorderPane borderPane) {
		SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.setDividerPosition(0, 0.2);
		splitPane.getItems().addAll(getLeftSplitPane(), getCenterSplitPane());
		borderPane.setCenter(splitPane);
		BorderPane.setAlignment(splitPane, Pos.CENTER);
	}

	/**
	 * Left split panel, contains a treeview and a listview
	 * 
	 * @return
	 */
	private SplitPane getLeftSplitPane() {
		SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPosition(0, 0.5);
		splitPane.getItems().addAll(getTreeView(), getListView());
		return splitPane;
	}

	/**
	 * The treeview (up)
	 * 
	 * @return
	 */
	private TreeView<String> getTreeView() {
		listTreeLeft = new TreeView<String>();
		return listTreeLeft;
	}

	/**
	 * The listview (down)
	 * 
	 * @return
	 */
	private ListView<String> getListView() {
		listViewLeft = new ListView<String>();
		listViewLeft.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		return listViewLeft;
	}

	/**
	 * Center split panel, contains two sets of pie charts
	 * 
	 * @return
	 */
	private SplitPane getCenterSplitPane() {
		SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPosition(0, 0.3);
		splitPane.getItems().addAll(getCenterGroups(0), getCenterGroups(1));
		return splitPane;
	}

	/**
	 * Up Tile panel, contains the first set of pie charts [Use as Main chart]
	 * 
	 * @return
	 */
	private Group getCenterGroups(int i) {
		Group chartGroup = new Group();
		TilePane tilePane = new TilePane();
		if (i == 0)
			tilePane.setStyle("-fx-background-color: " + VAVariables.panelColor[0] + ";");
		else
			tilePane.setStyle("-fx-background-color: " + VAVariables.panelColor[1] + ";");
		tilePane.setPrefColumns(2); // preferred columns

		if (i == 0) { // main set
			chartLeft[i] = new VAGraph(val.getRootGroupLeft(i), this, VAVariables.ChartType.LeftMain);
			chartRight[i] = new VAGraph(val.getRootGroupRight(i), this, VAVariables.ChartType.RightMain);
		} else { // sub set
			chartLeft[i] = new VAGraph(val.getRootGroupLeft(i), this, VAVariables.ChartType.LeftSub);
			chartRight[i] = new VAGraph(val.getRootGroupRight(i), this, VAVariables.ChartType.RightSub);
		}
		lblSource[i] = new Label(VAVariables.sourceRoot, chartLeft[i].getPieChart());
		lblSource[i].setContentDisplay(ContentDisplay.CENTER);
		lblTarget[i] = new Label(VAVariables.targetRoot, chartRight[i].getPieChart());
		lblTarget[i].setContentDisplay(ContentDisplay.CENTER);
		tilePane.getChildren().addAll(lblSource[i], lblTarget[i]);
		chartGroup.getChildren().add(tilePane);
		if (i == 0)
			initTooltip(chartLeft[i]);
		return chartGroup;
	}

	/**
	 * Add tooltip to the chart.
	 */
	private void initTooltip(VAGraph chartLeft) {
		pieTooltip = new Tooltip("click to view more");
		for (final PieChart.Data currentData : chartLeft.getPieChart().getData()) {
			Tooltip.install(currentData.getNode(), pieTooltip);
		}
	}

	/**
	 * Init Top panel, including a toolbar that consists of buttons, a choice
	 * box and a search box.
	 * 
	 * @param borderPane
	 */
	private void initTopToolbar(BorderPane borderPane) {
		ToolBar toolbar = new ToolBar();

		Region spacer1 = new Region();
		spacer1.setStyle("-fx-padding: 0 20em 0 0;");
		Region spacer2 = new Region();
		spacer2.setStyle("-fx-padding: 0 10em 0 0;");
		Region spacer3 = new Region();
		spacer3.setStyle("-fx-padding: 0 10em 0 0;");

		HBox buttonBar = new HBox();
		initButtons(buttonBar);
		initRadioButtons(buttonBar);
		initClusterButton(buttonBar);
		BorderPane searchboxborderPane = new BorderPane();
		initSearchBox();
		searchboxborderPane.setRight(searchBox);
		HBox.setMargin(searchBox, new Insets(0, 5, 0, 0));
		toolbar.getItems().addAll(spacer1, buttonBar, spacer2, searchboxborderPane);
		borderPane.setTop(toolbar);
		BorderPane.setAlignment(searchboxborderPane, Pos.CENTER);
	}

	/**
	 * Init buttons in the HBox
	 * 
	 * @param buttonBar
	 */
	private void initButtons(HBox buttonBar) {
		btnRoot = new Button("Top level");
		btnUp = new Button("Go Up");
		btnUFL = new Button("User feedback");
		setToolButtonActions();
		buttonBar.getChildren().addAll(btnRoot, btnUp, btnUFL);
	}

	private void initRadioButtons(HBox buttonBar) {
		ToggleGroup tg = new ToggleGroup();
		rbClass = new RadioButton("Class");
		rbClass.setToggleGroup(tg);
		rbClass.setUserData("Class");
		rbClass.setSelected(true);
		rbProperity = new RadioButton("Properity");
		rbProperity.setToggleGroup(tg);
		rbProperity.setUserData("Properity");
		buttonBar.getChildren().addAll(rbClass, rbProperity);
		setRadioButtonActions(tg);
	}

	private void initClusterButton(HBox buttonBar) {
		Region spacer = new Region();
		spacer.setStyle("-fx-padding: 0 5em 0 0;");
		btnClusters = new ToggleButton("Show clusters");
		buttonBar.getChildren().addAll(spacer, btnClusters);
		setToggleButtonForCluster();
	}

	/**
	 * Init search box
	 */
	private void initSearchBox() {
		searchBox = new VASearchBox(this);
		searchBox.setId("SearchBox");
	}

	/**
	 * Init right panel, using buttons to represent different matching
	 * algorithms
	 * 
	 * @return
	 */
	public void initRightFlowPane(BorderPane borderPane) {
		FlowPane flow = new FlowPane(Orientation.VERTICAL);
		flow.setPadding(new Insets(5, 0, 5, 0));
		flow.setVgap(8);
		flow.setHgap(4);
		flow.setPrefWrapLength(170); // preferred width allows for two columns
		flow.setStyle("-fx-background-color: DAE6F3;");
		final ToggleGroup group = new ToggleGroup();
		for (int i = 0; i < btnPages.length; i++) {
			btnPages[i] = new ToggleButton("AL" + String.valueOf(i + 1));
			btnPages[i].setUserData(i);
			btnPages[i].setToggleGroup(group);
			if (i >= VASyncData.getTotalDisplayNum()) {// init visibility
				btnPages[i].setVisible(false);
			}
			flow.getChildren().add(btnPages[i]);
		}
		status = VAVariables.currentSetStatus.noEmpty;
		btnPages[0].setSelected(true);// default, shown by main chart panel
		btnPages[0].setStyle("-fx-background-color: " + VAVariables.panelColor[0] + ";");
		setToggleButtonStatus(true);
		setPageButtonActions();
		borderPane.setRight(flow);
	}

	// ============Clusters============
	public void addClusters(ArrayList<String> cluster, ChartType ct) {
		String clusterMsg = null;
		// construct the cluster
		if (cluster.size() == 0) {
			return;
		}
		for (int i = 0; i < cluster.size(); i++) {
			clusterMsg += cluster.get(i);
			clusterMsg += '\n';
		}
		if (ct == ChartType.LeftMain) {

		} else if (ct == ChartType.LeftSub) {

		} else if (ct == ChartType.RightMain) {

		} else if (ct == ChartType.RightSub) {

		} else {
			// error
		}
	}

	// ============Pie Chart, tree/list views related logic============

	/**
	 * Update Pie chart
	 * 
	 * @param ontologyType
	 */
	public void updateLeftChart() {
		for (int i = 0; i < 2; i++)
			chartLeft[i].updateMainPieChart(VAVariables.ontologyType.Source);
	}

	/**
	 * Set source pie chart's label
	 * 
	 * @param label
	 * @param empty
	 */
	public void setLblSource(String label, int empty, int i) {
		lblSource[i].setText(label);
		if (empty == 1) {
			lblSource[i].setFont(Font.font("Verdana", 20));
			lblSource[i].setTextFill(Color.RED);
		} else {
			lblSource[i].setFont(Font.font("Verdana", 15));
			lblSource[i].setTextFill(Color.WHITESMOKE);
		}
	}

	/**
	 * Set target pie chart's label
	 * 
	 * @param label
	 */
	public void setLblTarget(String label, int i) {
		lblTarget[i].setText(label);
		lblTarget[i].setFont(Font.font("Verdana", 20));
		lblTarget[i].setTextFill(Color.RED);
	}

	/**
	 * Get the right pie chart (display only)
	 * 
	 * @return
	 */
	public VAGraph getRightPie(int i) {
		return chartRight[i];
	}

	public VAGraph getLeftPie2() {
		return chartLeft[1];
	}

	/**
	 * Generate new Tree view
	 * 
	 * @param parentGroup
	 */
	public void generateNewTreeView() {
		// tree view updates according to main set
		VAGroup parentGroup = val.generateParentGroup(0);
		TreeItem<String> listTreeLeftRoot = null;
		String label = "";
		if (parentGroup.getParent() == 0)
			label = VAVariables.sourceRoot;
		else
			label = parentGroup.getRootNodeName();
		listTreeLeftRoot = new TreeItem<String>(label);
		ArrayList<VAData> data = parentGroup.getVADataArray();
		for (VAData d : data) {
			String name = d.getNodeName();
			// String name = d.getNodeNameAndLabel();
			if (!d.isLeaf())
				name = VAVariables.nodeWithChildren + name;
			listTreeLeftRoot.getChildren().add(new TreeItem<String>(name));
		}

		listTreeLeft.setShowRoot(true);
		listTreeLeft.setRoot(listTreeLeftRoot);
		listTreeLeftRoot.setExpanded(true);
		if (addTreeViewListener == 1) {
			treeviewAction(data);
			addTreeViewListener = 0;
		}
	}

	/**
	 * Add tree view actions (refresh pie chart and related data), called by
	 * generateNewTree
	 * 
	 * @param data
	 */
	private void treeviewAction(final ArrayList<VAData> data) {
		listTreeLeft.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				@SuppressWarnings("unchecked")
				TreeItem<String> selectedItem = (TreeItem<String>) newValue;
				if (selectedItem != null) {
					String selected = selectedItem.getValue();
					if (selected.startsWith(VAVariables.nodeWithChildren))
						selected = selected.substring(VAVariables.nodeWithChildren.length());
					System.out.println("--TreeView-- selected=" + selected); // print
					if (!selected.equals(VAVariables.sourceRoot) && !listTreeLeft.getRoot().getValue().equals(selected))
						updateBothSets(selected, false);
				}
			}
		});
	}

	/**
	 * The stop is just a on and off variable
	 * 
	 * @param i
	 */
	public void setStop(int i) {
		stop = i;
	}

	public int getStop() {
		return stop;
	}

	public ListView<String> getlistView() {
		return listViewLeft;
	}

	public void setListView(ListView<String> list) {
		listViewLeft = list;
	}

	/**
	 * Update the pie chart panel with a new group
	 * 
	 * @param newGroup
	 */
	public void updateAllWithNewGroup(VAGroup newGroup, int set, boolean updateTreeList) {
		val.updateCurrentGroup(newGroup, set);
		if (set == 0)
			setUpButton(newGroup);

		chartLeft[set].updateMainPieChart(ontologyType.Source);
		chartLeft[set].clearList();
		if (set == 0 && updateTreeList)
			generateNewTreeView();// may being called twice
	}

	/**
	 * Update both pie charts sets
	 * 
	 * @param newNode
	 */
	public void updateBothSets(String newNode, boolean updateTreeList) {
		for (int i = 0; i < 2; i++) {
			VAGroup newGroup;
			VAData subda = VASyncData.searchFrom(newNode, val.getRootGroupLeft(i).getRootNode(), i);
			newGroup = val.generateNewGroup(VAVariables.ontologyType.Source, subda, i);
			updateAllWithNewGroup(newGroup, i, updateTreeList);
		}
	}

	// ==========Button & List Event==================

	/**
	 * Haven't been used yet
	 * 
	 * @param n
	 */
	public void setButtonVisible(int n) {
		if (n < btnPages.length && btnPages != null && btnPages[n] != null)
			btnPages[n].setVisible(true);
	}

	/**
	 * Enable/Disable go up button
	 * 
	 * @param group
	 */
	public void setUpButton(VAGroup group) {
		if (group != null) {
			if (btnUp.isDisable()) {
				btnUp.setDisable(false);
			}
		}
	}

	/**
	 * Init radio button actions
	 * 
	 * @param tg
	 */
	private void setRadioButtonActions(final ToggleGroup tg) {
		tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (tg.getSelectedToggle() != null) {
					String selected = tg.getSelectedToggle().getUserData().toString();
					if (selected.equals("Class")) {
						VAPanelLogic.setCurrentNodeType(VAVariables.nodeType.Class);
					} else {
						VAPanelLogic.setCurrentNodeType(VAVariables.nodeType.Property);
					}
					for (int i = 0; i < 2; i++) {
						val.InitData(i); // init both main and sub sets
						updateAllWithNewGroup(val.getRootGroupLeft(i), i, true);
					}
				}
			}
		});
	}

	/**
	 * Add event for page buttons
	 */
	private void setPageButtonActions() {
		int num = btnPages.length;
		ToggleGroup group = new ToggleGroup();
		for (int i = 0; i < num; i++)
			btnPages[i].setToggleGroup(group);

		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle selectedToggle) {
				if (selectedToggle != null && status != VAVariables.currentSetStatus.noEmpty) {
					int t = (status == VAVariables.currentSetStatus.mainSetEmpty) ? 0 : 1;
					int cur = (int) ((ToggleButton) selectedToggle).getUserData() + 1;
					VASyncData.setCurrentDisplayNum(cur, t);// (***)
					val.InitData(t); // init main or sub set
					// update selected group only
					updateAllWithNewGroup(val.getRootGroupLeft(t), t, true);

					// Set color according to current main set
					((ToggleButton) selectedToggle)
							.setStyle("-fx-background-color: " + VAVariables.panelColor[0] + ";");
					((ToggleButton) selectedToggle).requestFocus();

					status = VAVariables.currentSetStatus.noEmpty;

					// disable all other buttons
					setToggleButtonStatus(true);

				} else {
					// Renew Current set variable
					((ToggleButton) oldValue).setStyle(null);
					// set main set empty here for testing
					status = VAVariables.currentSetStatus.mainSetEmpty;

					// enable all other buttons
					setToggleButtonStatus(false);
				}
			}
		});
	}

	private void setToggleButtonStatus(boolean disable) {
		for (int i = 0; i < btnPages.length; i++) {
			if (!btnPages[i].isSelected()) {
				btnPages[i].setDisable(disable);
			}
		}
	}

	private void setToggleButtonForCluster() {
		ToggleGroup group = new ToggleGroup();
		btnClusters.setToggleGroup(group);
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue,
					final Toggle selectedToggle) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						int x = 1150;
						int y[] = new int[2];
						y[0] = 200;
						y[1] = 550;
						// TODO Auto-generated method stub
						for (int i = 0; i < 2; i++) {
							if (selectedToggle != null && CLPanel[i] == null) {
								CLPanel[i] = new VAClustersPanel();
								CLPanel[i].setPosition(x, y[i]);
							} else if (selectedToggle != null) {
								CLPanel[i].showFrame(true);
							} else if (CLPanel != null) {
								CLPanel[i].showFrame(false);
							}
						}
					}
				});
			}
		});
	}

	/**
	 * Add event for buttons
	 */
	private void setToolButtonActions() {
		/**
		 * Go to root panel
		 */
		btnRoot.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				System.out.println("Click Top");
				for (int i = 0; i < 2; i++) {
					updateAllWithNewGroup(val.getRootGroupLeft(i), i, true);
				}
			}

		});

		/**
		 * Go to previous panel (can only click once)
		 */
		btnUp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (!listTreeLeft.getRoot().getValue().equals(VAVariables.sourceRoot))
					updateBothSets(listTreeLeft.getRoot().getValue(), true);
				else {
					for (int i = 0; i < 2; i++) { // same as btnRoot
						updateAllWithNewGroup(val.getRootGroupLeft(i), i, true);
					}
				}

			}

		});

		btnUFL.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// VAUserFeedBack ufbFrame = new VAUserFeedBack();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (UFLPanel == null)
							UFLPanel = new VAUFLPanel();
						else
							UFLPanel.showFrame(true);
						btnUFL.setDisable(true);
					}
				});
			}

		});
	}

	public static void enableUFL(boolean enable) {
		btnUFL.setDisable(!enable);
	}
}
