package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class VAGroup {
	private static int nodeCount = 0;
	private int groupID;
	private int parent;
	private VAData rootNode;
	private ArrayList<VAData> VADataArray;
	private HashMap<String, Integer> slotCountMap;
	private ArrayList<Integer> arcIntervalIndexArray;

	public VAGroup() {
		this.groupID = ++nodeCount;
		this.parent = -1;
		this.rootNode = null;
		this.VADataArray = new ArrayList<VAData>();
		this.slotCountMap = new HashMap<String, Integer>();
		this.arcIntervalIndexArray = new ArrayList<Integer>();
	}

	/**
	 * Set the parent pie chart
	 * 
	 * @param parent
	 */
	public void setParent(int parent) {
		this.parent = parent;
	}

	/**
	 * Set the root node of this group
	 * 
	 * @param rootNode
	 */
	public void setRootNode(VAData rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * Calculate the number of data in every slot (see threshold in VAVariables
	 * for value) stored in slotCountMap
	 */
	private void setslotCountMap() {
		// for (VAData data : mapVAData.values()) {
		for (VAData data : VADataArray) {
			double sim = data.getSimilarity();
			for (int i = 0; i < VAVariables.slotNum; i++) {
				if (sim > VAVariables.threshold[i]
						&& sim <= VAVariables.threshold[i + 1]) {
					String key = VAVariables.thresholdName[i];
					if (!slotCountMap.containsKey(VAVariables.thresholdName[i])) {
						slotCountMap.put(key, 1);
					} else {
						slotCountMap.put(key, slotCountMap.get(key) + 1);
					}
					break;
				}
			}
		}
	}

	/**
	 * set the index(in VADataArray) of arcs Every Arc is a sub-area in a pie
	 * chart slice, corresponding to one subset of nodes We set this in case of
	 * large ontologies
	 */
	private void setArcIntervalIndex() {
		// Data with smallest similarity starts from -1
		for (int i = 0; i < VAVariables.totalArcNumOfPieChart; i++) {
			arcIntervalIndexArray.add(-1);
		}

		// add the last, the last must be the last data in VADataArray
		int VADataNum = VADataArray.size();
		arcIntervalIndexArray.add(VADataNum);

		int lastPos = -1; // end index of last arc interval
		int intervalCount = 1; // start from the first interval

		// Iterate through all the sorted data, get the index for each arc
		// interval in dataArray
		for (int i = 0; i < VADataNum;) {
			double thresh = 1.0 / VAVariables.totalArcNumOfPieChart
					* intervalCount;
			// new slot, add previous index as the end of last slot
			if (VADataArray.get(i).getSimilarity() <= thresh) {
				arcIntervalIndexArray.set(intervalCount, i);
				lastPos = i;
				i++;
			} else {
				// Threshod not large enough, increase
				intervalCount++;
				arcIntervalIndexArray.set(intervalCount, lastPos);
			}
		}
		// If the largest number is smaller than the last slot threshold
		// Set the remainings arc indexes
		while (intervalCount <= VAVariables.totalArcNumOfPieChart) {
			arcIntervalIndexArray.set(intervalCount++, lastPos);
		}

		// Print for testing, comment if you don't need it.
		// printData();
		// printArcInterval();
	}

	/**
	 * Set the children of this node
	 * 
	 * @param listVAData
	 */
	public void setListVAData(ArrayList<VAData> listVAData) {
		this.VADataArray = listVAData;
		setslotCountMap();
		setArcIntervalIndex();
	}

	public static int getNodeCount() {
		return nodeCount;
	}

	public int getGroupID() {
		return groupID;
	}

	public VAData getRootNode() {
		return rootNode;
	}

	public int getParent() {
		return parent;
	}

	public ArrayList<VAData> getVADataArray() {
		return VADataArray;
	}

	public HashMap<String, Integer> getslotCountMap() {
		return slotCountMap;
	}

	public ArrayList<Integer> getArcIntervalIndexArray() {
		return arcIntervalIndexArray;
	}

	/**
	 * Return root node's local name
	 * 
	 * @return
	 */
	public String getRootNodeName() {
		return rootNode.getNodeName();
	}

	/**
	 * If root node has children or not
	 * 
	 * @return
	 */
	public boolean hasChildren() {
		return rootNode.hasChildren();
	}

	public boolean hasMatching() {
		return rootNode.getTargetNode() != null;
	}

	public int getCurrentLevel() {
		return this.rootNode.getCurrentLevel();
	}

	/**
	 * Print out the name and similarity of all the data
	 */
	public void printData() {
		int VADataNum = VADataArray.size();
		System.out.println(VADataNum + " data");
		for (int i = 0; i < VADataNum; i++) {
			System.out.println(VADataArray.get(i).getNodeName() + " "
					+ VADataArray.get(i).getSimilarity() + " ");
		}
	}

	/**
	 * Print out the arc interval indexes( index in VADataArray) Number of arc
	 * intervals = VAVariables.totalArcNumOfPieChart;
	 */
	public void printArcInterval() {
		System.out.println("Interval index array: "
				+ arcIntervalIndexArray.size() + " intervals");
		for (int i = 0; i <= VAVariables.totalArcNumOfPieChart; i++) {
			System.out.print(arcIntervalIndexArray.get(i) + " ");
		}
		System.out.print("\n");
	}

	public void printSlots() {
		System.out.println("current root node:" + getRootNodeName());
		System.out.println(slotCountMap.keySet().size() + " Slots");
		for (String s : VAVariables.thresholdName) {
			if (slotCountMap.containsKey(s))
				System.out.println(s + " " + slotCountMap.get(s));
		}
	}

}
