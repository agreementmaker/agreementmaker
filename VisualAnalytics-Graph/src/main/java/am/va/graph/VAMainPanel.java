package am.va.graph;

/**
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 */
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
 
/**
 * A circular chart divided into segments. The value of each segment represents
 * a proportion of the total.
 *
 * @see javafx.scene.chart.PieChart
 * @see javafx.scene.chart.Chart
 */
public class VAMainPanel extends Application {
 
     private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));
         ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
             new PieChart.Data("Sun", 20),
             new PieChart.Data("IBM", 12),
             new PieChart.Data("HP", 25),
             new PieChart.Data("Dell", 22),
             new PieChart.Data("Apple", 30)
         );
        PieChart chart = new PieChart(pieChartData);
        chart.setClockwise(false);
        root.getChildren().add(chart);
    }
 
    @Override public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }
    
    public static void startPanel(){
    	launch();
    }
}