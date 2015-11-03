package am.va.graph;

public class VASearcher {

	public VAData search(String name, int set) {
		//System.out.println("Searcher searches " + name);
		// set 0 here
		VAData rootNode = VASyncData.getRootVAData(VAVariables.ontologyType.Source, 0);
		return searchFrom(name, rootNode, set);
	}

	private VAData searchFrom(String name, VAData rootNode, int set) {
		// TODO Auto-generated method stub
		// System.out.println("Search from " + rootNode.getNodeName());
		
		//get the first part
		//name = name.split("|")[0];
		
		return VASyncData.searchFrom(name, rootNode, set);
	}
}
