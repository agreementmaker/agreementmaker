package am.app.mappingEngine;

import java.util.ArrayList;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class AlignmentMatrix {
	
	//not used at the moment. At the moment indetermined similarity are set with 0.
	//if we want to start using it is important to keep it similar to 0, to allow compatibility with non-updated methods.
	final static double INDETERMINED = Double.MIN_NORMAL;
	
	protected String relation;
	protected alignType typeOfMatrix;
    protected final int rows;             // number of rows
    protected final int columns;             // number of columns
    protected final Alignment[][] data;   // M-by-N array

    
    // cloning constructor
    public AlignmentMatrix( AlignmentMatrix cloneme ) {
    	relation = cloneme.getRelation();
    	typeOfMatrix = cloneme.getAlignType();
    	
    	rows = cloneme.getRows();
    	columns = cloneme.getColumns();
    	
    	data = new Alignment[rows][columns];
    	
   		for(int i=0; i< cloneme.getRows(); i++) {
   			for(int j = 0; j < cloneme.getColumns(); j++) {
   				setSimilarity(i,j, cloneme.getSimilarity(i,j));
   			}
   		}
   	
    }
    
    // create M-by-N matrix of 0's
    public AlignmentMatrix(int M, int N, alignType type) {
    	relation = Alignment.EQUIVALENCE;
    	typeOfMatrix = type;
        this.rows = M;
        this.columns = N;
        data = new Alignment[M][N];
    }
    
    // create M-by-N matrix of 0's
    public AlignmentMatrix(int M, int N, alignType type, String rel) {
    	relation = rel;
    	typeOfMatrix = type;
        this.rows = M;
        this.columns = N;
        data = new Alignment[M][N];
    }

    // create matrix based on 2d array
    public AlignmentMatrix(double[][] data, alignType type) {  // TODO: Does this really have a use? No source-target relationships are created. (cos,10-29-09)
    	relation = Alignment.EQUIVALENCE;
    	typeOfMatrix = type;
        rows = data.length;
        columns = data[0].length;
        this.data = new Alignment[rows][columns];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                    this.data[i][j] = new Alignment(data[i][j]);
    }
    
    public Alignment get(int i, int j) {  // TODO: This function does not return null.  It should return null. (cos,10-29-09)
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
    	
    	if( data[i][j] == null )
    		return new Alignment(sourceList.get(i), targetList.get(j), 0.00d, relation);
    	else 
    		return data[i][j];
    }
    
    public void set(int i, int j, Alignment d) {
    	//data[d.getEntity1().getIndex()][d.getEntity2().getIndex()] = d.getSimilarity();
    	data[i][j] = d;
    }
    
    public double getSimilarity(int i, int j){
    	if( data[i][j] == null ) {
    		return 0.00d;
    	}
    	return data[i][j].getSimilarity();
    }
    
    public void setSimilarity(int i, int j, double d){
    	if( data[i][j] == null ) {
    		data[i][j] = new Alignment( d );
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
   
    public Object clone() {  // TODO: Make this work the right way (create new Alignment objects)
    	
    		AlignmentMatrix m = this;
    		AlignmentMatrix n = new AlignmentMatrix(m.getRows(), m.getColumns(), m.typeOfMatrix, m.relation);
    		for(int i=0; i< m.getRows(); i++) {
    			for(int j = 0; j < m.getColumns(); j++) {
    				n.setSimilarity(i,j,m.getSimilarity(i,j));
    			}
    		}
    		return n;
    }
    
    
    /**GENERAL FUNCTIONS FOR MATRIX NOT NEEDED NOW BUT MAY BE USEFUL IN THE FUTUR*/
    
    // swap rows i and j
    @SuppressWarnings("unused")
	private void swap(int i, int j) {  // TODO: This function makes no sense.
    	Alignment[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // create and return the transpose of the invoking matrix
    public AlignmentMatrix transpose() {
        AlignmentMatrix A = new AlignmentMatrix(columns, rows, typeOfMatrix, relation);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                A.data[j][i] = this.data[i][j];
        return A;
    }

    // return C = A + B;
    public AlignmentMatrix plus(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        AlignmentMatrix C = new AlignmentMatrix(rows, columns, typeOfMatrix, relation);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
            		C.data[i][j].setSimilarity(A.data[i][j].getSimilarity()  + B.data[i][j].getSimilarity());
        			if( C.data[i][j].getSimilarity() > 1.00d ) C.data[i][j].setSimilarity(1.00d);
            }
        return C;
    }


    // return C = A - B
    public AlignmentMatrix minus(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        AlignmentMatrix C = new AlignmentMatrix(rows, columns, typeOfMatrix, relation);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
            	C.data[i][j].setSimilarity( A.data[i][j].getSimilarity() - B.data[i][j].getSimilarity());
            	if( C.data[i][j].getSimilarity() < 0.00d ) C.data[i][j].setSimilarity(0.00d);
            }
        return C;
    }

    // does A = B exactly?
    public boolean eq(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                if (!(A.data[i][j] == B.data[i][j])) return false;
        return true;
    }

    // EACH CELL MULTIPLIED FOR THE SAME CELL IN THE OTHER MATRIX NOT A REAL MOLTIPLICATION MATRIX
    public AlignmentMatrix times(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (A.columns != B.rows) throw new RuntimeException("Illegal matrix dimensions.");
        AlignmentMatrix C = new AlignmentMatrix(A.rows, B.columns, typeOfMatrix, relation);
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
            	Alignment a = get(i,j);
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
				result[i][j] = data[i][j].getSimilarity();
			}
		}
		return result;
	}
    
    //********************* METHODS ADDED FOR SOME AM CALCULATIONS**********************************************
    /**
     * Return the array of numMaxValues max alignments, THE ARRAY IS ORDERED FROM THE BEST MAX VALUE TO THE WORST
     * this method is used both in selection process but also the AMlocalQuality algorithm
     */
    public Alignment[] getRowMaxValues(int row, int numMaxValues) {
		//remember to check to have numMaxValues lower than matrix columns before
    	Alignment[] maxAlignments = new Alignment[numMaxValues];
    	
		for(int h = 0; h<maxAlignments.length;h++) {
			maxAlignments[h] = new Alignment(-1); //intial max alignments have sim equals to -1, don't put 0 could create problem in the next for
		}
		
		Alignment currentValue;
		Alignment currentMax;
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

	public Alignment[] getColMaxValues(int col, int numMaxValues) {
		//remember to check to have numMaxValues lower than matrix rows before
    	Alignment[] maxAlignments = new Alignment[numMaxValues];
    	
		for(int h = 0; h<maxAlignments.length;h++) {
			maxAlignments[h] = new Alignment(-1); //intial max alignments have sim equals to -1
		}
		
		Alignment currentValue;
		Alignment currentMax;
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
	
	
	public String getRelation() { return relation; }
	public alignType getAlignType() { return typeOfMatrix; }
	
	public boolean isCellEmpty( int i, int j) {
		if( data[i][j] == null ) {
			return true;
		}
		return false;
	}
	
}
