package am.va.graph;

import java.util.ArrayList;

import am.va.graph.VAVariables.nodeType;
import am.va.graph.VAVariables.ontologyType;

/**
 * Data related logic
 * 
 * @author Yiting
 * 
 */
public class VAPanelLogic {

	private VAGroup rootGroupLeft[] = new VAGroup[2];
	private VAGroup rootGroupRight[] = new VAGroup[2];

	private VAGroup previousGroup[] = new VAGroup[2]; // pointer group
	private VAGroup currentGroup[] = new VAGroup[2]; // pointer group

	private static nodeType currentNodeType = nodeType.Class;

	public VAPanelLogic() {

	}

	// =============Getter and Setter=============
	public VAGroup getRootGroupLeft(int i) {
		return rootGroupLeft[i];
	}

	public void setRootGroupLeft(VAGroup rootGroupLeft, int i) {
		this.rootGroupLeft[i] = rootGroupLeft;
	}

	public VAGroup getRootGroupRight(int i) {
		return rootGroupRight[i];
	}

	public void setRootGroupRight1(VAGroup rootGroupRight, int i) {
		this.rootGroupRight[i] = rootGroupRight;
	}

	public static nodeType getCurrentNodeType() {
		return currentNodeType;
	}

	public static void setCurrentNodeType(nodeType currentNodeType) {
		VAPanelLogic.currentNodeType = currentNodeType;
	}

	public VAGroup getPreviousGroup(int i) {
		return previousGroup[i];
	}

	public void setPreviousGroup(VAGroup previousGroup, int i) {
		this.previousGroup[i] = previousGroup;
	}

	public VAGroup getCurrentGroup(int i) {
		return currentGroup[i];
	}

	public void setCurrentGroup(VAGroup currentGroup, int i) {
		this.currentGroup[i] = currentGroup;
	}

	// ==================Init data==================
	public void InitData(int i) {
		rootGroupLeft[i] = initRootGroup(rootGroupLeft[i], VAVariables.ontologyType.Source, i);
		rootGroupRight[i] = initRootGroup(rootGroupRight[i], VAVariables.ontologyType.Target, i);

		if (VAVariables.testInitData) {
			System.out.println("---------------" + VAVariables.keywordsLabel_InitData + "-----------------");
			System.out.println("i=" + i);
			rootGroupLeft[i].printData();
			System.out.println();
		}
	}

	private VAGroup initRootGroup(VAGroup rootGroup, VAVariables.ontologyType type, int currentSet) {

		// init root group
		rootGroup = new VAGroup();
		rootGroup.setParent(0);
		VAData rootNode = VASyncData.getRootVAData(type, currentSet);
		rootGroup.setRootNode(rootNode);
		// get all the children data sorted by similarity
		rootGroup.setListVAData(VASyncData.getChildrenData(rootNode, type, currentSet));
		// init current & previous group [(3/9/2014)that's where the bug is
		// caused!!!]
		currentGroup[currentSet] = rootGroup;
		previousGroup[currentSet] = rootGroup;

		return rootGroup;
	}

	// ==============Updating Logic===================

	/**
	 * Generate new group
	 * 
	 * @param ontologyType
	 * @param newRootData
	 * @return
	 */
	public VAGroup generateNewGroup(VAVariables.ontologyType ontologyType, VAData newRootData, int currentSet) {
		// Need a function here, return value:VAData
		VAGroup newGroup = new VAGroup();
		newGroup.setRootNode(newRootData);
		if (newRootData != null && newRootData.hasChildren()) {
			newGroup.setParent(currentGroup[currentSet].getGroupID());
			newGroup.setListVAData(VASyncData.getChildrenData(newRootData, ontologyType, currentSet));
		} else {
			newGroup.setParent(previousGroup[currentSet].getGroupID());
		}
		updateCurrentGroup(newGroup, currentSet);
		/**
		 * Update list, being called twice ?!
		 */
		// if (ontologyType == VAVariables.ontologyType.Source)
		// generateParentGroup(currentSet);
		return newGroup;
	}

	/**
	 * Update parent group
	 * 
	 * @return
	 */
	public VAGroup generateParentGroup(int currentSet) {
		VAGroup parentGroup = null;
		if (currentGroup[currentSet].getCurrentLevel() < 1) {
			// System.out.println("Generate Parent: parent=root");
			parentGroup = rootGroupLeft[0];
		} else {
			// System.out.println("Generate Parent: new parent");
			parentGroup = new VAGroup();
			VAData parentData = VASyncData.getParentVAData(currentGroup[currentSet].getRootNode());
			parentGroup.setRootNode(parentData);
			parentGroup.setListVAData(VASyncData.getChildrenData(parentData, VAVariables.ontologyType.Source,
					currentSet));
		}
		return parentGroup;
	}

	/**
	 * Update previous group, called by updateCurrentGroup
	 * 
	 * @param group
	 */
	private void updatePreviousGroup(VAGroup group, int currentSet) {
		if (group != null) {
			previousGroup[currentSet] = group;

		} else {
			System.out.println("- Previous group is empty ?!!");
		}
	}

	/**
	 * Update current group
	 * 
	 * @param group
	 */
	public void updateCurrentGroup(VAGroup group, int currentSet) {
		updatePreviousGroup(currentGroup[currentSet], currentSet);
		currentGroup[currentSet] = group;
	}

	public ArrayList<ArrayList<String>> checkForAmbiguousMatchings() {
		ArrayList<ArrayList<String>> clusters = new ArrayList<ArrayList<String>>();
		// get two pairs
		System.out.println("---------" + VAVariables.keywordsLabel_findAmbiguous + "----------");
		String source = null, target1 = null, target2 = null;
		double sim1 = 0, sim2 = 0;
		try {
			if (currentGroup[0] != null) {
				source = currentGroup[0].getRootNodeName();
				target1 = currentGroup[0].getRootNode().getTargetNode().getLocalName();
				sim1 = currentGroup[0].getRootNode().getSimilarity();
			}
			if (currentGroup[1] != null) {
				target2 = currentGroup[1].getRootNode().getTargetNode().getLocalName();
				sim2 = currentGroup[1].getRootNode().getSimilarity();
			}
		} catch (Exception e) {
			// when loading the rootnodes, getRootNode function returns null
			// use try-catch here to save codes
			e.printStackTrace();
		}
		if (target1 != null && target2 != null && !target1.equals(target2) && sim1 != sim2) {
			System.out.println("Ambiguous found! source=" + source);
			System.out.println("target1=" + target1 + ", sim=" + sim1);
			System.out.println("target2=" + target2 + ", sim=" + sim2);

			// get the clusters for two targets
			ArrayList<String> s = VASyncData.test.showNodePropertyLists(source, VAVariables.ontologyType.Source);
			ArrayList<String> t1 = VASyncData.test.showNodePropertyLists(target1, VAVariables.ontologyType.Target);
			ArrayList<String> t2 = VASyncData.test.showNodePropertyLists(target2, VAVariables.ontologyType.Target);
			clusters.add(s);
			clusters.add(t1);
			clusters.add(t2);
		}
		return clusters;
	}
}
