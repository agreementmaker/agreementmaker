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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.swing.JFrame;

public class VAUserFeedBack {
	private JFrame frameSub;
	private JFXPanel fxPanelSub;
	public VAUserFeedBack() {
		frameSub = new JFrame("VA - User Feed Back");
		fxPanelSub = new JFXPanel();
		frameSub.setSize(500, 320);
		frameSub.setLocation(500, 200);
		frameSub.setVisible(true);
		frameSub.add(fxPanelSub);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				InitFX();
			}
		});
	}
	
	public void InitFX() {
		Group rootSub = new Group();
		Scene mySubScene = new Scene(rootSub);
		fxPanelSub.setScene(mySubScene);
		
		String subSceneCss = VAUserFeedBack.class.getResource("VA.css").toExternalForm();
		mySubScene.getStylesheets().add(subSceneCss);
		AnchorPane anchorPane = new AnchorPane();

		ToggleGroup tg = new ToggleGroup();
		Label lblCompare = new Label();
		lblCompare.setFont(Font.font(null, FontWeight.BOLD, 15));
		lblCompare.setTextFill(Color.BLACK);
		// Set labels here, for now just assign two strings
		String source = "\"Reference\"";
		String target = "\"Paper\"";
		lblCompare.setText("How do you think " + source + " and " + target
				+ " matches?");
		ArrayList<RadioButton> rb = new ArrayList<RadioButton>();

		for (int i = 0; i < 5; i++) {
			RadioButton bt = new RadioButton(VAVariables.selectionPer[i]);
			bt.setToggleGroup(tg);
			//bt.setStyle("-fx-background-color:"+VAVariables.selectionStyle[i]);
			rb.add(bt);
		}

		anchorPane.getChildren().add(lblCompare);
		AnchorPane.setTopAnchor(lblCompare, Double.valueOf(30));
		AnchorPane.setLeftAnchor(lblCompare, Double.valueOf(30));
		int x = 70, y = 180;
		for (RadioButton r : rb) {
			anchorPane.getChildren().add(r);
			AnchorPane.setTopAnchor(r, Double.valueOf(x));
			AnchorPane.setLeftAnchor(r, Double.valueOf(y));
			x += 30;
		}
		x += 15;
		y += 40;
		Button btnSubmit = new Button("OK");
		btnSubmit.setId("submit-button");
		
		anchorPane.getChildren().add(btnSubmit);
		AnchorPane.setTopAnchor(btnSubmit, Double.valueOf(x));
		AnchorPane.setLeftAnchor(btnSubmit, Double.valueOf(y));

		rootSub.getChildren().add(anchorPane);

		btnSubmit.setOnAction(new EventHandler<ActionEvent>() {

			@SuppressWarnings("deprecation")
			@Override
			public void handle(ActionEvent arg0) {
				// Do things here, eg: save results...
				frameSub.hide();
			}
		});
		

	}
}
