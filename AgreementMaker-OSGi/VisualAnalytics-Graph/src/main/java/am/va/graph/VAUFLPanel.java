package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

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
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.swing.JFrame;

import am.app.ontology.Node;

/**
 * UFL Panel, contain all actions Generate new questions User choose the best
 * pairs and save to lstPairs structure Write back the result to matrix[0]
 * (haven't done yet)
 * 
 * @author Yiting
 * 
 */
public class VAUFLPanel {
	private JFrame frameSub;
	private JFXPanel fxPanelSub;
	private Group rootSub;
	private Scene mySubScene;
	private int pointer;

	private ArrayList<VAUFLPairs> lstPairs;
	private ListView<String> lstchoices;
	private Label lblQuestion;
	private Label lblSelection;
	private ToggleGroup tg;
	private double sim;

	private int FrameWidth = 500;
	private int FrameHeight = 400;

	public VAUFLPanel() {

		VAUFL vaUFL = new VAUFL(); // first init data sets
		lstPairs = new ArrayList<VAUFLPairs>(); // store all Amb pairs and the
												// best matchings with sim
		vaUFL.getAmbMatchings(lstPairs, VAVariables.ontologyType.Source);// init
																			// lstPairs
		pointer = 0;

		if (lstPairs != null && lstPairs.size() > 0) { // if Amb pairs exist
			frameSub = new JFrame("VA-UFL"); // init UFL panel
			fxPanelSub = new JFXPanel();
			frameSub.setSize(FrameWidth, FrameHeight);
			frameSub.setLocation(500, 200);
			frameSub.setVisible(true);
			frameSub.add(fxPanelSub);
			// frameSub.setAlwaysOnTop(true); //place the panel on the very top
			frameSub.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					VAPanel.enableUFL(true);
				}
			});

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					InitUFLFX();
				}
			});
		}
	}

	/**
	 * Init UFL Panel
	 */
	public void InitUFLFX() {
		rootSub = new Group();
		mySubScene = new Scene(rootSub);
		fxPanelSub.setScene(mySubScene);
		String subSceneCss = VAUFLPanel.class.getResource("VA.css").toExternalForm();
		mySubScene.getStylesheets().add(subSceneCss);
		VBox vbox = setUFLLayout();
		rootSub.getChildren().add(vbox);
		fxPanelSub.setScene(mySubScene);
	}

	/**
	 * Init GUI
	 * 
	 * @return
	 */
	private VBox setUFLLayout() {
		VBox vBox = new VBox(5);
		vBox.setAlignment(Pos.CENTER);
		vBox.setPadding(new Insets(75));
		vBox.setSpacing(20);

		initTopQuestion(vBox);
		initTopPreviousSelection(vBox);
		initCenterChoices(vBox);
		initBottomButtons(vBox);
		setQuestions(0);// init question
		return vBox;
	}

	/**
	 * Set top Question part
	 * 
	 * @param borderPane
	 */
	private void initTopQuestion(VBox vBox) {
		lblQuestion = new Label();
		vBox.getChildren().add(lblQuestion);
	}

	private void initTopPreviousSelection(VBox vBox) {
		lblSelection = new Label();
		vBox.getChildren().add(lblSelection);
	}

	/**
	 * Set center Choices part
	 * 
	 * @param borderPane
	 */
	private void initCenterChoices(VBox vBox) {
		// Create a listview & Radio Buttons here
		VBox centerBox = new VBox(2);

		lstchoices = new ListView<String>();
		lstchoices.setMaxHeight(FrameHeight / 3);
		lstchoices.setMaxWidth(FrameWidth / 3 * 2);

		HBox radiobuttons = new HBox();
		radiobuttons.setAlignment(Pos.CENTER);
		// HBox.setMargin(radiobuttons, new Insets(0, 5, 0, 0));
		tg = new ToggleGroup();
		RadioButton[] rb = new RadioButton[4];
		for (int i = 0; i <= 3; i++) {
			double s = i * 30;
			rb[i] = new RadioButton();
			rb[i].setText(Integer.toString(i * 30));
			rb[i].setUserData(s);
			rb[i].setToggleGroup(tg);
			radiobuttons.getChildren().add(rb[i]);
		}
		tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (tg.getSelectedToggle() != null) {
					sim = (double) (tg.getSelectedToggle().getUserData());
				}
			}
		});
		centerBox.getChildren().addAll(lstchoices, radiobuttons);
		vBox.getChildren().add(centerBox);
	}

	/**
	 * Set bottom Buttons part
	 * 
	 * @param borderPane
	 */
	private void initBottomButtons(VBox vBox) {
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		// HBox.setMargin(hbox, new Insets(0, 5, 0, 0));
		Button btnNext = new Button("Next");
		Button btnPrevious = new Button("Previous");
		Button btnSave = new Button("save");
		Button btnClose = new Button("close");
		setButtonActions(btnClose, btnSave, btnNext, btnPrevious);
		hbox.getChildren().addAll(btnNext, btnPrevious, btnSave, btnClose);
		vBox.getChildren().add(hbox);
	}

	/**
	 * Set button actions
	 * 
	 * @param btnClose
	 * @param btnSave
	 */
	private void setButtonActions(Button btnClose, final Button btnSave, Button btnNext, Button btnPrevious) {
		btnClose.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// close the UFL window
				showFrame(false);
				VAPanel.enableUFL(true);
			}

		});

		btnSave.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO save user selection
				// get current listview choice and set it to the bestChoice
				if (lstchoices.getSelectionModel().getSelectedItem() != null && sim > 0) {
					String currentChoice = lstchoices.getSelectionModel().getSelectedItem();
					double curSim = (double) tg.getSelectedToggle().getUserData();
					lstPairs.get(pointer).setBestChoice(currentChoice);
					lstPairs.get(pointer).setSim(curSim);
					btnSave.setDisable(true);
				} else {
					System.out.println("Not saved");
				}

				// Insert the choice to matrix[0]
			}

		});

		btnNext.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int qnumber = getNextQuestion();
				lstchoices.getSelectionModel().select(-1);
				setQuestions(qnumber);
				btnSave.setDisable(false);
			}

		});

		btnPrevious.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int qnumber = getPreviousQuestion();
				setQuestions(qnumber);
				btnSave.setDisable(false);
			}

		});
	}

	/**
	 * Set visibility of UFL frame
	 * 
	 * @param show
	 */
	public void showFrame(boolean show) {
		frameSub.setVisible(show);
	}

	public void setQuestions(int qnumber) {
		sim = 0;

		if (tg != null && tg.getSelectedToggle() != null) {
			tg.getSelectedToggle().setSelected(false);
		}
		// Set question label
		String qSourceName = lstPairs.get(qnumber).getSourceNode().getLocalName();
		String qSourceLabel = lstPairs.get(qnumber).getSourceNode().getLabel();
		String indexOfQuestion = "(" + (qnumber + 1) + "/" + lstPairs.size() + ") ";
		// lblQuestion.setText(indexOfQuestion +
		// "Select the concept that best matches \"" + qSourceName + "|"
		// + qSourceLabel + "\":");
		lblQuestion.setText(indexOfQuestion + "\"" + qSourceName + "|" + qSourceLabel + "\":");
		// Set question body (choices)
		ObservableList<String> cListData = FXCollections.observableArrayList();
		HashMap<String, Node> t = lstPairs.get(qnumber).getTargetNodes();
		for (String key : t.keySet()) {
			cListData.add(key);
		}
		if (lstPairs.get(qnumber).selected()) {
			String selected = lstPairs.get(qnumber).getBestChoice();
			String sim = lstPairs.get(qnumber).getSim();
			lblSelection.setText("Previous selection: " + selected + ", sim=" + sim + "%");
		} else {
			lblSelection.setText("Previous selection: N/A");
		}
		lstchoices.setItems(cListData);
	}

	/**
	 * Get next question by adding pointer 1
	 * 
	 * @return
	 */
	private int getNextQuestion() {
		if (pointer >= lstPairs.size() - 1)
			pointer = 0;
		else
			pointer++;
		return pointer;
	}

	/**
	 * Get previous question by reducing pointer 1
	 * 
	 * @return
	 */
	private int getPreviousQuestion() {
		if (pointer <= 0)
			pointer = lstPairs.size() - 1;
		else
			pointer--;
		return pointer;
	}

}
