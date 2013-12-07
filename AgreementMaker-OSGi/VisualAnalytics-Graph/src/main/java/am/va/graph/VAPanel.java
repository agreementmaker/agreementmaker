package am.va.graph;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class VAPanel {
	
	private static final boolean START_AGREEMENTMAKER = false;
	private static JFrame frame;
	private static ListView<String> listView;
	
	private static VAPieChart sourceChart;
	private static VAPieChart targetChart;
	
	/**
	 * Do initialization and show the interface
	 */
	private static void initAndShowGUI(VAGroup sourceVAGroup, VAGroup targetVAGroup) {
		// This method is invoked on Swing thread
		frame = new JFrame("FX");
		frame.setSize(1200, 500);
		final JFXPanel fxPanel = new JFXPanel();
		frame.add(fxPanel);
		frame.setVisible(true);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initFX(fxPanel);
			}
		});
	}
	
	/**
	 * set the source and target pie chart data and location
	 * @param sourceVAGroup
	 * @param targetVAGroup
	 */
	private void setCharts( VAGroup sourceVAGroup, VAGroup targetVAGroup ){
		sourceChart = new VAPieChart(sourceVAGroup);
		targetChart = new VAPieChart(targetVAGroup);
	}
	private static void initFX(JFXPanel fxPanel) {
		// This method is invoked on JavaFX thread

		final Group root = new Group();
		Scene myScene = new Scene(root);

		ObservableList<PieChart.Data> pieChartData = FXCollections
				.observableArrayList(new PieChart.Data("Sun", 20),
									 new PieChart.Data("IBM", 12), 
									 new PieChart.Data("HP",25), 
									 new PieChart.Data("Dell", 22),
									 new PieChart.Data("Apple", 30));
		
		ObservableList<PieChart.Data> pieChartData2 = FXCollections
				.observableArrayList(new PieChart.Data("Sun", 22),
									 new PieChart.Data("IBM", 32), 
									 new PieChart.Data("HP",65), 
									 new PieChart.Data("Dell", 12),
									 new PieChart.Data("Apple", 10));
		
		final PieChart chart = new PieChart(pieChartData);
		PieChart chart2 = new PieChart(pieChartData2);
		//chart.setClockwise(false);
		
		//root.getChildren().add(new Group(chart, chart2));
		root.getChildren().add(chart);
		root.getChildren().add(chart2);

		System.out.println(chart.getWidth());

		// Scene scene = createScene();
		fxPanel.setScene(myScene);
		
		final Label caption = new Label("");
		caption.setTextFill(Color.DARKORANGE);
		//caption.setStyle("-fx-font: 24 arial;");

		for (final PieChart.Data data : chart.getData()) {
		    data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
		        new EventHandler<MouseEvent>() {
		            @Override public void handle(MouseEvent e) {
		   
		                //Circle circle1 = new Circle(e.getSceneX(),e.getSceneY(),10, Color.RED);
		        		//root.getChildren().add(new Group(circle1));
		                listView = new ListView<String>(); 
		                listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		                listView.setPrefWidth(100);
		                root.getChildren().add(listView);
		                System.out.println("pressed " + String.valueOf(data.getPieValue()));
		             }
		        });
		}
	}
	

}
