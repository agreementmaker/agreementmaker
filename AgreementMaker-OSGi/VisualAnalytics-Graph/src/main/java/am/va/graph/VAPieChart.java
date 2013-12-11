package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;

import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

@SuppressWarnings("restriction")
public class VAPieChart {
	private PieChart pieChart;
	private ListView<String> listView;
	private ObservableList<PieChart.Data> pieCharDatalist;
	private static VAData selectedVAData;

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
			addListener(ontologyType);
		} else {
			int num = pieCharDatalist.size();
			for (int i = 0; i < num; i++)
				pieCharDatalist.remove(0);
		}
		if (VAPanel.getStop() != -1) {
			VAPanel.getRightPie().updateRightPieChart();
			String newLabel = currentGroup.getRootNodeName() + ": "
					+ currentGroup.getRootNode().getSimilarity();
			if (currentGroup.getParent() == 0)
				newLabel = "Source ontoloty";
			if (currentGroup.hasChildren())
				VAPanel.setSourceLabel(newLabel, 0);
			else
				VAPanel.setSourceLabel(newLabel, 1);
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
		String newLabel = "";
		// build new pie chart
		VAGroup currentGroup = VAPanel.getCurrentGroup();
		VAGroup newRightGroup = new VAGroup();
		HashMap<String, Integer> slotsMap = null;
		if (currentGroup != null && currentGroup.hasMatching()) {
			VAData newRightRootData = new VAData(currentGroup.getRootNode()
					.getTargetNode(), null, 0);
			newLabel = newRightRootData.getNodeName();
			if (newRightRootData.hasChildren()) {
				newRightGroup.setListVAData(VASyncData.getChildrenData(
						newRightRootData, VAVariables.ontologyType.Target));
				slotsMap = newRightGroup.getslotCountMap();
			}
		} else if (currentGroup != null && currentGroup.getParent() == 0) {
			slotsMap = VAPanel.getRightRootGroup().getslotCountMap();
			newLabel = "Target ontology";
		} else {
			newLabel = "No matching found.";
		}
		if (slotsMap != null)
			for (String key : VAVariables.thresholdName) {
				if (slotsMap.containsKey(key))
					pieCharDatalist.add(new PieChart.Data(key, slotsMap
							.get(key)));
			}
		VAPanel.setTargetLabel(newLabel);
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
	 * Add update list info listener (mouse entered event)
	 */
	public void addListener(final VAVariables.ontologyType ontologyType) {
		for (final PieChart.Data currentData : pieChart.getData()) {
			currentData.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent e) {
							getNodesList(currentData, ontologyType);
							VAPanel.setListView(listView);
						}

					});
		}
	}

	/**
	 * Get the list of node in the area, show in the list view If user click one
	 * item, update the pie chart of the ontology user selects
	 * 
	 * @param e
	 * @param data
	 * @return
	 */
	private void getNodesList(PieChart.Data data,
			VAVariables.ontologyType ontologyType) {

		VAGroup currentGroup = VAPanel.getCurrentGroup();

		final ArrayList<VAData> dataArrayList = currentGroup.getVADataArray();
		final HashMap<String, Integer> slotCountMap = currentGroup
				.getslotCountMap();
		final HashMap<String, VAData> listMap = new HashMap<String, VAData>();

		VARange idxRange = getPieSliceDataIdxRange(data, slotCountMap);

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
					// Still need to figure the color

					VAPanel.generateNewGroup(ontologyType);
					updatePieChart(ontologyType);
					listView.getSelectionModel().clearSelection();
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
	 * Given a pie chart slice, return the start and end index of the slice
	 * 
	 * @param data
	 * @param slotCountMap
	 * @return
	 */
	private VARange getPieSliceDataIdxRange(PieChart.Data data,
			HashMap<String, Integer> slotCountMap) {
		if (slotCountMap.containsKey(data.getName())) {
			int start = 0, end = 1;
			for (int i = 0;; i++) {
				if (data.getName() == VAVariables.thresholdName[i]
						|| i == VAVariables.slotNum)
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