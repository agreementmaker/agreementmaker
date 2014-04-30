package am.va.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * VAGroup: the data structure that build up the pie chart
 * For now I don't store each group in the memory
 * @author Yiting
 *
 */
public class VAGroup {
	private static int nodeCount = 0; //not really useful
	private int groupID;
	private int parent;
	private VAData rootNode;
	private ArrayList<VAData> VADataArray;
	private HashMap<String, Integer> slotCountMap;

	public VAGroup() {
		this.groupID = ++nodeCount;
		this.parent = -1;
		this.rootNode = null;
		this.VADataArray = new ArrayList<VAData>();
		this.slotCountMap = new HashMap<String, Integer>();
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
	 * Set the children of this node
	 * 
	 * @param listVAData
	 */
	public void setListVAData(ArrayList<VAData> listVAData) {
		this.VADataArray = listVAData;
		Collections.sort(listVAData);
		setslotCountMap();
		//setArcIntervalIndex();
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

	/**
	 * Return root node's local name
	 * 
	 * @return
	 */
	public String getRootNodeName() {
		return rootNode.getNodeName() + "|" + rootNode.getLabel();
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

	public void printSlots() {
		System.out.println("current root node:" + getRootNodeName());
		System.out.println(slotCountMap.keySet().size() + " Slots");
		for (String s : VAVariables.thresholdName) {
			if (slotCountMap.containsKey(s))
				System.out.println(s + " " + slotCountMap.get(s));
		}
	}

}
