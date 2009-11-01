package am.app.feedback.measures;

import java.util.Comparator;

import am.app.ontology.Node;

public class NodeComparator implements Comparator<Node>{

	
	
	@Override
	public int compare(Node o1, Node o2) {
		if(o1.getIndex() < o2.getIndex())
			return -1;
		else if (o1.getIndex() > o2.getIndex())
			return 1;
		else
			return 0;
	}

}
