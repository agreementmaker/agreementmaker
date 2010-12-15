package am.visualization.matrixplot;

import java.util.ArrayList;
import java.util.Collections;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class OrderedMatrixPlot extends MatrixPlot {
	int[] rowTransform;
	int[] colTransform;
	
	int[] rowInverseTransform;
	int[] colInverseTransform;
		
	public OrderedMatrixPlot(SimilarityMatrix mtx) {
		super(mtx);
	}
	
	private void createColTransform() {
		Ontology targetOntology = Core.getInstance().getOntologyByID(matrix.getTargetOntologyID());
		ArrayList<Node> nodes = null;
		
		if(matrix.getAlignType() == alignType.aligningClasses)
			nodes = targetOntology.getClassesList();
		else nodes = targetOntology.getPropertiesList();
		
		colTransform = new int[nodes.size()];
		colInverseTransform = new int[nodes.size()];
		
//		System.out.println("PREORDERING");
//		
//		for (int i = 0; i < nodes.size(); i++) {
//			System.out.println(nodes.get(i).getLocalName());
//		}
		
		ArrayList<Node> ordered = (ArrayList<Node>)nodes.clone();
		Collections.sort(ordered, new NodeComparator());
		
//		System.out.println("POSTORDERING");
//		
//		for (int i = 0; i < ordered.size(); i++) {
//			System.out.println(ordered.get(i).getLocalName());
//		}
		
		for (int i = 0; i < colTransform.length; i++) {
			colTransform[i] = ordered.get(i).getIndex();
		}
		
		boolean found;
		for (int i = 0; i < colTransform.length; i++){
			found = false;
			for (int j = 0; j < colTransform.length && !found; j++) {
				if(colTransform[j]==i){
					colInverseTransform[i] = j;
					found = true;
				}
			}
		}
	
	}

	private void createRowTransform() {
		int ontId = matrix.getSourceOntologyID();
		Ontology sourceOntology = Core.getInstance().getOntologyByID(ontId);
		ArrayList<Node> nodes = null;
		
		if(matrix.getAlignType() == alignType.aligningClasses)
			nodes = sourceOntology.getClassesList();
		else nodes = sourceOntology.getPropertiesList();
		
		rowTransform = new int[nodes.size()];
		rowInverseTransform = new int[nodes.size()];
		
		ArrayList<Node> ordered = (ArrayList<Node>)nodes.clone();
		Collections.sort(ordered, new NodeComparator());
		
		for (int i = 0; i < rowTransform.length; i++) {
			rowTransform[i] = ordered.get(i).getIndex();
		}
		
		boolean found;
		
		for (int i = 0; i < rowInverseTransform.length; i++){
			found = false;
			for (int j = 0; j < rowInverseTransform.length && !found; j++) {
				if(rowTransform[j]==i){
					rowInverseTransform[i] = j;
					found = true;
				}
			}
		}
	
	}
	
	@Override
	public int translateCol(int originalCol) {
		if(colTransform == null ) {
			createColTransform();
		}
		return colTransform[originalCol];
	}
	
	@Override
	public int translateRow(int originalRow) {
		if(rowTransform == null ) {
			createRowTransform();
		}
		return rowTransform[originalRow];
	}
	
	@Override
	public int inverseTranslateCol(int translatedCol) {
		return colInverseTransform[translatedCol];
	}
	
	@Override
	public int inverseTranslateRow(int translatedRow) {
		return rowInverseTransform[translatedRow];
	}
}
