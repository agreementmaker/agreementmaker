package am.va.graph;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import am.app.Core;
import am.ui.UI;
import am.ui.UICore;

@SuppressWarnings("restriction")
public class Test {

	private static final boolean START_AGREEMENTMAKER = false;

	// private static void initAndShowGUI(VAGroup rootGroup) {
	// // This method is invoked on Swing thread
	// JFrame frame = new JFrame("FX");
	// frame.setSize(500, 500);
	// final JFXPanel fxPanel = new JFXPanel();
	// frame.add(fxPanel);
	// frame.setVisible(true);
	//
	// Platform.runLater(new Runnable() {
	// @Override
	// public void run() {
	// initFX(fxPanel, rootGroup);
	// }
	// });
	// }

//	public static void initFX(JFXPanel fxPanel) {
//		// This method is invoked on JavaFX thread
//
//		Group root = new Group();
//		Scene myScene = new Scene(root);
//
//		ObservableList<PieChart.Data> pieChartData = FXCollections
//				.observableArrayList(new PieChart.Data("Sun", 20),
//						new PieChart.Data("IBM", 12), new PieChart.Data("HP",
//								25), new PieChart.Data("Dell", 22),
//						new PieChart.Data("Apple", 30), new PieChart.Data(
//								"Sun", 20), new PieChart.Data("IBM", 12),
//						new PieChart.Data("HP", 25), new PieChart.Data("Dell",
//								22), new PieChart.Data("Apple", 30),
//						new PieChart.Data("Apple", 30));
//		PieChart chart = new PieChart(pieChartData);
//		chart.setClockwise(false);
//		root.getChildren().add(chart);
//		System.out.println(chart.startAngleProperty());
//
//		// Scene scene = createScene();
//		fxPanel.setScene(myScene);
//	}

	public static void main(String[] args) {
		VASyncListener vaSync = new VASyncListener();

		// SwingUtilities.invokeLater(new Runnable() {
		// @Override
		// public void run() {
		// initAndShowGUI(rootGroup);
		// }
		// });
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!START_AGREEMENTMAKER) {
					Core.getInstance().setRegistry(new ManualMatcherRegistry());
					UICore.setUI(new UI());
				}
			}
		});
	}
}
