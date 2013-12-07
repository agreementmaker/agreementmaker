package am.va.graph;

import java.util.HashMap;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;

import javafx.scene.chart.PieChart;
import javafx.scene.input.MouseEvent;

@SuppressWarnings("restriction")
public class VAPieChart {
	private PieChart pieChart;
	private ObservableList<PieChart.Data> pieCharDatalist;

	public VAPieChart() {
		this.pieCharDatalist = null;
		this.pieChart = null;
	}

	/**
	 * Create new pie chart
	 * 
	 * @param group
	 */
	public VAPieChart(VAGroup group) {
		pieCharDatalist = FXCollections.observableArrayList();
		HashMap<String, Integer> slotsMap = group.getSlots();
		for (String key : slotsMap.keySet()) {
			pieCharDatalist.add(new PieChart.Data(key, slotsMap.get(key)));
		}
		pieChart = new PieChart(this.pieCharDatalist);
	}

	public void updatePieChart(VAGroup group) {
		pieCharDatalist = FXCollections.observableArrayList();
		HashMap<String, Integer> slotsMap = group.getSlots();
		for (String key : slotsMap.keySet()) {
			pieCharDatalist.add(new PieChart.Data(key, slotsMap.get(key)));
		}
		pieChart = new PieChart(this.pieCharDatalist);
	}

	/**
	 * Get pie chart
	 * 
	 * @return
	 */
	public PieChart getPieChart() {
		return this.pieChart;
	}
}