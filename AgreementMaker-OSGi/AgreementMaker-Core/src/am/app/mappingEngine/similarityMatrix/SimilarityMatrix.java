package am.app.mappingEngine.similarityMatrix;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public interface SimilarityMatrix {

	public Ontology getSourceOntology();
	
	public Ontology getTargetOntology();
	
	public int getRows();
	
	public int getColumns();
	
	public Mapping get(int i, int j);
	
	
	public void set(int i, int j, Mapping d);
	
	/**
	 * This method sets the similarity for a cell of the similarity matrix.
	 * @param i The row index.
	 * @param j The column index.
	 * @param similarity The similarity value, in the range [0,1].
	 */
	public void setSimilarity(int i, int j, double similarity);
	
	public double getSimilarity( int i, int j);
	
	public abstract alignType getAlignType();

	/**
	 * @param i
	 *            The index of the row to be retrieved.
	 * @return the i-th row of the matrix as an array.
	 *//*
	public Mapping[] getRow(int i);
	
	*//**
	 * @param j
	 *            The index of the column to be retrieved.
	 * @return the j-th column of the matrix as an array.
	 *//*
	public Mapping[] getCol(int j);*/
	
	public Mapping[] getColMaxValues(int col, int numMaxValues);

	public Mapping[] getRowMaxValues(int i, int numMaxValues);

	public double[][] getCopiedSimilarityMatrix();

	public SimilarityMatrix clone();

	public double getRowSum(int i);
	
	public double getColSum(int j);

	
	// used by GroupFinderMatcher
	// FIXME: Remove from interface.
	public List<Mapping> chooseBestN();

	// used by GroupFinderMatcher, BasicStructureSelectorMatcher
	// FIXME: Remove from interface.
	public List<Mapping> chooseBestN(List<Integer> rows, List<Integer> cols);

	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList,
			List<Integer> colsIncludedList, boolean considerThreshold,
			double threshold);
	
	public List<Mapping> chooseBestN(boolean considerThreshold, double threshold);
	
	// used by MatcherAnalyticsPanel
	// FIXME: Remove?
	public double getMaxValue();

	// used by MatcherAnalyticsPanel
	// FIXME: Remove?
	public Mapping[] getTopK(int k, boolean[][] filteredCells);

	public Mapping[] getTopK(int k);

	// Used by AgreementMaker-UserFeedback:DisagreementRanking
	public List<Mapping> toList() throws Exception ;

	public void fillMatrix(double sim, List<Node> sourceList, List<Node> targetList);

}
