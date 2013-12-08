package am.va.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class VAGroup {
	private static int nodeCount = 0;
	private int groupID;
	private int parent;
	private VAData rootNode;
	//private HashMap<String, VAData> mapVAData;
	private ArrayList<VAData> listVAData;
	private HashMap<String, Integer> slots;

	public VAGroup() {
		this.groupID = ++nodeCount;
		this.parent = -1;
		this.rootNode = null;
		//this.mapVAData = new HashMap<String, VAData>();
		this.listVAData = new ArrayList<VAData>();
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
	private void setSlots() {
		//for (VAData data : mapVAData.values()) {
		for(VAData data : listVAData){
			double sim = data.getSimilarity();
			for (int i = 0; i < VAVariables.slotsNum; i++) {
				if (sim > VAVariables.threshold[i]
						&& sim <= VAVariables.threshold[i + 1]) {
					String key = VAVariables.thresholdName[i];
					if (!slots.containsKey(VAVariables.thresholdName[i])) {
						slots.put(key, 1);
					} else {
						slots.put(key, slots.get(key) + 1);
					}
					break;
				}
			}
		}
	}

//	public void setMapVAData(HashMap<String, VAData> mapVAData) {
//		this.mapVAData = mapVAData;
//		setSlots();
//	}
	
	public void setListVAData(ArrayList<VAData> listVAData){
		this.listVAData = listVAData;
		setSlots();
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

//	public HashMap<String, VAData> getMapVAData() {
//		return mapVAData;
//	}
	
	public ArrayList<VAData> getListVAData(){
		return listVAData;
	}

	public HashMap<String, Integer> getSlots() {
		return slots;
	}

}
