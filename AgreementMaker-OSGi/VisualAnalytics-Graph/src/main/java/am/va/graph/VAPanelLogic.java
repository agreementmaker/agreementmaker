package am.va.graph;

import javafx.scene.control.Button;
import am.va.graph.VAVariables.nodeType;

/**
 * Implement the MatcherChangeListener to see if the ontologies have been
 * matched or not For now I haven't use this class
 * 
 * @author Yiting
 * 
 */
public class VAPanelLogic {

	private VAGroup rootGroupLeft1;
	private VAGroup rootGroupRight1;

	private VAGroup previousGroup1; // pointer group
	private VAGroup currentGroup1; // pointer group

	private static nodeType currentNodeType = nodeType.Class;

	public VAPanelLogic() {

	}

	// =============Getter and Setter=============
	public VAGroup getRootGroup() {
		return rootGroupLeft1;
	}

	public VAGroup getRootGroupLeft1() {
		return rootGroupLeft1;
	}

	public void setRootGroupLeft1(VAGroup rootGroupLeft1) {
		this.rootGroupLeft1 = rootGroupLeft1;
	}

	public VAGroup getRootGroupRight1() {
		return rootGroupRight1;
	}

	public void setRootGroupRight1(VAGroup rootGroupRight1) {
		this.rootGroupRight1 = rootGroupRight1;
	}

	public static nodeType getCurrentNodeType() {
		return currentNodeType;
	}

	public static void setCurrentNodeType(nodeType currentNodeType) {
		VAPanelLogic.currentNodeType = currentNodeType;
	}

	public VAGroup getPreviousGroup1() {
		return previousGroup1;
	}

	public void setPreviousGroup1(VAGroup previousGroup1) {
		this.previousGroup1 = previousGroup1;
	}

	public VAGroup getCurrentGroup1() {
		return currentGroup1;
	}

	public void setCurrentGroup1(VAGroup currentGroup1) {
		this.currentGroup1 = currentGroup1;
	}

	// ==================Init data==================
	public void InitData() {
		/**
		 * Set root group left
		 */
		rootGroupLeft1 = new VAGroup();
		rootGroupLeft1.setParent(0);
		VAData rootNodeLeft = VASyncData
				.getRootVAData(VAVariables.ontologyType.Source);
		rootGroupLeft1.setRootNode(rootNodeLeft);
		// get all the children data sorted by similarity
		rootGroupLeft1.setListVAData(VASyncData.getChildrenData(
				rootGroupLeft1.getRootNode(), VAVariables.ontologyType.Source));

		/**
		 * Set root group right
		 */
		rootGroupRight1 = new VAGroup();
		rootGroupRight1.setParent(0);
		VAData rootNodeRight = VASyncData
				.getRootVAData(VAVariables.ontologyType.Target);
		rootGroupRight1.setRootNode(rootNodeRight);
		rootGroupRight1
				.setListVAData(VASyncData.getChildrenData(
						rootGroupRight1.getRootNode(),
						VAVariables.ontologyType.Target));
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
			newGroup.setParent(currentGroup1.getGroupID());
			newGroup.setListVAData(VASyncData.getChildrenData(newRootData,
					ontologyType));
		} else {
			newGroup.setParent(previousGroup1.getGroupID());
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
		if (currentGroup1.getCurrentLevel() < 1) {
			// System.out.println("Generate Parent: parent=root");
			parentGroup = rootGroupLeft1;
		} else {
			// System.out.println("Generate Parent: new parent");
			parentGroup = new VAGroup();
			VAData parentData = VASyncData
					.getParentVAData(currentGroup1.getRootNode());
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
			previousGroup1 = group;

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
		updatePreviousGroup(currentGroup1);
		currentGroup1 = group;
	}

}
