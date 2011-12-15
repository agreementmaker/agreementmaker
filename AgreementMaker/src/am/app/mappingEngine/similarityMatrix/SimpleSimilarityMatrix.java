package am.app.mappingEngine.similarityMatrix;

import java.io.FileWriter;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;

/**
 * This class implements a SimilarityMatrix that is not attached to any
 * Ontology objects.
 * 
 * TODO: Implement all methods.
 *
 */
public class SimpleSimilarityMatrix extends SimilarityMatrix {

	private static final long serialVersionUID = 4761466118469007566L;
	
	private double[][] data;
	
	public SimpleSimilarityMatrix(int rows, int cols, alignType type) {
		super(null, null, type);
		data = new double[rows][cols];
	}
	
	@Override public alignType getAlignType() { return typeOfMatrix; }
	@Override public int getRows() { return data.length; }
	@Override public int getColumns() { return data[0].length; }
	@Override public Mapping get(int i, int j) { return null; }
	@Override public void set(int i, int j, Mapping d) { data[i][j] = d.getSimilarity(); }
	
	public void setSimilarity(int i, int j, double sim) { data[i][j] = sim; }

	@Override public double getSimilarity(int i, int j) {	return data[i][j]; }

	@Override
	public List<Mapping> chooseBestN() {
		// TODO: Implement this later.
		return null;
	}

	@Override
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList,
			List<Integer> colsIncludedList, boolean considerThreshold,
			double threshold) {
		return null;
	}

	@Override
	public List<Mapping> chooseBestN(boolean considerThreshold, double threshold) {
		return null;
	}

	@Override
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList,
			List<Integer> colsIncludedList) {
		return null;
	}

	@Override
	public SimilarityMatrix clone() {
		return null;
	}

	@Override
	public void initFromNodeList(List<Node> sourceList, List<Node> targetList) {
	}

	@Override
	public Mapping[] getColMaxValues(int col, int numMaxValues) {
		return null;
	}

	@Override
	public Mapping[] getRowMaxValues(int row, int numMaxValues) {
		return null;
	}

	@Override
	public double[][] getCopiedSimilarityMatrix() {
		return data;
	}

	@Override
	public double getRowSum(int row) {
		return 0;
	}

	@Override
	public double getColSum(int col) {
		return 0;
	}

	@Override
	public void fillMatrix(double d, List<Node> sourceList,
			List<Node> targetList) {
	}

	@Override
	public double getMaxValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Mapping[] getTopK(int k) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping[] getTopK(int k, boolean[][] filteredCells) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> toMappingArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> toMappingArray(FileWriter fw, int round) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Double> toSimilarityArray(List<Mapping> mapsArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimilarityMatrix toArraySimilarityMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

}
