package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;

import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

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
	public void updatePieChart(VAVariables.ontologyType ontologyType) {
		VAGroup currentGroup = VAPanel.getCurrentGroup();
		// VAPanel.testVAGroup(currentGroup);

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
			}
			// else if (VAPanel.getStop() == -1) {
			// VAPanel.setStop(0);
			// }
			addListener(ontologyType);
		} else {
			int num = pieCharDatalist.size();
			for (int i = 0; i < num; i++)
				pieCharDatalist.remove(0);
		}
		if (VAPanel.getStop() != -1) {
			VAPanel.setSourceLabel(currentGroup.getRootNodeName());
			VAPanel.getRightPie().updateRightPieChart();
		}

		if (VAPanel.getStop() == -1) {
			VAPanel.setStop(0);
		}
	}

	/**
	 * Update current right pie chart (display only)
	 */
	public void updateRightPieChart() {

		// Clear old pie chart
		int num = pieCharDatalist.size();
		for (int i = 0; i < num; i++)
			pieCharDatalist.remove(0);

		// build new pie chart
		VAGroup currentGroup = VAPanel.getCurrentGroup();
		if (currentGroup == null)
			currentGroup = VAPanel.getRightGroup();
		if (currentGroup.hasMatching()) {
			VAData newRightRootData = new VAData(currentGroup.getRootNode()
					.getTargetNode(), null, 0);

			VAPanel.setTargetLabel(newRightRootData.getNodeName());

			if (newRightRootData.hasChildren()) {
				currentGroup.setListVAData(VASyncData.getChildrenData(
						newRightRootData, VAVariables.ontologyType.Target));
				HashMap<String, Integer> slotsMap = currentGroup
						.getslotCountMap();
				for (String key : VAVariables.thresholdName) {
					if (slotsMap.containsKey(key))
						pieCharDatalist.add(new PieChart.Data(key, slotsMap
								.get(key)));
				}
			}
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
	public void addListener(final VAVariables.ontologyType ontologyType) {
		for (final PieChart.Data currentData : pieChart.getData()) {
			currentData.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent e) {
							System.out.println("-----------------click "
									+ currentData.getName() + " ("
									+ (int) currentData.getPieValue()
									+ " ontologies)" + "!!!!");
							getNodesList(e, currentData, ontologyType);
							VAPanel.setListView(listView);
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
	private void getNodesList(MouseEvent e, PieChart.Data data,
			VAVariables.ontologyType ontologyType) {

		VAGroup currentGroup = VAPanel.getCurrentGroup();

		if (currentGroup == null) {
			System.out.println("- group is empty, return empty list");
			return;
		}

		// get the arcindexArray, in order to get nodes by similarity range
		final ArrayList<Integer> arcIndexArray = currentGroup
				.getArcIntervalIndexArray();
		final ArrayList<VAData> dataArrayList = currentGroup.getVADataArray();
		final HashMap<String, Integer> slotCountMap = currentGroup
				.getslotCountMap();
		final HashMap<String, VAData> listMap = new HashMap<String, VAData>();

		VARange idxRange = getPieChartDataIdxRange(data, slotCountMap,
				arcIndexArray, e);

		if (idxRange.isValid()) {
			listView = VAPanel.getlistView();
			ObservableList<String> arcListData = getListData(idxRange,
					dataArrayList, listMap);

			listView.setItems(arcListData);
			setListViewAction(listView, listMap, ontologyType);
			// test
			printData(idxRange, dataArrayList);
		} else {
			System.out.println("- pie chart is empty, return empty list");
		}

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
			final HashMap<String, VAData> listMap,
			final VAVariables.ontologyType ontologyType) {
		// Add handler, if user click one ontology,
		// Update the pie chart
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				String selectedLocalName = listView.getSelectionModel()
						.getSelectedItems().get(0);
				if (selectedLocalName != null) {
					System.out.println("clicked on " + selectedLocalName);
					selectedVAData = listMap.get(selectedLocalName);
					VAPanel.getNewGroup(ontologyType);
					updatePieChart(ontologyType);
				} else {
					System.out.println("- select empty!");
				}
			}
		});
	}

	/**
	 * given start and end index of the data list, put the corresponding data
	 * into a list in order to show in listView
	 * 
	 * @param start
	 * @param end
	 * @param dataArrayList
	 * @param listMap
	 * @return
	 */
	private ObservableList<String> getListData(VARange idxRange,
			ArrayList<VAData> dataArrayList, HashMap<String, VAData> listMap) {
		ObservableList<String> arcListData = FXCollections
				.observableArrayList();

		int start = idxRange.getStartIdx(), end = idxRange.getEndIdx();
		// Put data in list view
		for (int i = start; i <= end; i++) {
			String name = dataArrayList.get(i).getNodeName();
			arcListData.add(name);
			listMap.put(name, dataArrayList.get(i));
			// System.out.println("data " + i + " = " + name);
		}

		return arcListData;
	}

	/**
	 * given a range, print out the data in the list
	 * 
	 * @param data
	 */
	private void printData(VARange idxRange, ArrayList<VAData> dataArrayList) {
		int start = idxRange.getStartIdx(), end = idxRange.getEndIdx();
		// Put data in list view
		System.out.println("print data " + idxRange.toString());
		if (idxRange.isValid()) {
			for (int i = start; i <= end; i++) {
				System.out.println("data " + i + " = "
						+ dataArrayList.get(i).toString());
			}
		}
	}

	/**
	 * given a slice of pie chart, return the index range of the pie chart slice
	 * in the dataArray
	 * 
	 * @param data
	 */
	private VARange getPieChartDataIdxRange(PieChart.Data data,
			HashMap<String, Integer> slotCountMap,
			ArrayList<Integer> arcIndexArray, MouseEvent e) {
		if (pieChart.getData().size() > 0) {
			int sliceIndex = pieChart.getData().indexOf(data);

			// get the first slice that is not empty
			// Pie Chart start to show this slice first, empty slots are omitted
			int firstNoneEmptySliceIdx = 0;
			while (arcIndexArray.get(firstNoneEmptySliceIdx) == -1)
				firstNoneEmptySliceIdx++;
			firstNoneEmptySliceIdx = (int) (firstNoneEmptySliceIdx / VAVariables.arcNumPerSlice);

			VARange idxRange;
			// If the node is few, show all the ontology nodes
			if (slotCountMap.containsKey(VAVariables.thresholdName[sliceIndex])
					&& slotCountMap.get(VAVariables.thresholdName[sliceIndex]) <= VAVariables.showAllNodesThresh) {
				int startArcIdx = (sliceIndex + firstNoneEmptySliceIdx)
						* VAVariables.arcNumPerSlice;
				idxRange = new VARange(arcIndexArray.get(startArcIdx) + 1,
						arcIndexArray.get(startArcIdx
								+ VAVariables.arcNumPerSlice));
			} else {
				// Show them by arc area
				int arcIndex = getArcIdxByPosition(e, data);
				idxRange = new VARange(arcIndexArray.get(arcIndex) + 1,
						arcIndexArray.get(arcIndex + 1));
			}
			return idxRange;
		} else {
			return new VARange();
		}
	}

}