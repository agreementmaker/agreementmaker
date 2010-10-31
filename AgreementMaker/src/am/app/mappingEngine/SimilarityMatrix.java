package am.app.mappingEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class SimilarityMatrix implements Serializable
{	
	private static final long serialVersionUID = -8724503849185030524L; // do not change the serialVersionUID unless required to break backward compatibility

	//not used at the moment. At the moment indetermined similarity are set with 0.
	//if we want to start using it is important to keep it similar to 0, to allow compatibility with non-updated methods.
	final static double INDETERMINED = Double.MIN_NORMAL;
	
	protected String relation = Mapping.EQUIVALENCE; //this is a default relation used when no relation is specified for this matrix
	protected alignType typeOfMatrix;
    protected final int rows;             // number of rows
    protected final int columns;             // number of columns
    protected final Mapping[][] data;   // M-by-N array

    protected int sourceOntologyID;
    protected int targetOntologyID;

    
    public int getSourceOntologyID() { return sourceOntologyID; }
	public void setSourceOntologyID(int sourceOntologyID) { this.sourceOntologyID = sourceOntologyID; }
	public int getTargetOntologyID() { return targetOntologyID; }
	public void setTargetOntologyID(int targetOntologyID) { this.targetOntologyID = targetOntologyID; }

	// cloning constructor
    public SimilarityMatrix( SimilarityMatrix cloneme ) {
    	
	    	relation = cloneme.getRelation();
	    	typeOfMatrix = cloneme.getAlignType();
	    	
	    	sourceOntologyID = cloneme.getSourceOntologyID();
	    	targetOntologyID = cloneme.getTargetOntologyID();
	    	
	    	rows = cloneme.getRows();
	    	columns = cloneme.getColumns();
	    	
	    	data = new Mapping[rows][columns];
	    	
	   		for(int i=0; i< cloneme.getRows(); i++) {
	   			for(int j = 0; j < cloneme.getColumns(); j++) {
	   				Mapping a = cloneme.get(i, j);
	   				if( a != null ) {
	   					data[i][j] = new Mapping(a.getEntity1(), a.getEntity2(), a.getSimilarity(), a.getRelation(), a.getAlignmentType());
	   				} else data[i][j] = null;
	   			}
	   		}
    	
    }
    
    // create M-by-N matrix of 0's with equivalence relation
    public SimilarityMatrix(int M, int N, alignType type) {
    	relation = Mapping.EQUIVALENCE;
    	typeOfMatrix = type;
        this.rows = M;
        this.columns = N;
        data = new Mapping[M][N];
    }
    
    // create M-by-N matrix of 0's
    public SimilarityMatrix(int M, int N, alignType type, String rel) {
    	relation = rel;
    	typeOfMatrix = type;
        this.rows = M;
        this.columns = N;
        data = new Mapping[M][N];
    }


    
    public Mapping get(int i, int j) {  return data[i][j];  }
    
    public void set(int i, int j, Mapping d) {
    
    	data[i][j] = d;
    }
    
    public double getSimilarity(int i, int j){
    	if( data[i][j] == null ) {
    		return 0.00d;
    	}
    	return data[i][j].getSimilarity();
    }
    
    @Deprecated
    public void setSimilarity(int i, int j, double d){
    	if( data[i][j] == null ) {
    		
        	Core core = Core.getInstance();
			Ontology sourceOntology = core.getSourceOntology();
			Ontology targetOntology = core.getTargetOntology();
			ArrayList<Node> sourceList;
			ArrayList<Node> targetList;
			if(typeOfMatrix.equals(alignType.aligningClasses)){
				sourceList = sourceOntology.getClassesList();
				targetList = targetOntology.getClassesList();
			}
			else{
				sourceList = sourceOntology.getPropertiesList();
				targetList = targetOntology.getPropertiesList();
			}
    		
    		
    		data[i][j] = new Mapping( sourceList.get(i), targetList.get(j), d , relation);
    	}
    	else {
    		data[i][j].setSimilarity(d);
    	}
    }
    
    public int getRows() {
    	return rows;
    }
    
    public int getColumns() {
    	return columns;
    }
    
	public String getRelation() { return relation; }
	public alignType getAlignType() { return typeOfMatrix; }
    
    //********************* METHODS ADDED FOR SOME AM CALCULATIONS**********************************************
    /**
     * Return the array of numMaxValues max alignments, THE ARRAY IS ORDERED FROM THE BEST MAX VALUE TO THE WORST
     * this method is used both in selection process but also the AMlocalQuality algorithm
     */
    public Mapping[] getRowMaxValues(int row, int numMaxValues) {
		//remember to check to have numMaxValues lower than matrix columns before
    	Mapping[] maxAlignments = new Mapping[numMaxValues];
    	
		for(int h = 0; h<maxAlignments.length;h++) {
			maxAlignments[h] = new Mapping(-1); //intial max alignments have sim equals to -1, don't put 0 could create problem in the next for
		}
		
		Mapping currentValue;
		Mapping currentMax;
		for(int j = 0; j<getColumns();j++) {
			currentValue = get(row,j);
			if( currentValue == null ) continue;
			//maxAlignments contains the ordered list of max alignments, the first is the best max value
			for(int k = 0;k<maxAlignments.length; k++) {
				currentMax = maxAlignments[k];
				if(currentValue.getSimilarity() >= currentMax.getSimilarity()) { //if so switch the new value with the one in array and then i have to continue scanning the array to put in the switched value
					maxAlignments[k] = currentValue;
					currentValue = currentMax;
				}
			}
		}

		return maxAlignments;
	}

	public double getRowSum(int row) {
		double sum = 0;
		for(int j = 0; j < getColumns(); j++) {
			if( get(row, j ) != null ) {
				sum += get(row, j).getSimilarity();
			}
		}
		return sum;
	}

	public Mapping[] getColMaxValues(int col, int numMaxValues) {
		//remember to check to have numMaxValues lower than matrix rows before
    	Mapping[] maxAlignments = new Mapping[numMaxValues];
    	
		for(int h = 0; h<maxAlignments.length;h++) {
			maxAlignments[h] = new Mapping(-1); //intial max alignments have sim equals to -1
		}
		
		Mapping currentValue;
		Mapping currentMax;
		for(int j = 0; j<getRows();j++) {
			currentValue = get(j, col);
			if( currentValue == null ) continue;
			//maxAlignments contains the ordered list of max alignments, the first is the best max value
			for(int k = 0;k<maxAlignments.length; k++) {
				currentMax = maxAlignments[k];
				if(currentValue.getSimilarity() >= currentMax.getSimilarity()) { //if so switch the new value with the one in array and then i have to continue scanning the array to put in the switched value
					maxAlignments[k] = currentValue;
					currentValue = currentMax;
				}
			}
		}

		return maxAlignments;
	}

	public double getColSum(int col) {
		double sum = 0;
		for(int i = 0; i < getRows(); i++) {
			if( get( i, col ) != null ) {
				sum += get(i, col).getSimilarity();
			}
		}
		return sum;
	}
	
	public boolean isCellEmpty( int i, int j) {
		if( data[i][j] == null ) {
			return true;
		}
		return false;
	}
	
	public Object clone(){
		SimilarityMatrix matrix = new SimilarityMatrix(this);
		return matrix;
	}
    
    
    
    /**GENERAL FUNCTIONS FOR MATRIX NOT NEEDED NOW BUT MAY BE USEFUL IN THE FUTUR*/


    // create and return the transpose of the invoking matrix
    public SimilarityMatrix transpose() {
        SimilarityMatrix A = new SimilarityMatrix(columns, rows, typeOfMatrix, relation);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                A.data[j][i] = this.data[i][j];
        return A;
    }

    // return C = A + B;
    public SimilarityMatrix plus(SimilarityMatrix B) {
        SimilarityMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        SimilarityMatrix C = new SimilarityMatrix(rows, columns, typeOfMatrix, relation);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
            		C.data[i][j].setSimilarity(A.data[i][j].getSimilarity()  + B.data[i][j].getSimilarity());
        			if( C.data[i][j].getSimilarity() > 1.00d ) C.data[i][j].setSimilarity(1.00d);
            }
        return C;
    }


    // return C = A - B
    public SimilarityMatrix minus(SimilarityMatrix B) {
        SimilarityMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        SimilarityMatrix C = new SimilarityMatrix(rows, columns, typeOfMatrix, relation);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
            	C.data[i][j].setSimilarity( A.data[i][j].getSimilarity() - B.data[i][j].getSimilarity());
            	if( C.data[i][j].getSimilarity() < 0.00d ) C.data[i][j].setSimilarity(0.00d);
            }
        return C;
    }

    // does A = B exactly?
    public boolean eq(SimilarityMatrix B) {
        SimilarityMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                if (!(A.data[i][j] == B.data[i][j])) return false;
        return true;
    }

    // EACH CELL MULTIPLIED FOR THE SAME CELL IN THE OTHER MATRIX NOT A REAL MOLTIPLICATION MATRIX
    public SimilarityMatrix times(SimilarityMatrix B) {
        SimilarityMatrix A = this;
        if (A.columns != B.rows) throw new RuntimeException("Illegal matrix dimensions.");
        SimilarityMatrix C = new SimilarityMatrix(A.rows, B.columns, typeOfMatrix, relation);
        for (int i = 0; i < C.rows; i++)
            for (int j = 0; j < C.columns; j++)
                for (int k = 0; k < A.columns; k++)
                	C.data[i][j].setSimilarity( A.data[i][j].getSimilarity()  * B.data[i][j].getSimilarity()); 
        return C;
    }
    
    // print matrix to standard output
    public void show() {
        for (int i = 0; i < rows; i++) {
        	System.out.println("**********************ROW "+i+" ************************");
            for (int j = 0; j < columns; j++) {
            	Mapping a = get(i,j);
            	if(a == null) {
            		System.out.println("Break for null alignment"+a);
            		break;
            	}
            	System.out.println(j+": "+get(i,j));
            }
            	
            System.out.println();
        }
    }

	public double[][] getCopiedSimilarityMatrix(){
		double[][] result = new double[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0 ; j < columns; j++) {
				if(data[i][j] == null){
					result[i][j] = 0;
				}
				else result[i][j] = data[i][j].getSimilarity();
			}
		}
		return result;
	}

	/**
	 * initFromNodeList(ArrayList<Node> sourceList, ArrayList<Node> targetList)
	 * creates an alignmentMatrix from the two node lists we provide
	 * @param sourceList subset of the rows we want to consider in the matrix (each row represents a concept in the source) 
	 * @param targetList subset of the columns we want to consider in the matrix (each column represents a concept in the target)
	 * @author michele 
	 */
	public void initFromNodeList(ArrayList<Node> sourceList, ArrayList<Node> targetList) {
		for(int i = 0; i < sourceList.size(); i++){
			for(int j = 0; j < targetList.size(); j++){
				data[i][j] = new Mapping(sourceList.get(i), targetList.get(j), 0.0);
			}
		}
	}
	
	/**
	 * chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList, boolean considerThreshold, double threshold)
 	 * takes an AlignmentMatrix (can be generalized with a finite matrix with finite values)
	 * and looks for the top n elements (n is min(#row, #column)) within the considered rows and columns.
	 * Takes O(m^2) with m being max(#row, #column)
	 * @param rowsIncludedList subset of the rows we want to consider in the matrix (each row represents a concept in the source) 
	 * @param colsIncludedList subset of the columns we want to consider in the matrix (each column represents a concept in the target)
	 * @param considerThreshold if true, the list will contain only mappings whose similarity value is above the threshold, otherwise it will contain every mapping found
	 * @param threshold the threshold value   
	 * @author michele 
	 */
	public ArrayList<Mapping> chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList, boolean considerThreshold, double threshold) {

		// Creation of the output ArrayList and a copy of the matrix
		int arraySize = Math.min(rowsIncludedList.size(), colsIncludedList.size());
		ArrayList<Mapping> chosenMappings = new ArrayList<Mapping>(arraySize);
		SimilarityMatrix input = new SimilarityMatrix(this);

		ArrayList<Integer> rowsIncluded = rowsIncludedList;
		ArrayList<Integer> colsIncluded = colsIncludedList;
		
		// matrix scan starts here
		while(rowsIncluded.size() > 0 && colsIncluded.size() > 0 ) // until we can look no more at concepts either in the source or in the target ontology
		{
			double simValue = 0;
			Mapping currentChoose = null;
			Integer r = new Integer(0);
			Integer c = new Integer(0);;
			for(int i = 0; i < input.getRows(); i++) {
				for(int j = 0; j < input.getColumns(); j++) {
					
					// within this loop we choose the couple of concepts with the highest similarity value
					if(simValue <= input.getSimilarity(i, j) && rowsIncluded.contains(i) && colsIncluded.contains(j)) {
						
						simValue = input.getSimilarity(i, j);
						currentChoose = input.get(i, j);
						r = i;
						c = j;
					}
				}
			}
			if(considerThreshold && simValue < threshold){
				return chosenMappings;
			}
			else{
				// then we exclude from the matrix the chosen concepts for further computation
				rowsIncluded.remove((Object) r);
				colsIncluded.remove((Object) c);
				// and we add the chosen mapping to the final list
				chosenMappings.add(currentChoose);
			}
			
			/*/ DEBUG INFORMATION
			System.out.println(currentChoose.toString());
			System.out.println(currentChoose.getEntity1().getChildren().toString());
			//System.out.println(r + " " + c + " " + currentChoose.getSimilarity());
			System.out.println();
			//*/	
		}
		return chosenMappings;
	}
	
	/**
	 * chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList):
	 * overridden with two parameters
	 * @param considerThreshold if true, the list will contain only mappings whose similarity value is above the threshold, otherwise it will contain every mapping found
	 * @param threshold the threshold value
	 * @author michele 
	 */
	public ArrayList<Mapping> chooseBestN(boolean considerThreshold, double threshold) {
		return this.chooseBestN(createIntListToN(this.getRows()), createIntListToN(this.getColumns()), considerThreshold, threshold);
	}
	
	/**
	 * chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList):
	 * overridden with two parameters
	 * @param rowsIncludedList subset of the rows we want to consider in the matrix (each row represents a concept in the source) 
	 * @param colsIncludedList subset of the columns we want to consider in the matrix (each column represents a concept in the target)
	 * @author michele 
	 */
	public ArrayList<Mapping> chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList) {
		return this.chooseBestN(rowsIncludedList, colsIncludedList, false, 0.0);
	}
	
	/**
	 * chooseBestN(): overridden with no parameters
	 * @author michele 
	 */
	public ArrayList<Mapping> chooseBestN() {
		return this.chooseBestN(createIntListToN(this.getRows()), createIntListToN(this.getColumns()), false, 0.0);
	}
	
	/**
	 * createIntListToN: creates an ArrayList of n integers from 0 to n-1
	 * useful to create a list for considering all the values of the rows or columns of the alignment matrix
	 * Takes O(n)
	 * @param n size of the ArrayList (n-1 is the last value)
	 * @return arrayList of integer values from 0 to n-1 
	 * @author michele 
	 */
	private static ArrayList<Integer> createIntListToN(int n){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < n; i++){
			list.add(i);
		}
		return list;		
	}
	
}
