package am.va.graph;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.swing.JFrame;

public class VAUFLPanel {
	private JFrame frameSub;
	private JFXPanel fxPanelSub;
	private Group rootSub;
	private Scene mySubScene;

	public VAUFLPanel() {
		frameSub = new JFrame("VA-UFL");;
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
	 * @return
	 */
	private BorderPane setUFLLayout() {
		BorderPane borderPane = new BorderPane();
		initTopQuestion(borderPane);
		initCenterChoices(borderPane, 3);
		initBottomButtons(borderPane);
		return borderPane;
	}

	/**
	 * Set top part of border panel
	 * @param borderPane
	 */
	private void initTopQuestion(BorderPane borderPane) {
		Label lblQuestion = new Label("Question here");
		borderPane.setTop(lblQuestion);
	}

	/**
	 * Set center part of border panel
	 * @param borderPane
	 * @param num
	 */
	private void initCenterChoices(BorderPane borderPane, int num) {
		ToggleGroup tg = new ToggleGroup();
		VBox vbox = new VBox();
		vbox.setSpacing(5);
		RadioButton[] rb = new RadioButton[num];
		for (int i = 0; i < num; i++) {
			rb[i] = new RadioButton("choice " + (i + 1));
			rb[i].setToggleGroup(tg);
			vbox.getChildren().add(rb[i]);
		}
		borderPane.setCenter(vbox);
	}
	
	/**
	 * Set bottom part of border panel
	 * @param borderPane
	 */
	private void initBottomButtons(BorderPane borderPane){
		VBox vbox = new VBox();
		
		Button btnClose = new Button("close");
		Button btnSave = new Button("save");
		setButtonActions(btnClose, btnSave);
		vbox.getChildren().addAll(btnClose, btnSave);
		borderPane.setBottom(vbox);
	}
	
	/**
	 * Set button actions
	 * @param btnClose
	 * @param btnSave
	 */
	private void setButtonActions(Button btnClose, Button btnSave){
		btnClose.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				// close the UFL window
				showFrame(false);
			}
			
		});
		
		btnSave.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				// TODO save user selection
				
			}
			
		});
	}
	
	/**
	 * Set visibility of UFL frame
	 * @param show
	 */
	public void showFrame(boolean show){
		frameSub.setVisible(show);
	}
}
