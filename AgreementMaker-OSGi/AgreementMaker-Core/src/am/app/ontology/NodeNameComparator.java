package am.app.ontology;

import java.util.Comparator;


public class NodeNameComparator implements Comparator<Node> {

	@Override
	public int compare(Node o1, Node o2) {
		return String.CASE_INSENSITIVE_ORDER.compare(o1.getLocalName(), o2.getLocalName());
	}

}
