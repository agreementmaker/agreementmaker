package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Side;

import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 * The main pie chart and its events
 * 
 * @author Yiting
 * 
 */
public class VAGraph {
	private PieChart pieChart;
	private ListView<String> listView;
	private ObservableList<PieChart.Data> pieCharDatalist;
	private VAVariables.ChartType type;
	private VAPanel vap;

	public VAGraph(VAPanel v) {
		this.pieCharDatalist = null;
		this.pieChart = null;
		this.vap = v;
	}

	/**
	 * Create new pie chart
	 * 
	 * @param group
	 */
	public VAGraph(VAGroup group, VAPanel v, VAVariables.ChartType Category) {
		this.vap = v;
		this.type = Category;
		pieCharDatalist = FXCollections.observableArrayList();
		HashMap<String, Integer> slotsMap = group.getslotCountMap();
		for (String key : VAVariables.thresholdName) {
			if (slotsMap.containsKey(key)) {
				PieChart.Data d = new PieChart.Data(key, slotsMap.get(key));
				pieCharDatalist.add(d);
			}
		}

		pieChart = new PieChart(this.pieCharDatalist);
		// Adjust the size of piechart & labels
		pieChart.setMaxSize(350, 350);
		pieChart.setLegendSide(Side.RIGHT);
		pieChart.setLegendVisible(false);
		pieChart.setLabelLineLength(5);
		pieChart.setClockwise(false);

		// the method must be applied after the chart has been shown on an
		// active scene (otherwise the data.getNode() call will return null).
		customPieChartColor();
	}

	private void customPieChartColor() {
		for (PieChart.Data d : pieCharDatalist) {
			d.getNode().setStyle("-fx-pie-color: " + VAVariables.ColorRange.get(d.getName()) + ";");
		}
	}

	/**
	 * Update current pie chart and add listeners
	 */
	public void updateMainPieChart(VAVariables.ontologyType ontologyType) {
		int currentSet = (type == VAVariables.ChartType.LeftMain) ? 0 : 1;
		VAGroup currentGroup = vap.getVal().getCurrentGroup(currentSet);

		if (currentGroup != null && currentGroup.hasChildren()) {
			if (vap.getStop() != -1) {// Renew pie chart and build a new one
				int num = pieCharDatalist.size();
				for (int i = 0; i < num; i++)
					pieCharDatalist.remove(0);
				HashMap<String, Integer> slotsMap = currentGroup.getslotCountMap();
				for (String key : VAVariables.thresholdName) {
					if (slotsMap.containsKey(key))
						pieCharDatalist.add(new PieChart.Data(key, slotsMap.get(key)));
				}
			}
			if (currentSet == 0) // add listener to main pie chart
				addListener(ontologyType, currentSet);
		} else {
			int num = pieCharDatalist.size();
			for (int i = 0; i < num; i++)
				pieCharDatalist.remove(0);
		}
		if (vap.getStop() != -1) {
			vap.getRightPie(currentSet).updateSubRightPieChart();
			String newLabel = currentGroup.getRootNodeName() + ": " + currentGroup.getRootNode().getSimilarity();
			if (currentGroup.getParent() == 0)
				newLabel = "Source ontoloty:" + String.valueOf(VASyncData.getCurrentDisplayNum(currentSet));
			if (currentGroup.hasChildren())
				vap.setLblSource(newLabel, 0, currentSet);
			else
				vap.setLblSource(newLabel, 1, currentSet);
		}
		customPieChartColor();
		if (vap.getStop() == -1) {
			vap.setStop(0);
		}
	}

