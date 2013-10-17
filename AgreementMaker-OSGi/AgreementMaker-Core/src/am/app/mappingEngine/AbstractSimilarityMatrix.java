package am.app.mappingEngine;

import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
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
public abstract class AbstractSimilarityMatrix implements SimilarityMatrix, Serializable
{

	/* Fields */
	private static final long serialVersionUID = -6257833394450852075L;

	protected transient Ontology sourceOntology, targetOntology;
	
	//not used at the moment. At the moment indetermined similarity are set with 0.
	//if we want to start using it is important to keep it similar to 0, to allow compatibility with non-updated methods.
	//public final static double INDETERMINED = Double.MIN_NORMAL;
	
	//protected MappingRelation relation = MappingRelation.EQUIVALENCE; //this is a default relation used when no relation is specified for this matrix
	protected final alignType typeOfMatrix;
    
	//public MappingRelation getRelation() { return relation; };	// relation of the matrix? is this required? No -- Cosmin, Sept 17, 2011.
	@Override
	public abstract alignType getAlignType();
	
	public AbstractSimilarityMatrix(Ontology sourceOntology, Ontology targetOntology, alignType typeOfMatrix) {
		this.sourceOntology = sourceOntology;
		this.targetOntology = targetOntology;
		this.typeOfMatrix = typeOfMatrix;
	}
	
	/* Getters and Setters */
	
	@Override
	public Ontology getSourceOntology() { return sourceOntology; }
	
	@Override
	public Ontology getTargetOntology() { return targetOntology; }
	
	@Override
	public abstract int getRows();
	
	@Override
	public abstract int getColumns();
	
	/** 
	 * Returns the mapping between concept i of the source ontology and concept j of the target ontology.
	 * @param i Source index.
	 * @param j Target index.
	 * @return Returns null if no mapping exists.
	 */
	@Override
	public abstract Mapping get(int i, int j);

	@Override
	public abstract void set(int i, int j, Mapping d);
	
	@Override
	public abstract void setSimilarity(int i, int j, double similarity);
	
	//@Deprecated
	//public abstract void setSimilarity(int i, int j, double d);  // deprecated because it cannot deal with null values.
	@Override
	public abstract double getSimilarity( int i, int j);
	
	/* Methods that calculate */
	
	@Override
	public abstract List<Mapping> chooseBestN();
	
	@Override
	public abstract List<Mapping> chooseBestN(List<Integer> rowsIncludedList, List<Integer> colsIncludedList, boolean considerThreshold, double threshold);
	
	@Override
	public abstract List<Mapping> chooseBestN(boolean considerThreshold, double threshold);
	
	@Override
	public abstract List<Mapping> chooseBestN(List<Integer> rowsIncludedList,	List<Integer> colsIncludedList);
	
	public abstract SimilarityMatrix clone();  // TODO: Should not be abstract? Investigate.
	
	public abstract void initFromNodeList(List<Node> sourceList, List<Node> targetList);
	
	@Override
	public abstract Mapping[] getColMaxValues(int col, int numMaxValues);
	
	public abstract Mapping[] getRowMaxValues(int row, int numMaxValues);
	public abstract double[][] getCopiedSimilarityMatrix();
	public abstract double getRowSum(int row);
	public abstract double getColSum(int col);
	
	public abstract void fillMatrix(double d, List<Node> sourceList, List<Node> targetList);
	
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
	@Override
	public List<Mapping> toList() throws Exception {
		List<Mapping> list = new ArrayList<Mapping>();
		for( int row = 0; row < getRows(); row++ ) {
			for( int col = 0; col < getColumns(); col++ ) {
				Mapping m = get(row, col);
				if( m == null ) {
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
	
	//public abstract int countNonNullValues(); // TODO: What is this used for? Investigate.
	
	public abstract List<Mapping> toMappingArray();
	public abstract List<Mapping> toMappingArray(FileWriter fw, int round);
	public abstract List<Double> toSimilarityArray(List<Mapping> mapsArray);
}
