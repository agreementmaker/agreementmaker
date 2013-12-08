package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

import java_cup.internal_error;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;

import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;

@SuppressWarnings("restriction")
public class VAPieChart {
	private PieChart pieChart;
	private ObservableList<PieChart.Data> pieCharDatalist;
	private double radius;	// Radius of the pie chart
	private Point2D center;	// pie chart center point
	private static VAData selectedVAData;

	public VAPieChart() {
		this.pieCharDatalist = null;
		this.pieChart = null;
		this.radius = 0;
		center = new Point2D(-1, -1);
	}

	/**
	 * Create new pie chart
	 * 
	 * @param group
	 */
	public VAPieChart(VAGroup group) {
		pieCharDatalist = FXCollections.observableArrayList();
		HashMap<String, Integer> slotsMap = group.getslotCountMap();
		for (String key : slotsMap.keySet()) {
			pieCharDatalist.add(new PieChart.Data(key, slotsMap.get(key)));
		}
		pieChart = new PieChart(this.pieCharDatalist);
		
	}
	
	public void updatePieChart() {
		int num = pieCharDatalist.size();
		System.out.println("remove=" + num);
		for (int i = 0; i < num; i++)
			pieCharDatalist.remove(0);
		HashMap<String, Integer> slotsMap = VAPanel.getCurrentGroup()
				.getslotCountMap();
		for (String key : slotsMap.keySet()) {
			pieCharDatalist.add(new PieChart.Data(key, slotsMap.get(key)));
		}
		addListener();
	}

	/**
	 * Get pie chart
	 * 
	 * @return
	 */
	public PieChart getPieChart() {
		return this.pieChart;
	}
	
	/**
	 * Get pie radius
	 * 
	 * @return
	 */
	public double getRadius() {
		return this.radius;
	}
	
	/**
	 * Get pie center
	 * 
	 * @return
	 */
	public Point2D getPieCenter() {
		return this.center;
	}
	
	/**
	 * Set pie radius
	 * 
	 * @return
	 */
	public void setRadius( double r ) {
		this.radius = r;
	}
	
	/**
	 * Get pie center
	 * 
	 * @return
	 */
	public void setPieCenter(Point2D c) {
		this.center = c;
	}
	
	public void addListener() {
		if (VAPanel.getStop() == 0)
			for (final PieChart.Data currentData : pieChart.getData()) {
				currentData.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
						new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent arg0) {
								System.out.println("-----------------click!!!!");
							
				                ListView<String> listView = getNodesList(arg0, currentData);
				                VAPanel.getFXGroup().getChildren().add(listView);
				                
								VAPanel.getNewGroup(VAPanel.getCurrentGroup());
								VAPanel.TEST(VAPanel.getCurrentGroup());
								updatePieChart();
							}

						});
			}
	}
	
	private int getArcIdxByPosition( MouseEvent e, PieChart.Data data){
		/**
    	 * every slot is 10%, every arc is 2%
    	 * so every slot has 5 arcs
    	 * arcs are divided by the distance to the center pointer
    	 */
    	Point2D pos = new Point2D(e.getSceneX(), e.getSceneY());
    	double dist = pos.distance(center);
    	int sliceIndex = pieChart.getData().indexOf(data);
    	int arcIndex = sliceIndex*VAVariables.arcNumPerSlice+(int)(VAVariables.arcNumPerSlice*dist/radius);
    	System.out.println("Pos = " + pos + " arcIndex " + arcIndex);
    	return arcIndex;

	}
	
	private ListView<String> getNodesList(MouseEvent e, PieChart.Data data ) {
		
		// get the arcindexArray, in order to get nodes by similarity range
		ArrayList<Integer> arcIndexArray = VAPanel.getCurrentGroup().getArcIntervalIndexArray();
		ArrayList<VAData> dataArrayList = VAPanel.getCurrentGroup().getVADataArray();
		HashMap<String, Integer> slotCountMap = VAPanel.getCurrentGroup().getslotCountMap();
		final HashMap<String, VAData> listMap = new HashMap<String, VAData>();
		
		int sliceIndex = pieChart.getData().indexOf(data);		
		int startDataIdx, endDataIdx;
		System.out.println("slice index is " + sliceIndex + " slot count is " + 
				slotCountMap.get(VAVariables.thresholdName[sliceIndex]));
		// if number of nodes in this slice is smaller than a threshold, show them all
		if( slotCountMap.get(VAVariables.thresholdName[sliceIndex])  <= VAVariables.showAllNodesThresh ){
			int startArcIdx = sliceIndex*VAVariables.arcNumPerSlice;
			startDataIdx = arcIndexArray.get(startArcIdx)+1;
			endDataIdx = arcIndexArray.get(startArcIdx+VAVariables.arcNumPerSlice);
		}else{
			int arcIndex = getArcIdxByPosition(e, data);
			startDataIdx = arcIndexArray.get(arcIndex)+1;
			endDataIdx = arcIndexArray.get(arcIndex+1);
		}
		System.out.println("start = " + startDataIdx + " end= " + endDataIdx);
		ObservableList<String> arcListData = FXCollections.observableArrayList();
		
        for( int i= startDataIdx; i< endDataIdx; i++){
        	String name = dataArrayList.get(i).getSourceNode().getLocalName();
        	arcListData.add(name);
        	//arcListData.add(dataArrayList.get(i));
        	listMap.put(name, dataArrayList.get(i));
        }
        final ListView<String> listView = new ListView<String>(arcListData);
        listView.setPrefHeight(500);
        listView.setPrefWidth(100);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent event) {
                System.out.println("clicked on " + listView.getSelectionModel().getSelectedItems());
            }
		});
        
        return listView;
	}
	
}