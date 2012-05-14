package am.app.mappingEngine.similarityMatrix;

import java.util.ArrayList;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Ontology;
import am.utility.Pair;

/**
 * 
 * @author joe
 *
 * This class extends the sparse matrix and does not include mappings that are less then the threshold that 
 * is being used by the matcher.  By not including mappings that are less then the threshold, larger ontologies 
 * are able to be matched because memory is saved.
 *
 */

public class ApproximativeSparseMatrix extends SparseMatrix{
	
	/** Holds sum and count of the mappings that are lower then the threshold
	Each row and col will have its own index in the arraylist */ 
	private ArrayList<Pair<Double, Integer>> rows, cols;
	
	/** the threshold of similarity that determines if the mapping is included in the matrix.
	 * The .6D is the default threshold.  This can be changed by passing a threshold in the constructor or by using the setter method
	 * getThreshold() */
	private double threshold=.6D;
	

/**---------------------Constructors for the super class-------------------------------------------*/
	public ApproximativeSparseMatrix(Ontology sourceOntology, Ontology targetOntology,
			alignType typeOfMatrix) {
		super(sourceOntology, targetOntology, typeOfMatrix);
		init();
	}
	
	public ApproximativeSparseMatrix(SparseMatrix s){
		super(s);
		init();
	}
/**------------------------------------------------------------------------------------------------*/
	
	
/**---------------------Constructors that include the threshold as a param-------------------------*/	
	public ApproximativeSparseMatrix(Ontology sourceOntology, Ontology targetOntology,
			alignType typeOfMatrix, double threshold) {
		super(sourceOntology, targetOntology, typeOfMatrix);
		this.threshold=threshold;
		init();
	}
	
	public ApproximativeSparseMatrix(SparseMatrix s, double threshold){
		super(s);
		this.threshold=threshold;
		init();
	}
/**------------------------------------------------------------------------------------------------*/
	
	private void init(){
		/** make the arrays of the size of the number of rows and cols.  This will allow easier adding to the arrays because each index of the array
		 * relates to a row number or col number.*/
		rows= new ArrayList<Pair<Double, Integer>>(super.getRows());
		cols= new ArrayList<Pair<Double, Integer>>(super.getColumns());
	}
	
	/** override the set method of the sparse matrix so that we dont include anything lower then the threshold **/
	public void set(int row, int column, Mapping obj){
		/** if the similarity of the passed in Mapping is greater then the given threshold add it to the matrix,
		 * else add it to the approiate array.*/
		if(obj.getSimilarity() > threshold)
			super.set(row, column, obj);
		else{
			if(row < rows.size() || column < cols.size()){
				/** add the similarity of this Mapping to the correct row and then update the row count so the avearge can be calcuated.*/
				rows.get(row).setLeft(rows.get(row).getLeft()+obj.getSimilarity());
				rows.get(row).setRight(rows.get(row).getRight()+1);
				
				/** add the similarity of this Mapping to the correct col and then update the col count so the avearge can be calcuated.*/
				cols.get(column).setLeft(cols.get(column).getLeft()+obj.getSimilarity());
				cols.get(column).setRight(cols.get(column).getRight()+1);
			}else{
				//throw an error here
				System.err.println("The row or column in the params are out of bounds!");
			}
		}
	}
	
	/** returns the average for the specifed row**/
	public double getRowAverage(int row){
		if(row < rows.size())
			return rows.get(row).getLeft()/((double)rows.get(row).getRight());
		return 0.0D;//TODO: should I be returning 0.0D here?
	}
	
	/** returns the average for the specifed col**/
	public double getColAverage(int col){
		if(col < cols.size())
			return cols.get(col).getLeft()/((double)cols.get(col).getRight());
		return 0.0D;//TODO: should I be returning 0.0D here?
	}
	
	/** getters */
	public double getThreshold() {return threshold;}
	
	/** setters */
	public void setThreshold(double threshold) {this.threshold = threshold;}
}