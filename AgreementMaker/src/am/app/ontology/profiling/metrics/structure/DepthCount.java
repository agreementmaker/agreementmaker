package am.app.ontology.profiling.metrics.structure;

import java.util.ArrayList;
import java.util.List;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.metrics.AbstractOntologyMetric;
import am.utility.numeric.AvgMinMaxNumber;

public class DepthCount extends AbstractOntologyMetric {

	public DepthCount(Ontology o) { super(o); }

	private AvgMinMaxNumber classesResult;
	
	@Override
	public void runMetric() {
		List<Node> classesList = ontology.getClassesList();
		
		int[] childrenCount = new int[classesList.size()];
		
		for( int i = 0; i < classesList.size(); i++ ) {
			Node currentClass = classesList.get(i);
			childrenCount[i] = currentClass.getMaxDepth();
		}
		
		classesResult = new AvgMinMaxNumber("Classes Depth Count", childrenCount);
	}

	@Override
	public List<AvgMinMaxNumber> getResult() {
		ArrayList<AvgMinMaxNumber> list = new ArrayList<AvgMinMaxNumber>();
		list.add(classesResult);
		return list;
	}

}
