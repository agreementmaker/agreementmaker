package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;

import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

@SuppressWarnings("restriction")
public class VAPieChart {
	private PieChart pieChart;
	private ListView<String> listView;
	private ObservableList<PieChart.Data> pieCharDatalist;
	private double radius; // Radius of the pie chart
	private Point2D center; // pie chart center point
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
		for (String key : VAVariables.thresholdName) {
			if (slotsMap.containsKey(key))
				pieCharDatalist.add(new PieChart.Data(key, slotsMap.get(key)));
		}
		pieChart = new PieChart(this.pieCharDatalist);
	}

	/**
	 * Update current pie chart and add listeners
	 */
	public void updatePieChart() {
		VAGroup currentGroup = VAPanel.getCurrentGroup();
		VAPanel.testVAGroup(currentGroup);
		VAPanel.setSourceLabel(currentGroup.getRootNodeName());
		if (currentGroup != null && currentGroup.hasChildren()) {
			if (VAPanel.getStop() != -1) {// Renew pie chart and build a new one
				int num = pieCharDatalist.size();
				for (int i = 0; i < num; i++)
					pieCharDatalist.remove(0);
				HashMap<String, Integer> slotsMap = currentGroup
						.getslotCountMap();
				for (String key : VAVariables.thresholdName) {
					if (slotsMap.containsKey(key))
						pieCharDatalist.add(new PieChart.Data(key, slotsMap
								.get(key)));
				}
			} else if (VAPanel.getStop() == -1) {
				VAPanel.setStop(0);
			}
			addListener();
		} else {
			int num = pieCharDatalist.size();
			for (int i = 0; i < num; i++)
				pieCharDatalist.remove(0);
		}
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
	public void setRadius(double r) {
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

	/**
	 * Add update list info listener (mouse entered event)
	 */
	public void addListener() {
		for (final PieChart.Data currentData : pieChart.getData()) {
			currentData.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent arg0) {
							System.out
									.println("-----------------click update list!!!!");
							System.out.println(VAPanel.getCurrentGroup()
									.getRootNode().getSourceNode()
									.getLocalName());
							ListView<String> newlist = getNodesList(arg0,
									currentData);
							VAPanel.setListView(newlist);
						}

					});
		}
	}

	/**
	 * based on the position of mouse click, compute the arc index of area
	 * 
	 * @param MouseEvent
	 *            e,
	 * @param Pie
	 *            Chart data, the data user clicked
	 * @return
	 */
	private int getArcIdxByPosition(MouseEvent e, PieChart.Data data) {
		/**
		 * every slot is 10%, every arc is 2% so every slot has 5 arcs arcs are
		 * divided by the distance to the center pointer
		 */
		Point2D pos = new Point2D(e.getSceneX(), e.getSceneY());
		double dist = pos.distance(center);
		int sliceIndex = pieChart.getData().indexOf(data);
		int arcIndex = sliceIndex * VAVariables.arcNumPerSlice
				+ (int) (VAVariables.arcNumPerSlice * dist / radius);
		// System.out.println("Pos = " + pos + " arcIndex " + arcIndex);
		return arcIndex;

	}

	/**
	 * Get the list of node in the area, show in the list view If user click one
	 * item, update the pie chart of the ontology user selects
	 * 
	 * @param e
	 * @param data
	 * @return
	 */
	private ListView<String> getNodesList(MouseEvent e, PieChart.Data data) {

		VAGroup currentGroup = VAPanel.getCurrentGroup();

		if (currentGroup == null) {
			System.out.println("- group is empty, return empty list");
			return listView;
		}

		// get the arcindexArray, in order to get nodes by similarity range
		ArrayList<Integer> arcIndexArray = currentGroup
				.getArcIntervalIndexArray();
		ArrayList<VAData> dataArrayList = currentGroup.getVADataArray();
		HashMap<String, Integer> slotCountMap = currentGroup.getslotCountMap();
		final HashMap<String, VAData> listMap = new HashMap<String, VAData>();

		if (pieChart.getData().size() > 0) {
			int sliceIndex = pieChart.getData().indexOf(data);
			int startDataIdx, endDataIdx;
			if (slotCountMap.containsKey(VAVariables.thresholdName[sliceIndex])
					&& slotCountMap.get(VAVariables.thresholdName[sliceIndex]) <= VAVariables.showAllNodesThresh) {
				int startArcIdx = sliceIndex * VAVariables.arcNumPerSlice;
				startDataIdx = arcIndexArray.get(startArcIdx) + 1;
				endDataIdx = arcIndexArray.get(startArcIdx
						+ VAVariables.arcNumPerSlice);
			} else {
				// Show them by arc area
				int arcIndex = getArcIdxByPosition(e, data);
				startDataIdx = arcIndexArray.get(arcIndex) + 1;
				endDataIdx = arcIndexArray.get(arcIndex + 1);
			}
			// System.out.println("start = " + startDataIdx + " end= " +
			// endDataIdx);

			listView = VAPanel.getlistView();
			ObservableList<String> arcListData = FXCollections
					.observableArrayList();

			// Put data in list view
			for (int i = startDataIdx; i <= endDataIdx; i++) {
				String name = dataArrayList.get(i).getSourceNode()
						.getLocalName();
				arcListData.add(name);
				// arcListData.add(dataArrayList.get(i));
				listMap.put(name, dataArrayList.get(i));
				// System.out.println("data " + i + " = " + name);
			}

			listView.setItems(arcListData);
			setListViewAction(listView, listMap);
		} else {
			System.out.println("- pie chart is empty, return empty list");
		}

		return listView;
	}

	/**
	 * return slected VAData often called after user clicks one item on
	 * ListView, passed to getNewGroup in VAPanel
	 * 
	 * @return
	 */
	public static VAData getSelectedVAData() {
		return selectedVAData;
	}

	/**
	 * Set click action of listView
	 * 
	 * @param listView
	 * @param listMap
	 */
	private void setListViewAction(final ListView<String> listView,
			final HashMap<String, VAData> listMap) {
		// Add handler, if user click one ontology,
		// Update the pie chart
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				String selectedLocalName = listView.getSelectionModel()
						.getSelectedItems().get(0);
				if (selectedLocalName != null) {
					System.out.println("clicked on " + selectedLocalName);
					selectedVAData = listMap.get(selectedLocalName);
					VAPanel.getNewGroup();
					updatePieChart();
				} else {
					System.out.println("- select empty!");
				}
			}
		});
	}

}