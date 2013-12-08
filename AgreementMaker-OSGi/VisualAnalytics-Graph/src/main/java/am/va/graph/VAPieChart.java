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
		System.out.println("slots size=" + slotsMap.size());
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
				.getSlots();
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

	public void addListener() {
		if (VAPanel.getStop() == 0)
			for (PieChart.Data currentData : pieChart.getData()) {
				currentData.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
						new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent arg0) {
								System.out
										.println("-----------------click!!!!");
								VAPanel.getNewGroup(VAPanel.getCurrentGroup());
								VAPanel.TEST(VAPanel.getCurrentGroup());
								updatePieChart();
							}

						});
			}
	}
}