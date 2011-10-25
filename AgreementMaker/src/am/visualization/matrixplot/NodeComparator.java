package am.visualization.matrixplot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import am.Utility;
import am.app.ontology.Node;

public class NodeComparator implements Comparator<Node> {

	@Override
	public int compare(Node n1, Node n2) {
		
		if(n1.getResource().canAs(OntClass.class)){
			OntClass cl1 = null;
			OntClass cl2 = null;
			
			cl1 = n1.getResource().as(OntClass.class);
			
			if(n2.getResource().canAs(OntClass.class))
				cl2 = n2.getResource().as(OntClass.class);
			
			int d1 = getClassDepth(cl1, 1);
			int d2 = getClassDepth(cl2, 1);
			
			int diff = d1 - d2;
			
			if(diff != 0) return diff;
			
			return n1.getLocalName().compareTo(n2.getLocalName());
		}
		else{
			OntProperty pr1 = null;
			OntProperty pr2 = null;
			
			pr1 = n1.getResource().as(OntProperty.class);
			
			if(n2.getResource().canAs(OntProperty.class))
				pr2 = n2.getResource().as(OntProperty.class);
			
			int d1 = getPropertyDepth(pr1, 1);
			int d2 = getPropertyDepth(pr2, 1);
			
			int diff = d1 - d2;
			
			if(diff != 0) return diff;
			
			return n1.getLocalName().compareTo(n2.getLocalName());
		}
			
	}
	
	public static int getClassDepth(OntClass cl, int i) {
		List<OntClass> parents = cl.listSuperClasses().toList();
		
		for (int j = 0; j < parents.size(); j++) {
			if(parents.get(j).isAnon()) continue;
			return getClassDepth(parents.get(j), i+1);
		}
		return i;
	}
	
	public static int getPropertyDepth(OntProperty pr, int i) {
		List<? extends OntProperty> parents = pr.listSuperProperties().toList();
		
		for (int j = 0; j < parents.size(); j++) {
			if(parents.get(j).isAnon()) continue;
			return getPropertyDepth(parents.get(j), i+1);
		}
		return i;
	}
}