	/**
	 * Update current right pie chart (display only) Called by
	 * updateMainPieChart
	 */
	public void updateSubRightPieChart() {
		int t = (type == VAVariables.ChartType.RightMain) ? 0 : 1;
		// Clear old pie chart
		int num = pieCharDatalist.size();
		for (int i = 0; i < num; i++)
			pieCharDatalist.remove(0);
		String newLabel = "";

		// build new pie chart based on currentGroup info
		VAGroup currentGroup = vap.getVal().getCurrentGroup(t);
		VAGroup newRightGroup = new VAGroup();
		HashMap<String, Integer> slotsMap = null;
		if (currentGroup != null && currentGroup.hasMatching()) {
			VAData newRightRootData = new VAData(currentGroup.getRootNode().getTargetNode(), null, 0);
			newLabel = newRightRootData.getNodeName();
			if (newRightRootData.hasChildren()) {
				newRightGroup.setListVAData(VASyncData.getChildrenData(newRightRootData,
						VAVariables.ontologyType.Target, t));
				slotsMap = newRightGroup.getslotCountMap();
			}
		} else if (currentGroup != null && currentGroup.getParent() == 0) {
			slotsMap = vap.getVal().getRootGroupRight(t).getslotCountMap();
			newLabel = "Target ontology";
		} else {
			newLabel = "No matching found.";
		}
		if (slotsMap != null)
			for (String key : VAVariables.thresholdName) {
				if (slotsMap.containsKey(key))
					pieCharDatalist.add(new PieChart.Data(key, slotsMap.get(key)));
			}
		vap.setLblTarget(newLabel, t);
		customPieChartColor();
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
	 * Add update list info listener (mouse entered event) Called by
	 * updatePieChart
	 */
	public void addListener(final VAVariables.ontologyType ontologyType, final int currentSet) {
		for (final PieChart.Data currentData : pieChart.getData()) {
			currentData.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent e) {
					getNodesList(currentData, ontologyType, currentSet);
					vap.setListView(listView);
				}

			});
		}
	}

	/**
	 * Get the list of node in the area, show in the list view If user click one
	 * item, update the pie chart of the ontology user selects Called by
	 * addListener
	 * 
	 * @param e
	 * @param data
	 * @return
	 */
	private void getNodesList(PieChart.Data data, VAVariables.ontologyType ontologyType, int currentSet) {

		VAGroup currentGroup = vap.getVal().getCurrentGroup(currentSet);

		final ArrayList<VAData> dataArrayList = currentGroup.getVADataArray();
		final HashMap<String, Integer> slotCountMap = currentGroup.getslotCountMap();
		final HashMap<String, VAData> listMap = new HashMap<String, VAData>();

		VARange idxRange = getPieSliceDataIdxRange(data, slotCountMap);

		if (idxRange.isValid()) {
			listView = vap.getlistView();
			ObservableList<String> arcListData = getListData(idxRange, dataArrayList, listMap);

			listView.setItems(arcListData);
			setListViewAction(listView, listMap, ontologyType);
			// test
			// printData(idxRange, dataArrayList);
		} else {
			System.out.println("- pie chart is empty, return empty list");
		}

	}

	/**
	 * Set click action of listView Called by getNodesList
	 * 
	 * @param listView
	 * @param listMap
	 */
	private void setListViewAction(final ListView<String> listView, final HashMap<String, VAData> listMap,
			final VAVariables.ontologyType ontologyType) {
		// Add handler, if user click one ontology,
		// Update the pie chart
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				String selectedLocalName = listView.getSelectionModel().getSelectedItems().get(0);
				if (selectedLocalName != null) {
					vap.updateBothSets(selectedLocalName);
					listView.getSelectionModel().clearSelection();
				} else {
					System.out.println("- select empty!");
				}
			}
		});
	}

	/**
	 * given start and end index of the data list, put the corresponding data
	 * into a list in order to show in listView Called by getNodesList
	 * 
	 * @param start
	 * @param end
	 * @param dataArrayList
	 * @param listMap
	 * @return
	 */
	private ObservableList<String> getListData(VARange idxRange, ArrayList<VAData> dataArrayList,
			HashMap<String, VAData> listMap) {
		ObservableList<String> arcListData = FXCollections.observableArrayList();

		int start = idxRange.getStartIdx(), end = idxRange.getEndIdx();
		// Put data in list view
		for (int i = start; i <= end; i++) {
			String name = dataArrayList.get(i).getNodeName();
			arcListData.add(name);
			listMap.put(name, dataArrayList.get(i));
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
				System.out.println("data " + i + " = " + dataArrayList.get(i).toString());
			}
		}
	}

	/**
	 * Given a pie chart slice, return the start and end index of the slice
	 * Called by getNodesList
	 * 
	 * @param data
	 * @param slotCountMap
	 * @return
	 */
	private VARange getPieSliceDataIdxRange(PieChart.Data data, HashMap<String, Integer> slotCountMap) {
		if (slotCountMap.containsKey(data.getName())) {
			int start = 0, end = 1;
			for (int i = 0;; i++) {
				if (data.getName() == VAVariables.thresholdName[i] || i == VAVariables.slotNum)
					break;
				if (slotCountMap.containsKey(VAVariables.thresholdName[i])) {
					start += slotCountMap.get(VAVariables.thresholdName[i]);
				}
			}
			end = start + slotCountMap.get(data.getName()) - 1;
			return new VARange(start, end);
		} else
			return new VARange();

	}

	/**
	 * Clear current listview (this can be done in VAPanel.java also, just put
	 * it here for now
	 */
	public void clearList() {
		if (this.listView != null) {
			int num = this.listView.getItems().size();
			for (int i = 0; i < num; i++) {
				this.listView.getItems().remove(0);
			}
		}
	}
}