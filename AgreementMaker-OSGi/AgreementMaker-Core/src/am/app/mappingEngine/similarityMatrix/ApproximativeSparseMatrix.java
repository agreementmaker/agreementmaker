package am.app.mappingEngine.similarityMatrix;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
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

public class ApproximativeSparseMatrix extends SparseMatrix {
	
	/** Holds sum and count of the mappings that are lower then the threshold
	Each row and col will have its own index in the arraylist */ 
	private Pair<Double, Integer>[] rows, cols;
	
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
		rows= new Pair[super.getRows()];
		cols= new Pair[super.getColumns()];
		for(int i=0;i<super.getRows();i++)
			rows[i]=new Pair<Double, Integer>(0.0D, 0);
		for(int i=0;i<super.getColumns();i++)
			cols[i]=new Pair<Double, Integer>(0.0D,0);
	}
	
	/** override the set method of the sparse matrix so that we dont include anything lower then the threshold **/
	public void set(int row, int column, Mapping obj){
		/** if the similarity of the passed in Mapping is greater then the given threshold add it to the matrix,
		 * else add it to the approiate array.*/
		if(obj.getSimilarity() > threshold)
			super.set(row, column, obj);
		else{
			if(row < rows.length || column < cols.length){
				/** add the similarity of this Mapping to the correct row and then update the row count so the avearge can be calcuated.*/
				rows[row].setLeft(rows[row].getLeft()+obj.getSimilarity());
				rows[row].setRight(rows[row].getRight()+1);
				
				/** add the similarity of this Mapping to the correct col and then update the col count so the avearge can be calcuated.*/
				cols[column].setLeft(cols[column].getLeft()+obj.getSimilarity());
				cols[column].setRight(cols[column].getRight()+1);
			}else{
				//throw an error here
				System.err.println("The row or column in the params are out of bounds! row="+row+" size of array="+rows.length+" col="+column+" cols.size="+cols.length);
			}
		}
	}
	
	/** returns the average for the specifed row**/
	public double getRowAverage(int row){
		if(row < rows.length)
			return rows[row].getLeft()/((double)rows[row].getRight());
		return 0.0D;//TODO: should I be returning 0.0D here?
	}
	
	/** returns the average for the specifed col**/
	public double getColAverage(int col){
		if(col < cols.length)
			return cols[col].getLeft()/((double)cols[col].getRight());
		return 0.0D;//TODO: should I be returning 0.0D here?
	}
	
	/** Overrides the super method so that the values not included in the matrix are included. */
	public double getRowSum(int row){
		double d=super.getRowSum(row);
		d+=rows[row].getLeft();
		return d;
	}
	
	/** Overrides the super method so that the values not included in the matrix are included. */
	public double getColSum(int col){
		double d=super.getColSum(col);
		d+=cols[col].getLeft();
		return d;
	}
	
	
	/** getters */
	public double getThreshold() {return threshold;}
	
	/** setters */
	public void setThreshold(double threshold) {this.threshold = threshold;}
}