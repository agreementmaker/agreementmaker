package agreementMaker.application.mappingEngine;

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

    // return C = A + B
    public AlignmentMatrix plus(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        AlignmentMatrix C = new AlignmentMatrix(rows, columns);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                C.data[i][j].setSimilarity(A.data[i][j].getSimilarity()  + B.data[i][j].getSimilarity()) ;
        return C;
    }


    // return C = A - B
    public AlignmentMatrix minus(AlignmentMatrix B) {
        AlignmentMatrix A = this;
        if (B.rows != A.rows || B.columns != A.columns) throw new RuntimeException("Illegal matrix dimensions.");
        AlignmentMatrix C = new AlignmentMatrix(rows, columns);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
            	 C.data[i][j].setSimilarity(A.data[i][j].getSimilarity()  - B.data[i][j].getSimilarity()) ;
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
                	C.data[i][j].setSimilarity(A.data[i][j].getSimilarity()  * B.data[i][j].getSimilarity()) ;
        return C;
    }

    // print matrix to standard output
    public void show() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) 
                System.out.printf("%9.4f ", data[i][j]);
            System.out.println();
        }
    }
}
