package am.va.graph;

import am.va.graph.VAVariables.nodeType;

/**
 * Data related logic
 * 
 * @author Yiting
 * 
 */
public class VAPanelLogic {

	private VAGroup rootGroupLeft[] = new VAGroup[2]; 
	private VAGroup rootGroupRight[] = new VAGroup[2];

	private VAGroup previousGroup; // pointer group
	private VAGroup currentGroup; // pointer group

	private static nodeType currentNodeType = nodeType.Class;

	public VAPanelLogic() {

	}

	// =============Getter and Setter=============
//	public VAGroup getRootGroup(int i) {
//		return rootGroupLeft[i];
//	}

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

	public VAGroup getPreviousGroup1() {
		return previousGroup;
	}

	public void setPreviousGroup(VAGroup previousGroup1) {
		this.previousGroup = previousGroup1;
	}

	public VAGroup getCurrentGroup() {
		return currentGroup;
	}

	public void setCurrentGroup(VAGroup currentGroup1) {
		this.currentGroup = currentGroup1;
	}

	// ==================Init data==================
	public void InitData() {
		for(int i=0; i<2; i++){
		rootGroupLeft[i] = initRootGroup(rootGroupLeft[i], VAVariables.ontologyType.Source);
		rootGroupRight[i] = initRootGroup(rootGroupRight[i], VAVariables.ontologyType.Target);
		}
	}

	private VAGroup initRootGroup(VAGroup rootGroup, VAVariables.ontologyType type) {
		rootGroup = new VAGroup();
		rootGroup.setParent(0);
		VAData rootNode = VASyncData.getRootVAData(type);
		rootGroup.setRootNode(rootNode);
		// get all the children data sorted by similarity
		rootGroup.setListVAData(VASyncData.getChildrenData(
				rootGroup.getRootNode(), type));
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
	public VAGroup generateNewGroup(VAVariables.ontologyType ontologyType,
			VAData newRootData) {
		// Need a function here, return value:VAData
		VAGroup newGroup = new VAGroup();
		newGroup.setRootNode(newRootData);
		if (newRootData != null && newRootData.hasChildren()) {
			newGroup.setParent(currentGroup.getGroupID());
			newGroup.setListVAData(VASyncData.getChildrenData(newRootData,
					ontologyType));
		} else {
			newGroup.setParent(previousGroup.getGroupID());
		}
		updateCurrentGroup(newGroup);
		/**
		 * Update list, being called twice ?!
		 */
		if (ontologyType == VAVariables.ontologyType.Source)
			generateParentGroup();
		return newGroup;
	}

	/**
	 * Update parent group
	 * 
	 * @return
	 */
	public VAGroup generateParentGroup() {
		VAGroup parentGroup = null;
		if (currentGroup.getCurrentLevel() < 1) {
			// System.out.println("Generate Parent: parent=root");
			parentGroup = rootGroupLeft[0];
		} else {
			// System.out.println("Generate Parent: new parent");
			parentGroup = new VAGroup();
			VAData parentData = VASyncData.getParentVAData(currentGroup
					.getRootNode());
			parentGroup.setRootNode(parentData);
			parentGroup.setListVAData(VASyncData.getChildrenData(parentData,
					VAVariables.ontologyType.Source));
		}
		return parentGroup;
	}

	/**
	 * Update previous group
	 * 
	 * @param group
	 */
	public void updatePreviousGroup(VAGroup group) {
		if (group != null) {
			previousGroup = group;

		} else {
			System.out.println("- Previous group is empty ?!!");
		}
	}

	/**
	 * Update current group
	 * 
	 * @param group
	 */
	public void updateCurrentGroup(VAGroup group) {
		updatePreviousGroup(currentGroup);
		currentGroup = group;
	}

}
