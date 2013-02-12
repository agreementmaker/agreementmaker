package am.app.mappingEngine.similarityMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;

/**
 * An aggregative sparse matrix does not store mappings below a certain
 * threshold.
 * 
 * Using the Decorator design pattern to encapsulate a SparseMatrix.
 * @author Cosmin Stroe (cstroe@gmail.com)
 * 
 */
public class AggregativeSparseMatrix implements SimilarityMatrix {

	public final int rows;
	public final double threshold;
	
	private Ontology sourceOntology;
	private Ontology targetOntology;
	
	private SparseMatrix m;
	
	public AggregativeSparseMatrix(Ontology sourceOntology, Ontology targetOntology, double threshold) {
		this.threshold = threshold;
		this.sourceOntology = sourceOntology;
		this.targetOntology = targetOntology;
		
		this.rows = sourceOntology.getClassesList().size();
		
		m = new SparseMatrix(sourceOntology, targetOntology, alignType.aligningClasses);
	}
	
	@Override
	public Ontology getSourceOntology() {
		return sourceOntology;
	}

	@Override
	public Ontology getTargetOntology() {
		return targetOntology;
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public int getColumns() {
		return targetOntology.getClassesList().size();
	}

	@Override
	public Mapping get(int i, int j) {
		return m.get(i, j);
	}

	@Override
	public void set(int i, int j, Mapping d) {
		if( d.getSimilarity() >= threshold ) {
			m.set(i, j, d);
		}
	}

	@Override
	public double getSimilarity(int i, int j) {
		return m.getSimilarity(i, j);
	}

	@Override
	public alignType getAlignType() {
		return m.getAlignType();
	}

	@Override
	public Mapping[] getColMaxValues(int col, int numMaxValues) {
		return m.getColMaxValues(col, numMaxValues);
	}

	@Override
	public Mapping[] getRowMaxValues(int i, int numMaxValues) {
		return m.getRowMaxValues(i, numMaxValues);
	}

	@Override
	public double[][] getCopiedSimilarityMatrix() {
		return m.getCopiedSimilarityMatrix();
	}
	
	@Override
	public SimilarityMatrix clone() {
		return m.clone();
	}

	@Override
	public double getRowSum(int i) {
		return m.getRowSum(i);
	}

	@Override
	public List<Mapping> chooseBestN() {
		return m.chooseBestN();
	}

	@Override
	public List<Mapping> chooseBestN(List<Integer> rows, List<Integer> cols) {
		return m.chooseBestN(rows, cols);
	}

	@Override
	public double getMaxValue() {
		return m.getMaxValue();
	}

	@Override
	public Mapping[] getTopK(int k, boolean[][] filteredCells) {
		return m.getTopK(k, filteredCells);
	}

	@Override
	public Mapping[] getTopK(int k) {
		return m.getTopK(k);
	}

	@Override
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList,
			List<Integer> colsIncludedList, boolean considerThreshold,
			double threshold) {
		return m.chooseBestN(rowsIncludedList, colsIncludedList, considerThreshold, threshold);
	}

	@Override
	public List<Mapping> chooseBestN(boolean considerThreshold, double threshold) {
		return m.chooseBestN(considerThreshold, threshold);
	}

	@Override
	public List<Mapping> toList() throws Exception {
		return m.toList();
	}
	
}
