package am.va.graph;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javax.swing.JFrame;

public class VAClustersPanel {
	private JFrame frameSub;
	private JFXPanel fxPanelSub;
	private Group rootSub;
	private Scene mySubScene;

	private Label lblCluster;

	private int FrameWidth = 200;
	private int FrameHeight = 200;

	private ArrayList<String> cluster;

	public ArrayList<String> getCluster() {
		return cluster;
	}

	public void setCluster(ArrayList<String> cluster) {
		this.cluster = cluster;
	}

	private void updateLabel() {
		String tmp = null;
		// construct cluster
		for (int i = 0; i < cluster.size(); i++) {
			tmp += cluster.get(i);
			tmp += "\n";
		}
		final String msg = tmp;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// javaFX operations should go here
				lblCluster.setText(msg);
			}
		});

	}

	public VAClustersPanel(ArrayList<String> cluster) {
		this.cluster = cluster;

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

	public void InitUFLFX() {

		rootSub = new Group();
		mySubScene = new Scene(rootSub);
		fxPanelSub.setScene(mySubScene);
		String subSceneCss = VAUFLPanel.class.getResource("VA.css").toExternalForm();
		mySubScene.getStylesheets().add(subSceneCss);
		lblCluster = new Label();
		updateLabel();
		rootSub.getChildren().add(lblCluster);
		fxPanelSub.setScene(mySubScene);
	}

	public void showFrame(boolean show) {
		if (show) {
			updateLabel();
		}
		frameSub.setVisible(show);
	}

	public void setPosition(int x, int y) {
		frameSub.setBounds(x, y, FrameWidth, FrameHeight);
	}
}
