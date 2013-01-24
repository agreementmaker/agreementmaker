package am.app.mappingEngine.similarityMatrix;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;

public interface SimilarityMatrix {

	public Ontology getSourceOntology();
	
	public Ontology getTargetOntology();
	
	public int getRows();
	
	public int getColumns();
	
	public Mapping get(int i, int j);
	
	public void set(int i, int j, Mapping d);
	
	public double getSimilarity( int i, int j);
	
	public abstract alignType getAlignType();

	public Mapping[] getColMaxValues(int col, int numMaxValues);

	public Mapping[] getRowMaxValues(int i, int numMaxValues);

	public double[][] getCopiedSimilarityMatrix();

	public SimilarityMatrix clone();

	public double getRowSum(int i);

	
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

	

}
