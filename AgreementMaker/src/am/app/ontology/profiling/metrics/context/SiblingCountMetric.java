package am.app.ontology.profiling.metrics.context;

import java.util.ArrayList;
import java.util.List;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.metrics.AbstractOntologyMetric;
import am.utility.numeric.AvgMinMaxNumber;

public class SiblingCountMetric extends AbstractOntologyMetric {

	public SiblingCountMetric(Ontology o) {
		super(o);
	}

	private AvgMinMaxNumber classesResult;
	
	@Override
	public void runMetric() {
		List<Node> classesList = ontology.getClassesList();
		
		int[] siblingsCount = new int[classesList.size()];
		
		for( int i = 0; i < classesList.size(); i++ ) {
			Node currentClass = classesList.get(i);
			siblingsCount[i] = countSiblings(currentClass);
		}
		
		classesResult = new AvgMinMaxNumber("Classes Siblings Count", siblingsCount);
	}

	private int countSiblings( Node currentClass ) {
		
		List<Node> parents = currentClass.getParents();
		
		if( parents == null || parents.size() == 0 ) return 0;
		
		int siblings = 0;
		for( Node currentParent : parents ) {
			siblings += currentParent.getChildCount() - 1;
		}
		
		return siblings;
	}

	@Override
	public List<AvgMinMaxNumber> getResult() {
		ArrayList<AvgMinMaxNumber> list = new ArrayList<AvgMinMaxNumber>();
		list.add(classesResult);
		return list;
	}
	
}
