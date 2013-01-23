package am.app.mappingEngine.similarityMatrix;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;

/**
 * An aggregative sparse matrix does not store mappings below a certain
 * threshold.
 * 
 * Using the Decorator design pattern to encapsulate 
 * @author Cosmin Stroe (cstroe@gmail.com)
 * 
 */
public class AggregativeSparseMatrix implements SimilarityMatrix {

	private SparseMatrix m;

	@Override
	public Ontology getSourceOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ontology getTargetOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRows() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColumns() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Mapping get(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(int i, int j, Mapping d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getSimilarity(int i, int j) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public alignType getAlignType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping[] getColMaxValues(int col, int numMaxValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping[] getRowMaxValues(int i, int numMaxValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[][] getCopiedSimilarityMatrix() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SimilarityMatrix clone() {
		return null;
	}

	@Override
	public double getRowSum(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Mapping> chooseBestN() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> chooseBestN(List<Integer> rows, List<Integer> cols) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getMaxValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Mapping[] getTopK(int k, boolean[][] filteredCells) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping[] getTopK(int k) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList,
			List<Integer> colsIncludedList, boolean considerThreshold,
			double threshold) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> chooseBestN(boolean considerThreshold, double threshold) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> toList() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
