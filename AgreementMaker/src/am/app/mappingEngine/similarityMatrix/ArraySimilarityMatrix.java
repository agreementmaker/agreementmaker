package am.app.mappingEngine.similarityMatrix;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class ArraySimilarityMatrix extends SimilarityMatrix implements Serializable {

	private static final long serialVersionUID = 7152244093634002737L;
	
    protected int rows;             // number of rows
    protected int columns;             // number of columns
    protected Mapping[][] data;   // M-by-N array

	// cloning constructor
    public ArraySimilarityMatrix( SimilarityMatrix cloneme ) {
    	
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
    public ArraySimilarityMatrix(Ontology s, Ontology t, alignType type) {
    	relation = Mapping.EQUIVALENCE;
    	typeOfMatrix = type;
    	if(type == alignType.aligningClasses){
            this.rows = s.getClassesList().size();
            this.columns = t.getClassesList().size();
    	}
    	else{
            this.rows = s.getPropertiesList().size();
            this.columns = t.getPropertiesList().size();
    	}
        data = new Mapping[this.rows][this.columns];
    }
    
    // create M-by-N matrix of 0's with equivalence relation
    public ArraySimilarityMatrix(int M, int N, alignType type) {
    	relation = Mapping.EQUIVALENCE;
    	typeOfMatrix = type;
        this.rows = M;
        this.columns = N;
        data = new Mapping[M][N];
    }
     
    // create M-by-N matrix of 0's
    public ArraySimilarityMatrix(int M, int N, alignType type, String rel) {
    	relation = rel;
    	typeOfMatrix = type;
        this.rows = M;
        this.columns = N;
        data = new Mapping[M][N];
    }
    
    public Mapping get(int i, int j) {  return data[i][j];  }
    
    @Override
    public void set(int i, int j, Mapping d) { data[i][j] = d; }
    
    @Override
    public double getSimilarity(int i, int j){
    	if( data[i][j] == null ) {
    		return 0.00d;
    	}
    	return data[i][j].getSimilarity();
    }
    
    @Override
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
	@Override
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

	@Override
	public double getRowSum(int row) {
		double sum = 0;
		for(int j = 0; j < getColumns(); j++) {
			if( get(row, j ) != null ) {
				sum += get(row, j).getSimilarity();
			}
		}
		return sum;
	}

	@Override
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

	@Override
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
	
	@Override
	public SimilarityMatrix clone(){
		SimilarityMatrix matrix = new ArraySimilarityMatrix(this);
		return matrix;
	}
    
	@Override
	public Vector<Mapping> toMappingArray(){
		Vector<Mapping> mappingArray = new Vector<Mapping>();
		for(int i = 0; i < getRows(); i++){
			for(int j = 0; j < getColumns(); j++){
				if(this.get(i, j) != null){
					mappingArray.add(this.get(i, j));
				}
			}
		}
		return mappingArray;
    }
	
	public Vector<Mapping> toMappingArray(FileWriter fw, int round){
		Vector<Mapping> mappingArray = new Vector<Mapping>(getRows() * getColumns());
		for(int i = 0; i < getRows(); i++){
			for(int j = 0; j < getColumns(); j++){
				if(this.get(i, j) != null){
					mappingArray.add(this.get(i, j));
					if(round == 1)
					try {
						fw.append(this.get(i, j).toString() + "\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return mappingArray;
    }
    
	@Override
	public Vector<Double> toSimilarityArray(Vector<Mapping> mapsArray){
		Vector<Double> similarityArray = new Vector<Double>();
		for(int i = 0; i < mapsArray.size(); i++){
			similarityArray.add(mapsArray.get(i).getSimilarity());
		}
		return similarityArray;
    }
    
    /**GENERAL FUNCTIONS FOR MATRIX NOT NEEDED NOW BUT MAY BE USEFUL IN THE FUTUR*/


    // create and return the transpose of the invoking matrix
    public SimilarityMatrix transpose() {
        SimilarityMatrix A = new ArraySimilarityMatrix(columns, rows, typeOfMatrix, relation);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                A.set(i,j, this.data[i][j]);
        return A;
    }

    // return C = A + B;
    public SimilarityMatrix plus(SimilarityMatrix B) {
        SimilarityMatrix A = this;
        if (B.getRows() != A.getRows() || B.getColumns() != A.getColumns()) throw new RuntimeException("Illegal matrix dimensions.");
        SimilarityMatrix C = new ArraySimilarityMatrix(rows, columns, typeOfMatrix, relation);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
            	Mapping m1 = A.get(i,j);
            	Mapping m2 = B.get(i,j);
            	if( m1 == null && m2 == null ) continue;
            	else if( m1 == null ) C.set(i, j, new Mapping( m2.getEntity1(), m2.getEntity2(), m2.getSimilarity() ));
            	else if( m2 == null ) C.set(i, j, new Mapping( m1.getEntity1(), m1.getEntity2(), m1.getSimilarity() ));
            	else {
            		double newSim = Math.max(1.00d, A.getSimilarity(i,j)  + B.getSimilarity(i,j) );
            		C.set(i, j, new Mapping( m1.getEntity1(), m1.getEntity2(), newSim ));
            	}
            }
        return C;
    }


    // return C = A - B
    public SimilarityMatrix minus(SimilarityMatrix B) {
        SimilarityMatrix A = this;
        if (B.getRows() != A.getRows() || B.getColumns() != A.getColumns()) throw new RuntimeException("Illegal matrix dimensions.");
        SimilarityMatrix C = new ArraySimilarityMatrix(rows, columns, typeOfMatrix, relation);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
            	Mapping m1 = A.get(i,j);
            	Mapping m2 = B.get(i,j);
            	if( m1 == null && m2 == null ) continue;
            	else if( m1 == null ) C.set(i, j, new Mapping( m2.getEntity1(), m2.getEntity2(), m2.getSimilarity() ));
            	else if( m2 == null ) C.set(i, j, new Mapping( m1.getEntity1(), m1.getEntity2(), m1.getSimilarity() ));
            	else {
            		double newSim = Math.min(1.00d, A.getSimilarity(i,j) - B.getSimilarity(i,j) );
            		C.set(i, j, new Mapping( m1.getEntity1(), m1.getEntity2(), newSim ));
            	}
            }
        return C;
    }

    // does A = B exactly?
    public boolean eq(SimilarityMatrix B) {
        SimilarityMatrix A = this;
        if (B.getRows() != A.getRows() || B.getColumns() != A.getColumns()) throw new RuntimeException("Illegal matrix dimensions.");
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                if (!(A.get(i,j) == B.get(i,j))) return false;
        return true;
    }

    // EACH CELL MULTIPLIED FOR THE SAME CELL IN THE OTHER MATRIX NOT A REAL MOLTIPLICATION MATRIX
    public SimilarityMatrix times(SimilarityMatrix B) {
        SimilarityMatrix A = this;
        if (B.getRows() != A.getRows() || B.getColumns() != A.getColumns()) throw new RuntimeException("Illegal matrix dimensions.");
        SimilarityMatrix C = new ArraySimilarityMatrix(A.getRows(), B.getColumns(), typeOfMatrix, relation);
        for (int i = 0; i < C.getRows(); i++)
            for (int j = 0; j < C.getColumns(); j++)
                for (int k = 0; k < A.getColumns(); k++) {
	                Mapping m1 = A.get(i,j);
	            	Mapping m2 = B.get(i,j);
	            	if( m1 == null && m2 == null ) continue;
	            	else if( m1 == null ) C.set(i, j, new Mapping( m2.getEntity1(), m2.getEntity2(), 0.0d ));
	            	else if( m2 == null ) C.set(i, j, new Mapping( m1.getEntity1(), m1.getEntity2(), 0.0d ));
	            	else {
	            		double newSim = Math.max(1.00d, A.getSimilarity(i,j) * B.getSimilarity(i,j) );
	            		C.set(i, j, new Mapping( m1.getEntity1(), m1.getEntity2(), newSim ));
	            	}
                }
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

    /* What does this do? What is it used by? */
    @Override
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
	@Override
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
	@Override
	public ArrayList<Mapping> chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList, boolean considerThreshold, double threshold) {

		// Creation of the output ArrayList and a copy of the matrix
		int arraySize = Math.min(rowsIncludedList.size(), colsIncludedList.size());
		ArrayList<Mapping> chosenMappings = new ArrayList<Mapping>(arraySize);
		//SimilarityMatrix input = new ArraySimilarityMatrix(this);

		ArrayList<Integer> rowsIncluded = rowsIncludedList;
		ArrayList<Integer> colsIncluded = colsIncludedList;
		
		// matrix scan starts here
		while(rowsIncluded.size() > 0 && colsIncluded.size() > 0 ) // until we can look no more at concepts either in the source or in the target ontology
		{
			double simValue = 0;
			Mapping currentChoose = null;
			Integer r = new Integer(0);
			Integer c = new Integer(0);;
			for(int i = 0; i < getRows(); i++) {
				for(int j = 0; j < getColumns(); j++) {
					
					// within this loop we choose the couple of concepts with the highest similarity value
					if(simValue <= getSimilarity(i, j) && rowsIncluded.contains(i) && colsIncluded.contains(j)) {
						
						simValue = getSimilarity(i, j);
						currentChoose = get(i, j);
						r = i;
						c = j;
					}
				}
			}
			if(considerThreshold && simValue < threshold){
				return chosenMappings;
			}
			else if ( currentChoose != null ) {
				// we add the chosen mapping to the final list
				chosenMappings.add(currentChoose);
			}
			// then we exclude from the matrix the chosen concepts for further computation
			rowsIncluded.remove((Object) r);
			colsIncluded.remove((Object) c);

			
			// DEBUG INFORMATION
			//System.out.println(currentChoose.toString());
			//System.out.println(currentChoose.getEntity1().getChildren().toString());
			//System.out.println(r + " " + c + " " + currentChoose.getSimilarity());
			//System.out.println();
			//	
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
	@Override
	public ArrayList<Mapping> chooseBestN(boolean considerThreshold, double threshold) {
		return this.chooseBestN(Utility.createIntListToN(this.getRows()), Utility.createIntListToN(this.getColumns()), considerThreshold, threshold);
	}
	
	/**
	 * chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList):
	 * overridden with two parameters
	 * @param rowsIncludedList subset of the rows we want to consider in the matrix (each row represents a concept in the source) 
	 * @param colsIncludedList subset of the columns we want to consider in the matrix (each column represents a concept in the target)
	 * @author michele 
	 */
	@Override
	public ArrayList<Mapping> chooseBestN(ArrayList<Integer> rowsIncludedList, ArrayList<Integer> colsIncludedList) {
		return this.chooseBestN(rowsIncludedList, colsIncludedList, false, 0.0);
	}
	
	/**
	 * chooseBestN(): overridden with no parameters
	 * @author michele 
	 */
	@Override
	public ArrayList<Mapping> chooseBestN() {
		return this.chooseBestN(Utility.createIntListToN(this.getRows()), Utility.createIntListToN(this.getColumns()), false, 0.0);
	}
	
	/**
	 * fillMatrix: fill the matrix with one value
	 * @param val value to fill the matrix with
	 * @author michele 
	 */
//	@Override
	public void fillMatrix(double val, ArrayList<Node> sList, ArrayList<Node> tList){
		// create M-by-N matrix of the selected value
		assert (val >= 0 && val <= 1);
	    for(int i = 0; i < this.getRows(); i++){
	    	for(int j = 0; j < this.getColumns(); j++){
	    		if(get(i, j) == null){
	    			set(i, j, new Mapping(sList.get(i), tList.get(j), val));
	    		}
	    		else {
	    			Mapping updatingMapping = this.get(i, j);
	    			updatingMapping.setSimilarity(val);
	    			set(i, j, updatingMapping);
	    		}
	    	}
	    }
	}
	
	// TODO: Make the max value update when populating the matrix.
	@Override
	public double getMaxValue() {
		double max = 0.0;
		
		for( int i = 0; i < data.length; i++ ) {
			for( int j = 0; j < data[i].length; j++ ) {
				if( data[i][j] != null && data[i][j].getSimilarity() > max ) { max = data[i][j].getSimilarity(); }
			}
		}
		
		return max;
	}
	
	@Override
	public Mapping[] getTopK( int k ) {
		Mapping[] topK = new Mapping[k];
		
		for( int i = 0; i < k; i++ ) { topK[i] = null; } // clear the matrix
		
		for( int i = 0; i < data.length; i++ ) {
			for( int j = 0; j < data[i].length; j++ ) {
				if( data[i][j] == null ) continue; 
				if( topK[k-1] == null || topK[k-1].getSimilarity() < data[i][j].getSimilarity() ) {
					topK[k-1] = data[i][j];
					// the bubble rises up the array
					for( int l = k-1; l > 0; l-- ) {
						if( topK[l-1] == null || topK[l-1].getSimilarity() < topK[l].getSimilarity() ) {
							Mapping temp = topK[l-1];
							topK[l-1] = topK[l];
							topK[l] = temp;
						} else {
							break;
						}
					}
				}
				
			}
		}
		
		return topK;
	}
	
	@Override
	public Mapping[] getTopK( int k, boolean[][] filteredCells ) {
		Mapping[] topK = new Mapping[k];
		
		for( int i = 0; i < k; i++ ) { topK[i] = null; } // clear the matrix
		
		for( int i = 0; i < data.length; i++ ) {
			for( int j = 0; j < data[i].length; j++) {
				if( filteredCells[i][j] ) continue;  // this cell is filtered, go on to the next one
				if( data[i][j] == null ) continue; 
				if( topK[k-1] == null || topK[k-1].getSimilarity() < data[i][j].getSimilarity() ) {
					topK[k-1] = data[i][j];
					// the bubble rises up the array
					for( int l = k-1; l > 0; l-- ) {
						if( topK[l-1] == null || topK[l-1].getSimilarity() < topK[l].getSimilarity() ) {
							Mapping temp = topK[l-1];
							topK[l-1] = topK[l];
							topK[l] = temp;
						} else {
							break;
						}
					}
				}
				
			}
		}
		
		return topK;
	}
	
	@Override
	public int countNonNullValues() {
		int count = 0;
		for(int i = 0; i < this.getRows(); i++){
			for(int j = 0; j < this.getColumns(); j++){
				if(this.get(i, j) != null){
					count++;
				}
			}
		}
		return count;
	}
	@Override
	public SimilarityMatrix toArraySimilarityMatrix() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
