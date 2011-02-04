package am.app.mappingEngine;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

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
public abstract class SimilarityMatrix
{

	/* Fields */
	
    protected int sourceOntologyID, targetOntologyID;
	
	//not used at the moment. At the moment indetermined similarity are set with 0.
	//if we want to start using it is important to keep it similar to 0, to allow compatibility with non-updated methods.
	final static double INDETERMINED = Double.MIN_NORMAL;
	
	protected String relation = Mapping.EQUIVALENCE; //this is a default relation used when no relation is specified for this matrix
	protected alignType typeOfMatrix;
    
	public abstract String getRelation();	// relation of the matrix? is this required?
	public abstract alignType getAlignType();
	
	/* Getters and Setters */
	
	public int getSourceOntologyID() { return sourceOntologyID; }
	public void setSourceOntologyID(int sourceOntologyID) { this.sourceOntologyID = sourceOntologyID; }
	public int getTargetOntologyID() { return targetOntologyID; }
	public void setTargetOntologyID(int targetOntologyID) { this.targetOntologyID = targetOntologyID; }
	
	public abstract int getRows();
	public abstract int getColumns();
	
	public abstract Mapping get(int i, int j);
	public abstract void set(int i, int j, Mapping d);
	
	@Deprecated
	public abstract void setSimilarity(int i, int j, double d);  // deprecated because it cannot deal with null values.
	public abstract double getSimilarity( int i, int j);
	
	/* Methods that calculate */
	
	public abstract ArrayList<Mapping> chooseBestN();
	public abstract ArrayList<Mapping> chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList, boolean considerThreshold, double threshold);
	public abstract ArrayList<Mapping> chooseBestN(boolean considerThreshold, double threshold);
	public abstract ArrayList<Mapping> chooseBestN(ArrayList<Integer> rowsIncludedList,	ArrayList<Integer> colsIncludedList);
	
	public abstract SimilarityMatrix clone();  // TODO: Should not be abstract? Investigate.
	
	public abstract void initFromNodeList(ArrayList<Node> sourceList, ArrayList<Node> targetList);
	
	public abstract Mapping[] getColMaxValues(int col, int numMaxValues);
	public abstract Mapping[] getRowMaxValues(int row, int numMaxValues);
	public abstract double[][] getCopiedSimilarityMatrix();
	public abstract double getRowSum(int row);
	public abstract double getColSum(int col);
	
	public abstract void fillMatrix(double d, ArrayList<Node> sourceList, ArrayList<Node> targetList);
	
	/* Mapping retrieval methods */
	
	/**
	 * This method should return an descending ordered list of mappings that have
	 * a similarity equal to or above the given threshold. 
	 * 
	 * If you do not want a threshold, set th = 0.0.
	 * 
	 * Complexity: O( n^2 + n log n );
	 * 
	 * @param th A threshold.  Mappings with similarity equal to or above this threshold will be returned.
	 * @return Descending list of Mappings with similarity >= th. 
	 */
	public List<Mapping> getOrderedMappingsAboveThreshold( double th ) {
		
		Vector<Mapping> mappingArray = new Vector<Mapping>();
		for(int i = 0; i < getRows(); i++){
			for(int j = 0; j < getColumns(); j++){
				Mapping currentMapping = get(i,j);
				if(currentMapping != null && currentMapping.getSimilarity() >= th ){
					mappingArray.add(this.get(i, j));
				}
			}
		}
		
		Collections.sort(mappingArray, new MappingSimilarityComparator() );
		return mappingArray;
	}
	
	
	public List<Mapping> getOrderedMappingsWithNull() {
		
		Vector<Mapping> mappingArray = new Vector<Mapping>();
		for(int i = 0; i < getRows(); i++){
			for(int j = 0; j < getColumns(); j++){
				Mapping currentMapping = get(i,j);
				if( currentMapping == null ) mappingArray.add( new Mapping(null, null, 0.0d) );
				else {mappingArray.add(this.get(i, j)); }
			}
		}
		
		Collections.sort(mappingArray, new MappingSimilarityComparator() );
		return mappingArray;
	}
	
	
	/**
	 * This method returns all the mappings in the matrix.
	 * Null entries are allocated new Mapping object with similarity = 0.0.
	 * @return
	 * @throws Exception 
	 */
	public List<Mapping> toList() throws Exception {
		ArrayList<Mapping> list = new ArrayList<Mapping>();
		for( int row = 0; row < getRows(); row++ ) {
			for( int col = 0; col < getColumns(); col++ ) {
				Mapping m = get(row, col);
				if( m == null ) {
					Ontology sourceOntology = Core.getInstance().getOntologyByID(sourceOntologyID);
					Ontology targetOntology = Core.getInstance().getOntologyByID(targetOntologyID);
					
					if( typeOfMatrix == alignType.aligningClasses ) {
						Node sN = sourceOntology.getClassesList().get(row);
						Node tN = targetOntology.getClassesList().get(col);
						list.add( new Mapping(sN, tN, 0.0d) );
					} else if( typeOfMatrix == alignType.aligningProperties ) {
						Node sN = sourceOntology.getPropertiesList().get(row);
						Node tN = targetOntology.getPropertiesList().get(col);
						list.add( new Mapping(sN, tN, 0.0d) );
					} else {
						throw new Exception("Similarity Matrix in invalid state.");
					}
				}
				else list.add(m);
			}
		}
		return list;
	}
	
	
	/* Ranking methods */
	public abstract double getMaxValue(); // TODO: Should not be abstract.
	
	public abstract Mapping[] getTopK(int k); // TODO: Should not be abstract.
	public abstract Mapping[] getTopK(int k, boolean[][] filteredCells); // TODO: Should not be abstract.
	
	public abstract int countNonNullValues(); // TODO: What is this used for? Investigate.
	
	public abstract Vector<Mapping> toMappingArray();
	public abstract Vector<Mapping> toMappingArray(FileWriter fw, int round);
	public abstract Vector<Double> toSimilarityArray(Vector<Mapping> mapsArray);
	
	public abstract SimilarityMatrix toArraySimilarityMatrix();  // TODO: Is this necessary? Investigate.
	
}
