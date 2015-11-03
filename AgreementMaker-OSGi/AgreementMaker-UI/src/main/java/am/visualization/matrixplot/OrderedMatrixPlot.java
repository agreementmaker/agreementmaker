package am.visualization.matrixplot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;

public class OrderedMatrixPlot extends MatrixPlot {

	private static final long serialVersionUID = -6639837299305451033L;
	
	int[] rowTransform;
	int[] colTransform;
	
	int[] rowInverseTransform;
	int[] colInverseTransform;
	
	ArrayList<Integer> colLines;
	ArrayList<Integer> rowLines;	
	
	public OrderedMatrixPlot(MatchingTask matcher, SimilarityMatrix mtx, MatrixPlotPanel mpnl) {
		super(matcher, mtx, mpnl);
		autoDrawCrosshairs = false;
		
	}
	
	private void createColTransform() {
		Ontology targetOntology = matrix.getTargetOntology();
		List<Node> nodes = null;
		
		colLines = new ArrayList<Integer>();
		
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
		
		List<Node> ordered = new ArrayList<Node>(nodes);
		Collections.sort(ordered, new NodeComparator());
		
//		System.out.println("POSTORDERING");
//		
//		for (int i = 0; i < ordered.size(); i++) {
//			System.out.println(ordered.get(i).getLocalName());
//		}
		
		createTransformAndLines(colLines, ordered, colTransform);
		
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

		Ontology sourceOntology = matrix.getSourceOntology();
		List<Node> nodes = null;
		
		rowLines = new ArrayList<Integer>();
		
		if(matrix.getAlignType() == alignType.aligningClasses)
			nodes = sourceOntology.getClassesList();
		else nodes = sourceOntology.getPropertiesList();
		
		rowTransform = new int[nodes.size()];
		rowInverseTransform = new int[nodes.size()];
		
		List<Node> ordered = new ArrayList<Node>(nodes); // (ArrayList<Node>)nodes.clone();
		Collections.sort(ordered, new NodeComparator());
		
		for (int i = 0; i < rowTransform.length; i++) {
			rowTransform[i] = ordered.get(i).getIndex();
		}
		
		createTransformAndLines(rowLines, ordered, rowTransform);
		
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
	
	public void createTransformAndLines(List<Integer> lines, List<Node> ordered,int[] transform){
		int prevDepth = 1;
		int currDepth;
		for (int i = 0; i < transform.length; i++) {
			if(matrix.getAlignType() == alignType.aligningClasses){
				OntClass cl = ordered.get(i).getResource().as(OntClass.class);
				currDepth = NodeComparator.getClassDepth(cl, 1);
				System.out.println(cl.getLocalName()+" "+currDepth);
			}
			else{
				OntProperty pr = ordered.get(i).getResource().as(OntProperty.class);
				currDepth = NodeComparator.getPropertyDepth(pr, 1);
			}
				
			if(currDepth!=prevDepth){
				lines.add(i);
				System.out.println("adding line "+ i);
			}
			prevDepth = currDepth;				
			transform[ordered.get(i).getIndex()] = i;
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D gPlotArea = (Graphics2D)g;
		
		//Draw depth lines
		gPlotArea.setColor(Color.RED);
		
		for (int i = 0; i < rowLines.size(); i++) {
			int row = rowLines.get(i);
			gPlotArea.drawLine( row * squareSize, 0, row * squareSize, getHeight() );
		}
		
		for (int i = 0; i < colLines.size(); i++) {
			int col = colLines.get(i);
			gPlotArea.drawLine( 0, col * squareSize, getWidth(), col * squareSize);
		}
		
		drawCrosshairs((Graphics2D)g);		
	}
}
