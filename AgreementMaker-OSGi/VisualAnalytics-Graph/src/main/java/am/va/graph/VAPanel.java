package am.va.graph;

import java.util.ArrayList;
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
	private static int stop = -1;

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
		final VAPieChart chart = new VAPieChart(rootGroup);

		chart.getPieChart().setClockwise(false);
		root.getChildren().add(chart.getPieChart());

		fxPanel.setScene(myScene);
		updateCurrentGroup(rootGroup);
		TEST(currentGroup);
		chart.updatePieChart();
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
		System.out.println(count);
		if (count == 1)
			newRootData = currentGroup.getListVAData().get(3);
		else
			newRootData = currentGroup.getListVAData().get(1);
		count++;
		if (newRootData != null
				&& newRootData.getSourceNode().getChildCount() > 0) { // if
																		// there's
																		// still
																		// new
																		// group
			VAGroup newGroup = new VAGroup();
			newGroup.setParent(currentGroup.getGroupID());
			newGroup.setRootNode(newRootData);
			newGroup.setListVAData(VASyncData.getChildrenData(newRootData));
			updateCurrentGroup(newGroup);
		} else {
			stop = 1;
		}
	}

	private static void updateCurrentGroup(VAGroup group) {
		if (group != null) {
			currentGroup = new VAGroup();
			currentGroup.setParent(group.getParent());
			currentGroup.setRootNode(group.getRootNode());
			currentGroup.setListVAData(group.getListVAData());
		} else {
			System.out.println("New group is NULL");
		}
	}

	/**
	 * Init rootGroup
	 * 
	 * @param group
	 */
	public static void setRootGroup(VAGroup group) {
		rootGroup = group;
	}

	public static void TEST(VAGroup rootGroup) {
		String rootNodeName = rootGroup.getRootNode().getSourceNode()
				.getLocalName();
		System.out.println(rootNodeName);
		ArrayList<VAData> vaData = rootGroup.getListVAData();
		for (VAData d : vaData) {
			System.out.println(d.getSourceNode().getLocalName() + ","
					+ d.getTargetNode().getLocalName() + ","
					+ d.getSimilarity());
		}
		HashMap<String, Integer> slots = rootGroup.getSlots();
		for (String s : slots.keySet()) {
			System.out.println(s + ":" + slots.get(s));
		}
	}

	public static VAGroup getCurrentGroup() {
		return currentGroup;
	}

	public static int getStop() {
		return stop;
	}
	
	public static void setStop(int s){
		stop = s;
	}
}
