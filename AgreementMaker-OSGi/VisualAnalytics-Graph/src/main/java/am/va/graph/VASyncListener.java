package am.va.graph;

import java.util.ArrayList;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.MatchingTaskChangeEvent.EventType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class VASyncListener implements MatcherChangeListener {

	public VASyncListener() {
		Core.getInstance().addMatcherChangeListener(this);
	}

	@Override
	public void matcherChanged(MatchingTaskChangeEvent e) {

		// Build root panel for source ontology
		String type = e.getEvent().toString();
		System.out.println(type); // type for finishing matching?
		VAGroup rootGroup = new VAGroup();
		rootGroup.setParent(0);
		rootGroup.setRootNode(getRootVAData(VAVariables.ontologyType.Source));
		rootGroup.setDataList(getChildrenData(rootGroup.getRootNode()));
	}

	/**
	 * Get the children list of current root node
	 * 
	 * @param rootNodeData
	 * @return
	 */
	private ArrayList<VAData> getChildrenData(VAData rootNodeData) {
		ArrayList<VAData> res = null;
		Node rootNode = rootNodeData.sourceNode;
		for (Node n : rootNode.getChildren()) {
			int nIndex = n.getIndex();
			String nName = n.getLocalName();
			// get target node info which best matches this node
			Node t = null;
			double s = 0.00;
			res.add(new VAData(n, t, s));
		}
		return res;
	}

	/**
	 * Get VAData for Root node
	 * 
	 * @param ontologyType
	 * @return
	 */
	private VAData getRootVAData(VAVariables.ontologyType ontologyType) {
		Node sNode = null, tNode = null;
		double Similarity = 0.0;
		if (ontologyType == VAVariables.ontologyType.Source) { // pie chart for
																// source
																// ontology
			sNode = getRootNode(VAVariables.ontologyType.Source);
			tNode = null;
		}
		return new VAData(sNode, tNode, Similarity);
	}

	/**
	 * Get the root Node
	 * 
	 * @param ontologyType
	 * @return
	 */
	private Node getRootNode(VAVariables.ontologyType ontologyType) {
		Node rootNode = null;
		List<MatchingTask> matchingTask = Core.getInstance().getMatchingTasks();
		MatchingTask currentTask = matchingTask.get(0); // for now only select
														// the first task
		if (ontologyType == VAVariables.ontologyType.Source) {
			Ontology sourceOntology = currentTask.matcherResult
					.getSourceOntology();
			rootNode = sourceOntology.getClassesRoot();
		} else {
			Ontology targetOntology = currentTask.matcherResult
					.getTargetOntology();
			rootNode = targetOntology.getClassesRoot();
		}

		return rootNode;
	}

}
