package am.va.graph;

import java.util.ArrayList;

import am.va.graph.VAVariables.ontologyType;

public class VASearcher {

	public VAData search(String name) {
		System.out.println("Searcher searches " + name);
		// set 0 here
		VAData rootNode = VASyncData.getRootVAData(
				VAVariables.ontologyType.Source, 0);
		return searchFrom(name, rootNode);
	}

	private VAData searchFrom(String name, VAData rootNode) {
		// TODO Auto-generated method stub
		System.out.println("Search from " + rootNode.getNodeName());

		if (rootNode != null) {
			// find ontology
			if (rootNode.getNodeName() != null
					&& rootNode.getNodeName().equals(name)) {
				System.out.println("Return " + rootNode.getNodeName());
				return rootNode;
			}

			// Search children recursively
			if (rootNode.hasChildren()) {
				// set 0 here
				ArrayList<VAData> children = VASyncData.getChildrenData(
						rootNode, ontologyType.Source, 0);
				for (VAData childData : children) {
					VAData result = searchFrom(name, childData);
					if (result != null) {
						return result;
					}
				}
			}
		}

		return null;
	}
}
