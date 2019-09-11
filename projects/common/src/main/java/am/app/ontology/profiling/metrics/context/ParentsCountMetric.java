package am.app.ontology.profiling.metrics.context;

import java.util.ArrayList;
import java.util.List;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.metrics.AbstractOntologyMetric;
import am.utility.numeric.AvgMinMaxNumber;

public class ParentsCountMetric extends AbstractOntologyMetric {

	public ParentsCountMetric(Ontology o) {
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
		
		classesResult = new AvgMinMaxNumber("Classes Parent Count", parentsCount);
	}

	@Override
	public List<AvgMinMaxNumber> getResult() {
		ArrayList<AvgMinMaxNumber> list = new ArrayList<AvgMinMaxNumber>();
		list.add(classesResult);
		return list;
	}
}
