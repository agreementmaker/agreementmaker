package am.app.mappingEngine;

import java.util.ArrayList;
import java.util.Vector;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;

/**
 * This interface represents a Similarity Matrix.
 * 
 * The idea is to separate the definition of a Similarity Matrix from the
 * implementation.
 * 
 * We can have similarity matrices 
 * 	(1) in a memory array (ArraySimilarityMatrix)
 * 	(2) in a sparse matrix representation
 * 	(3) in a database
 */
public interface SimilarityMatrix
{

	String getRelation();	// relation of the matrix? is this required?
	alignType getAlignType();
	
	int getSourceOntologyID();
	int getTargetOntologyID();
	void setTargetOntologyID(int targetOntologyID);
	void setSourceOntologyID(int sourceOntologyID);
	
	int getRows();
	int getColumns();
	
	public Mapping get(int i, int j);
	public void set(int i, int j, Mapping d);
	
	@Deprecated
	public void setSimilarity(int i, int j, double d);
	public double getSimilarity( int i, int j);
	
	public ArrayList<Mapping> chooseBestN();
	public ArrayList<Mapping> chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList, boolean considerThreshold, double threshold);
	
	SimilarityMatrix clone();
	ArrayList<Mapping> chooseBestN(boolean considerThreshold, double threshold);
	ArrayList<Mapping> chooseBestN(ArrayList<Integer> rowsIncludedList,
			ArrayList<Integer> colsIncludedList);
	
	void initFromNodeList(ArrayList<Node> sourceList, ArrayList<Node> targetList);
	
	Mapping[] getColMaxValues(int col, int numMaxValues);
	Mapping[] getRowMaxValues(int row, int numMaxValues);
	double[][] getCopiedSimilarityMatrix();
	double getRowSum(int row);
	double getColSum(int col);
	
	void fillMatrix(double val);
	double getMaxValue();
	Mapping[] getTopK(int k);
	Mapping[] getTopK(int k, boolean[][] filteredCells);
	
	Vector<Mapping> toMappingArray();
	Vector<Double> toSimilarityArray(Vector<Mapping> mapsArray);
	
}
