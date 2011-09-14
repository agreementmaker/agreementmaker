package am.app.ontology.profiling.metrics.multipleinheritance;

import java.util.List;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.metrics.AbstractOntologyMetric;
import am.utility.numeric.AvgMinMaxNumber;

public class MultipleInheritanceMetric extends AbstractOntologyMetric {

	public MultipleInheritanceMetric(Ontology o) {
		super(o);
	}

	private AvgMinMaxNumber classesResult;
	
	@Override
	public void runMetric() {
		List<Node> classesList = ontology.getClassesList();
		
		int[] parentsCount = new int[classesList.size()];
		
		for( int i = 0; i < classesList.size(); i++ ) {
			Node currentClass = classesList.get(i);
			parentsCount[i] = currentClass.getParentCount();
		}
		
		classesResult = new AvgMinMaxNumber("Classes Multiple Inheritance Count", parentsCount);
	}

	@Override public boolean hasSingleValueResult() { return true; }

	@Override
	public AvgMinMaxNumber getSingleValueResult() { return classesResult; }

}
