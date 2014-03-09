package am.va.graph;

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
		//System.out.println("Search from " + rootNode.getNodeName());
		return VASyncData.searchFrom(name, rootNode);
	}
}
