package am.app.ontology.profiling.metrics.annotation;

import java.util.ArrayList;
import java.util.List;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.metrics.AbstractOntologyMetric;
import am.utility.numeric.AvgMinMaxNumber;

public class AnnotationStringLengthMetric extends AbstractOntologyMetric {

	public AnnotationStringLengthMetric(Ontology o) {
		super(o);
	}

	private AvgMinMaxNumber classesLabelResult;
	
	@Override
	public void runMetric() {
		List<Node> classesList = ontology.getClassesList();
		
		int[] labelStringLength = new int[classesList.size()];
		
		for( int i = 0; i < classesList.size(); i++ ) {
			Node currentClass = classesList.get(i);
			labelStringLength[i] = currentClass.getLabel().length();
		}
		
		classesLabelResult = new AvgMinMaxNumber("Classes Label Length", labelStringLength);
	}

	@Override
	public List<AvgMinMaxNumber> getResult() {
		List<AvgMinMaxNumber> list = new ArrayList<AvgMinMaxNumber>();
		list.add(classesLabelResult);
		return list;
	}

}
