package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.swing.JFrame;

import am.app.ontology.Node;

public class VAUFLPanel {
	private JFrame frameSub;
	private JFXPanel fxPanelSub;
	private Group rootSub;
	private Scene mySubScene;
	private int pointer;

	private ArrayList<VAUFLPairs> lstPairs;
	private ListView<String> lstchoices;
	private Label lblQuestion;

	public VAUFLPanel() {

		VAUFL vaUFL = new VAUFL(); // first init data sets
		lstPairs = new ArrayList<VAUFLPairs>();
		vaUFL.getAbiMatchings(lstPairs, VAVariables.ontologyType.Source);
		pointer = -1;

		if (lstPairs != null && lstPairs.size() > 0) {
			frameSub = new JFrame("VA-UFL");
			fxPanelSub = new JFXPanel();
			frameSub.setSize(500, 320);
			frameSub.setLocation(500, 200);
			frameSub.setVisible(true);
			frameSub.add(fxPanelSub);

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
		BorderPane borderPane = setUFLLayout();
		rootSub.getChildren().add(borderPane);
		fxPanelSub.setScene(mySubScene);
	}

	/**
	 * Init GUI
	 * 
	 * @return
	 */
	private BorderPane setUFLLayout() {
		BorderPane borderPane = new BorderPane();
		initTopQuestion(borderPane);
		initCenterChoices(borderPane, 3);
		initBottomButtons(borderPane);
		setQuestions(0);// init question
		return borderPane;
	}

	/**
	 * Set top part of border panel
	 * 
	 * @param borderPane
	 */
	private void initTopQuestion(BorderPane borderPane) {
		lblQuestion = new Label();
		borderPane.setTop(lblQuestion);
	}

	/**
	 * Set center part of border panel
	 * 
	 * @param borderPane
	 * @param num
	 */
	private void initCenterChoices(BorderPane borderPane, int num) {
		// Create a listview here
		lstchoices = new ListView<String>();
		lstchoices.setMaxHeight(100);
		borderPane.setCenter(lstchoices);
	}

	/**
	 * Set bottom part of border panel
	 * 
	 * @param borderPane
	 */
	private void initBottomButtons(BorderPane borderPane) {
		VBox vbox = new VBox();
		Button btnNext = new Button("Next");
		Button btnPrevious = new Button("Previous");
		Button btnSave = new Button("save");
		Button btnClose = new Button("close");
		setButtonActions(btnClose, btnSave, btnNext, btnPrevious);
		vbox.getChildren().addAll(btnNext, btnPrevious, btnSave, btnClose);
		borderPane.setBottom(vbox);
	}

	/**
	 * Set button actions
	 * 
	 * @param btnClose
	 * @param btnSave
	 */
	private void setButtonActions(Button btnClose, Button btnSave, Button btnNext, Button btnPrevious) {
		btnClose.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// close the UFL window
				showFrame(false);
			}

		});

		btnSave.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO save user selection
				//get current listview choice and set it to the bestChoice (bug here)
				String currentChoice = lstchoices.getSelectionModel().getSelectedItem();
				lstPairs.get(pointer).setBestChoice(currentChoice);
				
				//Insert the choice to matrix[0]
			}

		});
		
		btnNext.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int qnumber = getNextQuestion();
				setQuestions(qnumber);
			}
			
		});
		
		btnPrevious.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int qnumber = getPreviousQuestion();
				setQuestions(qnumber);
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
		// Set question label
		String qSourceName = lstPairs.get(qnumber).getSourceNode().getLocalName();
		lblQuestion.setText("Select the concept that best matches \"" + qSourceName + "\":");

		// Set question body (choices)
		ObservableList<String> cListData = FXCollections.observableArrayList();
		HashMap<String, Node> t = lstPairs.get(qnumber).getTargetNodes();
		for (String key : t.keySet()) {
			cListData.add(key);
		}
		if(lstPairs.get(qnumber).selected()){
			lstchoices.getSelectionModel().setSelectionMode(SelectionMode.valueOf(lstPairs.get(qnumber).getBestChoice()));
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
