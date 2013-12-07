package am.va.graph;

import java.util.HashMap;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.input.MouseEvent;

public class VAPanel {

	private static JFrame frame;
	private static JFXPanel fxPanel;
	private static VAGroup rootGroup;
	private static VAGroup currentGroup;
	private static int count = 1;

	/**
	 * Init Frame
	 */
	public static void initAndShowGUI() {
		frame = new JFrame("VA");
		frame.setSize(500, 500);
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
		final Group root = new Group();
		final Scene myScene = new Scene(root);

		VAPieChart chart = new VAPieChart(rootGroup);
		chart.getPieChart().setClockwise(false);
		root.getChildren().add(chart.getPieChart());

		fxPanel.setScene(myScene);
		updateCurrentGroup(rootGroup);

		for (PieChart.Data currentData : chart.getPieChart().getData()) {
			currentData.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent arg0) {
							// TODO Auto-generated method stub
							getNewGroup(currentGroup);
							VAPieChart chart = new VAPieChart(currentGroup);
							chart.getPieChart().setClockwise(false);
							root.getChildren().remove(0);// remove the previous
															// chart
							root.getChildren().add(chart.getPieChart());// add
																		// new
																		// chart
							TEST(currentGroup);
							fxPanel.updateUI();
						}

					});
		}
	}

	/**
	 * Generate new VAGroup according user's click
	 * 
	 * @param currentGroup
	 * @return
	 */
	public static void getNewGroup(VAGroup currentGroup) {
		// Need a function here, return value:VAData
		VAData newRootData;
		if (count == 1)
			newRootData = rootGroup.getMapVAData().get("Reference");
		else
			newRootData = rootGroup.getMapVAData().get("Book");
		count++;
		VAGroup newGroup = new VAGroup();
		newGroup.setParent(currentGroup.getGroupID());
		newGroup.setRootNode(newRootData);
		newGroup.setMapVAData(VASyncData.getChildrenData(newRootData));
		updateCurrentGroup(newGroup);
	}

	private static void updateCurrentGroup(VAGroup group) {
		currentGroup = new VAGroup();
		currentGroup.setParent(group.getParent());
		currentGroup.setRootNode(group.getRootNode());
		currentGroup.setMapVAData(group.getMapVAData());
	}

	/**
	 * Init rootGroup
	 * 
	 * @param group
	 */
	public static void setRootGroup(VAGroup group) {
		rootGroup = group;
	}

	private static void TEST(VAGroup rootGroup) {
		String rootNodeName = rootGroup.getRootNode().getSourceNode()
				.getLocalName();
		System.out.println(rootNodeName);
		HashMap<String, VAData> vaData = rootGroup.getMapVAData();
		for (VAData d : vaData.values()) {
			System.out.println(d.getSourceNode().getLocalName() + ","
					+ d.getTargetNode().getLocalName() + ","
					+ d.getSimilarity());
		}
		HashMap<String, Integer> slots = rootGroup.getSlots();
		for (String s : slots.keySet()) {
			System.out.println(s + ":" + slots.get(s));
		}
	}
}
