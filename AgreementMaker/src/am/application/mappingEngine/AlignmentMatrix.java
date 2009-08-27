package am.application.mappingEngine;

public class AlignmentMatrix {
    private final int rows;             // number of rows
    private final int columns;             // number of columns
    private final Alignment[][] data;   // M-by-N array

    // create M-by-N matrix of 0's
    public AlignmentMatrix(int M, int N) {
        this.rows = M;
        this.columns = N;
        data = new Alignment[M][N];
    }

    // create matrix based on 2d array
    public AlignmentMatrix(Alignment[][] data) {
        rows = data.length;
        columns = data[0].length;
        this.data = new Alignment[rows][columns];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                    this.data[i][j] = data[i][j];
    }

    // copy constructor not used right now
    private AlignmentMatrix(AlignmentMatrix A) { this(A.data); }
    
    public Alignment get(int i, int j) {
    	return data[i][j];
    }
    
    public void set(int i, int j, Alignment d) {
    	data[i][j] = d;
    }
    
    public int getRows() {
    	return rows;
    }
    
    public int getColumns() {
    	return columns;
    }
   
    public Object clone() {
    	
    		AlignmentMatrix m = this;
    		AlignmentMatrix n = new AlignmentMatrix(m.getRows(), m.getColumns());
    		for(int i=0; i< m.getRows(); i++) {
    			for(int j = 0; j < m.getColumns(); j++) {
    				n.set(i,j,m.get(i,j));
    			}
    		}
    		return n;
    }
    
    
    /**GENERAL FUNCTIONS FOR MATRIX NOT NEEDED NOW BUT MAY BE USEFUL IN THE FUTUR*/
    
    // swap rows i and j
    private void swap(int i, int j) {
    	Alignment[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // create and return the transpose of the invoking matrix
    public AlignmentMatrix transpose() {
        AlignmentMatrix A = new AlignmentMatrix(columns, rows);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                A.data[j][i] = this.data[i][j];
        return A;
    }

    // return C = A + B;
    public AlignmentMatrix plus(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        AlignmentMatrix C = new AlignmentMatrix(rows, columns);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
            	if( A.data[i][j] != null && B.data[i][j] != null )
            		C.data[i][j].setSimilarity(A.data[i][j].getSimilarity()  + B.data[i][j].getSimilarity()) ;
            	else
            		C.data[i][j] = null;
        return C;
    }


    // return C = A - B
    public AlignmentMatrix minus(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        AlignmentMatrix C = new AlignmentMatrix(rows, columns);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
            	if( A.data[i][j] != null && B.data[i][j] != null )
            		C.data[i][j].setSimilarity(A.data[i][j].getSimilarity()  - B.data[i][j].getSimilarity()) ;
            	else
            		C.data[i][j] = null;
        return C;
    }

    // does A = B exactly?
    public boolean eq(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                if (!A.data[i][j].equals(B.data[i][j])) return false;
        return true;
    }

    // EACH CELL MULTIPLIED FOR THE SAME CELL IN THE OTHER MATRIX NOT A REAL MOLTIPLICATION MATRIX
    public AlignmentMatrix times(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (A.columns != B.rows) throw new RuntimeException("Illegal matrix dimensions.");
        AlignmentMatrix C = new AlignmentMatrix(A.rows, B.columns);
        for (int i = 0; i < C.rows; i++)
            for (int j = 0; j < C.columns; j++)
                for (int k = 0; k < A.columns; k++)
                	if( A.data[i][j] != null && B.data[i][j] != null )
                		C.data[i][j].setSimilarity(A.data[i][j].getSimilarity()  * B.data[i][j].getSimilarity()) ;
                	else
                		C.data[i][j] = null;
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

	public double[][] getSimilarityMatrix(){
		double[][] result = new double[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0 ; j < columns; j++) {
				if( data[i][j] != null ) result[i][j] = data[i][j].getSimilarity();
				else result[i][j] = 0;
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
}
