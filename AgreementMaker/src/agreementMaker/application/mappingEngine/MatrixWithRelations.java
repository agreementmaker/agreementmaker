package agreementMaker.application.mappingEngine;

public class MatrixWithRelations extends Matrix {
	
	public final static String EQUIVALENCE = "=";

    private final String[][] relations;   // M-by-N array

    // create M-by-N matrix of 0's
    public MatrixWithRelations(int M, int N) {
    	super(M,N);
        relations = new String[M][N];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                   relations[i][j] = EQUIVALENCE;
    }
    
    public String getRelation(int i, int j) {
    	return relations[i][j];
    }
    
    public void setRelation(int i, int j, String r){
    	relations[i][j] = r;
    }

}