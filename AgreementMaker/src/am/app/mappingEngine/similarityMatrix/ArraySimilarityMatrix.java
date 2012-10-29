package am.app.mappingEngine.similarityMatrix;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import am.AMException;
import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class ArraySimilarityMatrix extends SimilarityMatrix {

	private static final long serialVersionUID = 7152244093634002737L;
	
	protected List<Node> rowNodes;
	protected List<Node> colNodes;

	protected class SimRel implements Serializable{
		private static final long serialVersionUID = 3340618624390143439L;
		
		public double similarity;
		public MappingRelation relation;
		public String provenance;
		
		SimRel(double similarity, MappingRelation relation) {
			this.similarity = similarity;
			this.relation = relation;
		}
		
		SimRel(double similarity, MappingRelation relation, String provenance) {
			this.similarity = similarity;
			this.relation = relation;
			this.provenance = provenance;
		}
	}
	
    protected int rows;             // number of rows
    protected int columns;             // number of columns
    protected SimRel[][] data;   // M-by-N array

	// cloning constructor
    public ArraySimilarityMatrix( SimilarityMatrix cloneme ) throws AMException {
    		super(cloneme.getSourceOntology(), cloneme.getTargetOntology(), cloneme.getAlignType());
	    	//relation = cloneme.getRelation();
	    	
	    	rows = cloneme.getRows();
	    	columns = cloneme.getColumns();
	    	    		
	    	if( typeOfMatrix == alignType.aligningClasses ) { 
	    		rowNodes = sourceOntology.getClassesList();
	    		colNodes = targetOntology.getClassesList();
	    	} else if ( typeOfMatrix == alignType.aligningProperties ) {
	    		rowNodes = sourceOntology.getPropertiesList();
	    		colNodes = targetOntology.getPropertiesList();
	    	} else {
	    		System.err.println("Invalid typeOfMatrix: " + typeOfMatrix + ".  Assuming aligningClasses.");
	    		rowNodes = sourceOntology.getClassesList();
	    		colNodes = targetOntology.getClassesList();
	    	}
	    	
	    	data = new SimRel[rows][columns];
	    	
	   		for(int i=0; i< cloneme.getRows(); i++) {
	   			for(int j = 0; j < cloneme.getColumns(); j++) {
	   				Mapping a = cloneme.get(i, j);
	   				if( a != null ) {
	   					data[i][j] = new SimRel(a.getSimilarity(), a.getRelation());
	   				} else data[i][j] = null;
	   			}
	   		}
    	
    }
    
 // create M-by-N matrix of 0's with equivalence relation
    public ArraySimilarityMatrix(Ontology source, Ontology target, alignType type) {
    	super(source,target,type);
    	//relation = MappingRelation.EQUIVALENCE;
   			
    	if( typeOfMatrix == alignType.aligningClasses ) {
    		this.rows = source.getClassesList().size();
            this.columns = target.getClassesList().size();
    		rowNodes = source.getClassesList();
    		colNodes = target.getClassesList();
    	} else if ( typeOfMatrix == alignType.aligningProperties ) {
    		rowNodes = source.getPropertiesList();
    		colNodes = target.getPropertiesList();
    	} else {
    		System.err.println("Invalid typeOfMatrix: " + typeOfMatrix + ".  Assuming aligningClasses.");
    		rowNodes = source.getClassesList();
    		colNodes = target.getClassesList();
    	}
    	
    	this.rows = rowNodes.size();
        this.columns = colNodes.size();
    	
        data = new SimRel[this.rows][this.columns];
    }
        
    // create M-by-N matrix of 0's with equivalence relation
    /*public ArraySimilarityMatrix(int M, int N, alignType type) throws AMException {
    	//relation = MappingRelation.EQUIVALENCE;
    	typeOfMatrix = type;
        this.rows = M;
        this.columns = N;
        
        Ontology source = Core.getInstance().getOntologyByID(sourceOntologyID);
		if( source == null ) throw new AMException("No ontology found with ID = " + sourceOntologyID);
    	
		Ontology target = Core.getInstance().getOntologyByID(targetOntologyID);
		if( target == null ) throw new AMException("No ontology found with ID = " + targetOntologyID);
		
    	if( typeOfMatrix == alignType.aligningClasses ) { 
    		rowNodes = source.getClassesList();
    		colNodes = target.getClassesList();
    	} else if ( typeOfMatrix == alignType.aligningProperties ) {
    		rowNodes = source.getPropertiesList();
    		colNodes = target.getPropertiesList();
    	} else {
    		System.err.println("Invalid typeOfMatrix: " + typeOfMatrix + ".  Assuming aligningClasses.");
    		rowNodes = source.getClassesList();
    		colNodes = target.getClassesList();
    	}
        
        data = new SimRel[M][N];
    }*/
     
    // create M-by-N matrix of 0's
    /*public ArraySimilarityMatrix(int M, int N, alignType type, MappingRelation rel) {
    	relation = rel;
    	typeOfMatrix = type;
        this.rows = M;
        this.columns = N;
        
        data = new SimRel[M][N];
    }*/
    
    //junit constructor
   //public ArraySimilarityMatrix(){}
    //public void initData(int m, int n){data=new Mapping[m][n];}
    
    @Override
    public Mapping get(int i, int j) {
    	if( data[i][j] == null ) return null;  
    	return new Mapping( rowNodes.get(i), colNodes.get(j), data[i][j].similarity, data[i][j].relation, data[i][j].provenance );
    	//return data[i][j];
    }
    
    @Override
    public void set(int i, int j, Mapping d) { 
    	if( d != null ) {
	    	//if( rowNodes[i] == null ) rowNodes[i] = d.getEntity1();
	    	//if( colNodes[j] == null ) colNodes[j] = d.getEntity2();
	    	data[i][j] = new SimRel(d.getSimilarity(), d.getRelation(), d.getProvenance()); 
    	} else {
    		data[i][j] = null;
    	}
    }
    
    @Override
    public double getSimilarity(int i, int j){
    	if( data[i][j] == null ) { return 0.00d; }
    	return data[i][j].similarity;
    }
    
    /*@Override
    public void setSimilarity(int i, int j, double d){
    	if( data[i][j] == null ) {
    		
        	Core core = Core.getInstance();
			Ontology sourceOntology = core.getSourceOntology();
			Ontology targetOntology = core.getTargetOntology();
			List<Node> sourceList;
			List<Node> targetList;
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
    }*/
    
    public int getRows() {
    	return rows;
    }
    
    public int getColumns() {
    	return columns;
    }
    
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
	public SimilarityMatrix clone() {
		try {
			SimilarityMatrix matrix = new ArraySimilarityMatrix(this);
			return matrix;
		} catch( AMException e ) {
			e.printStackTrace();
			return null;
		}
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
	public List<Double> toSimilarityArray(List<Mapping> mapsArray){
		Vector<Double> similarityArray = new Vector<Double>();
		for(int i = 0; i < mapsArray.size(); i++){
			similarityArray.add(mapsArray.get(i).getSimilarity());
		}
		return similarityArray;
    }
    
    /**GENERAL FUNCTIONS FOR MATRIX NOT NEEDED NOW BUT MAY BE USEFUL IN THE FUTUR*/


    // create and return the transpose of the invoking matrix
    public SimilarityMatrix transpose() {
    	
        SimilarityMatrix A = new ArraySimilarityMatrix(targetOntology, sourceOntology, typeOfMatrix);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                A.set(i,j, new Mapping(rowNodes.get(i),colNodes.get(j),data[i][j].similarity,data[i][j].relation));
        return A;
    }

    // return C = A + B;
    public SimilarityMatrix plus(SimilarityMatrix B) {
        SimilarityMatrix A = this;
        if (B.getRows() != A.getRows() || B.getColumns() != A.getColumns()) throw new RuntimeException("Illegal matrix dimensions.");
        SimilarityMatrix C = new ArraySimilarityMatrix(sourceOntology, targetOntology, typeOfMatrix);
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
        SimilarityMatrix C = new ArraySimilarityMatrix(sourceOntology, targetOntology, typeOfMatrix);
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
        SimilarityMatrix C = new ArraySimilarityMatrix(A.getSourceOntology(), B.getTargetOntology(), typeOfMatrix);
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
				else result[i][j] = data[i][j].similarity;
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
	public void initFromNodeList(List<Node> sourceList, List<Node> targetList) {
		rowNodes = new ArrayList<Node>();
		for( int i = 0; i < sourceList.size(); i++ ) {
			rowNodes.add(sourceList.get(i));
		}
		
		for( int j = 0; j < targetList.size(); j++ ) {
			colNodes.add(targetList.get(j));
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
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList, List<Integer> colsIncludedList, boolean considerThreshold, double threshold) {

		// FIXME: Fix this method.
		System.err.println("Do not use chooseBestN from ArraySimilarityMatrix, it must be unit tested.");
		
		// Creation of the output ArrayList and a copy of the matrix
		int arraySize = Math.min(rowsIncludedList.size(), colsIncludedList.size());
		ArrayList<Mapping> chosenMappings = new ArrayList<Mapping>(arraySize);
		//SimilarityMatrix input = new ArraySimilarityMatrix(this);

		List<Integer> rowsIncluded = rowsIncludedList;
		List<Integer> colsIncluded = colsIncludedList;
		
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
	public List<Mapping> chooseBestN(boolean considerThreshold, double threshold) {
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
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList, List<Integer> colsIncludedList) {
		return this.chooseBestN(rowsIncludedList, colsIncludedList, false, 0.0);
	}
	
	/**
	 * chooseBestN(): overridden with no parameters
	 * @author michele 
	 */
	@Override
	public List<Mapping> chooseBestN() {
		return this.chooseBestN(Utility.createIntListToN(this.getRows()), Utility.createIntListToN(this.getColumns()), false, 0.0);
	}
	
	/**
	 * fillMatrix: fill the matrix with one value
	 * @param val value to fill the matrix with
	 * @author michele 
	 */
//	@Override
	public void fillMatrix(double val, List<Node> sList, List<Node> tList){
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
				if( data[i][j] != null && data[i][j].similarity > max ) { max = data[i][j].similarity; }
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
				if( topK[k-1] == null || topK[k-1].getSimilarity() < data[i][j].similarity ) {
					topK[k-1] = new Mapping(rowNodes.get(i), colNodes.get(j), data[i][j].similarity, data[i][j].relation);
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
				if( topK[k-1] == null || topK[k-1].getSimilarity() < data[i][j].similarity ) {
					topK[k-1] = new Mapping(rowNodes.get(i), colNodes.get(j), data[i][j].similarity, data[i][j].relation);
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
	/*
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
	*/
	@Override
	public SimilarityMatrix toArraySimilarityMatrix() {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	//junit test
	@Test public void insertAndGet16000000() {
		long start=System.currentTimeMillis();
		for(int j=0;j<500;j++){
			Random r=new Random();
			ArrayList<Integer> xVals=new ArrayList<Integer>();
			ArrayList<Integer> yVals=new ArrayList<Integer>();
			
			for(int i=0;i<4000;i++){
				xVals.add(new Integer(i));
				yVals.add(new Integer(i));
			}
			
			ArraySimilarityMatrix m=new ArraySimilarityMatrix();
			m.initData(4000, 4000);
			int[] x=new int[16000000];
			int[] y=new int[16000000];
			double[] z=new double[16000000];
			//insert 500 entries in the matrix and
			for(int i=0;i<4000;i++){
				int x1=r.nextInt(xVals.size());
				int y1=r.nextInt(yVals.size());
				double z1=r.nextDouble();
				
				//System.out.println("inserting i="+i+" : ("+xVals.get(x1)+","+yVals.get(y1)+","+z1+")");
				m.set(xVals.get(x1), yVals.get(y1), new Mapping(z1));
				x[i]=xVals.get(x1);
				y[i]=yVals.get(y1);
				z[i]=z1;
				
				xVals.remove(x1);
				yVals.remove(y1);
			}
			/*
			//check the entries
			for(int i=0;i<4000;i++){
				//System.out.println("getting i="+i+" : ("+x[i]+","+y[i]+")");
				Mapping temp=m.get(x[i], y[i]);
				//System.out.println(x[i]+","+ y[i]);
				//System.out.println(temp);
				assertTrue(temp.getSimilarity()==z[i]);
			}
			
		}
		long end=System.currentTimeMillis();
		long total=end-start;
		System.out.println("total time (in seconds): "+(total/100)/500);
	}
	*/
	
	/**
	 * This method required to restore the ArraySimilarityMatrix
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		sourceOntology = Core.getInstance().getSourceOntology();
		targetOntology = Core.getInstance().getTargetOntology();
		
		if( typeOfMatrix == alignType.aligningClasses ) {
    		this.rows = sourceOntology.getClassesList().size();
            this.columns = targetOntology.getClassesList().size();
    		rowNodes = sourceOntology.getClassesList();
    		colNodes = targetOntology.getClassesList();
    	} else if ( typeOfMatrix == alignType.aligningProperties ) {
    		rowNodes = sourceOntology.getPropertiesList();
    		colNodes = targetOntology.getPropertiesList();
    	} else {
    		System.err.println("Invalid typeOfMatrix: " + typeOfMatrix + ".  Assuming aligningClasses.");
    		rowNodes = sourceOntology.getClassesList();
    		colNodes = targetOntology.getClassesList();
    	}
		
	}
}
