package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class VAGroup {
	private static int nodeCount = 0;
	private int groupID;
	private int parent;
	private VAData rootNode;
	private ArrayList<VAData> lstVAData;
	private HashMap<String, Integer> slots;

	public VAGroup() {
		this.groupID = ++nodeCount;
		this.parent = -1;
		this.rootNode = null;
		this.lstVAData = new ArrayList<VAData>();
		this.slots = new HashMap<String, Integer>();
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
	 * Calculate the number of 10 slots
	 */
	public void setSlots() {
		for (VAData data : lstVAData) {
			double sim = data.getSimilarity();
			for (int i = 0; i < VAVariables.slotsNum; i++) {
				if (sim > VAVariables.threshold[i]
						&& sim < VAVariables.threshold[i + 1]) {
					String key = VAVariables.thresholdName[i];
					if (!slots.containsKey(VAVariables.thresholdName[i])) {
						slots.put(key, 0);
					} else {
						slots.put(key, slots.get(key) + 1);
					}
					break;
				}
			}
		}
	}

	/**
	 * Set the data list of this level
	 */
	public void setDataList() {

	}

	public VAData getRootNode() {
		return rootNode;
	}

	public int getParent() {
		return parent;
	}

	public ArrayList<VAData> getLstVAData() {
		return lstVAData;
	}

	public HashMap<String, Integer> getSlots() {
		return slots;
	}

}
