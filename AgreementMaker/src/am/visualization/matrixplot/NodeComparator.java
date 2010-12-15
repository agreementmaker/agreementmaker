package am.visualization.matrixplot;

import java.util.Comparator;

import com.hp.hpl.jena.ontology.OntClass;

import am.Utility;
import am.app.ontology.Node;

public class NodeComparator implements Comparator<Node> {

	@Override
	public int compare(Node n1, Node n2) {
		OntClass cl1 = null;
		OntClass cl2 = null;
		
		if(n1.getResource().canAs(OntClass.class))
			cl1 = n1.getResource().as(OntClass.class);
		
		if(n2.getResource().canAs(OntClass.class))
			cl2 = n2.getResource().as(OntClass.class);
		
		int d1 = getClassDepth(cl1, 1);
		int d2 = getClassDepth(cl2, 1);
		
		int diff = d1 - d2;
		
		if(diff != 0) return diff;
		
		return n1.getLocalName().compareTo(n2.getLocalName());
	}
	
	public static int getClassDepth(OntClass cl, int i) {
		OntClass parent = cl.getSuperClass();
		if(parent==null) return i;
		return getClassDepth(parent, i+1);
	}
}
