/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;

/**
 * @author Michele Caci
 *
 */
public class PCGSimilarityMatrix extends ArraySimilarityMatrix {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2010468683366304011L;
	
	/**
	 * sourceOrigSize is intended to host the number of classes or properties of the source ontology in the ArraySimilarityMatrix
	 * this number is supposed to be no less than the number of rows of the equivalent ArraySimilarityMatrix
	 * otherwise it loses some concepts or properties of the ontology during calculations
	 */
	private int sourceOrigSize;
	/**
	 * targetOrigSize is intended to host the number of classes or properties of the target ontology in the ArraySimilarityMatrix
	 * this number is supposed to be no less than the number of columns of the equivalent ArraySimilarityMatrix
	 * otherwise it loses some concepts or properties of the ontology during calculations
	 */
	private int targetOrigSize;
	/**
	 * degenerateASMatrix is a flag to check whether the created PCG SM has the same dimensions of its equivalent ASM
	 */
	private boolean degeneratePCGMatrix;
	
//	private WrappingGraph sourceNodes;
//	private WrappingGraph targetNodes;
	
	public PCGSimilarityMatrix( PCGSimilarityMatrix cloneme ) {
    	super(cloneme);
    	sourceOrigSize = cloneme.sourceOrigSize;
    	targetOrigSize = cloneme.targetOrigSize;
    	degeneratePCGMatrix = cloneme.degeneratePCGMatrix;
//    	sourceNodes = cloneme.sourceNodes;
//    	targetNodes = cloneme.targetNodes;
	}
	
	public PCGSimilarityMatrix(WrappingGraph s, WrappingGraph t, alignType aType) {
		super(0, 0, aType);
		
		relation = MappingRelation.EQUIVALENCE;
    	typeOfMatrix = aType;
    	int M = 0;
    	int N = 0;
    	if(aType == alignType.aligningClasses){
    		sourceOrigSize = s.getClassMatSize();
    		targetOrigSize = t.getClassMatSize();
    		M = s.numVertices() - s.getPropertyMatSize();
    		N = t.numVertices() - t.getPropertyMatSize();
    		
		}
    	else if(aType == alignType.aligningProperties){
    		sourceOrigSize = s.getPropertyMatSize();
    		targetOrigSize = t.getPropertyMatSize();
    		M = s.getPropertyMatSize();
    		N = t.getPropertyMatSize();
		}
    	else{
    	}
    	
    	if((M == sourceOrigSize) && (N == targetOrigSize)){
			degeneratePCGMatrix = true;
		}
		else{
			degeneratePCGMatrix = false;
		}
    	
        this.rows = M;
        this.columns = N;
        data = new Mapping[M][N];
       
//        sourceNodes = s; targetNodes = t;
		
	}
	
	/**
	 * @param sourceOrigSize the sourceOrigSize to set
	 */
	public void setSourceOrigSize(int sourceOrigSize) {
		this.sourceOrigSize = sourceOrigSize;
	}

	/**
	 * @return the sourceOrigSize
	 */
	public int getSourceOrigSize() {
		return sourceOrigSize;
	}

	/**
	 * @param targetOrigSize the targetOrigSize to set
	 */
	public void setTargetOrigSize(int targetOrigSize) {
		this.targetOrigSize = targetOrigSize;
	}

	/**
	 * @return the targetOrigSize
	 */
	public int getTargetOrigSize() {
		return targetOrigSize;
	}

	/**
	 * @param degeneratePCGMatrix the degeneratePCGMatrix to set
	 */
	public void setDegeneratePCGMatrix(boolean degeneratePCGMatrix) {
		this.degeneratePCGMatrix = degeneratePCGMatrix;
	}

	/**
	 * @return the degeneratePCGMatrix
	 */
	public boolean isDegeneratePCGMatrix() {
		return degeneratePCGMatrix;
	}

	/**
	 * @return corresponding ASMatrix
	 */
	public ArraySimilarityMatrix toArraySimilarityMatrix(){
		
		ArraySimilarityMatrix asmNew = new ArraySimilarityMatrix(getSourceOrigSize(), getTargetOrigSize(), getAlignType());
		for(int i = 0; i < getSourceOrigSize(); i++){
			for(int j = 0; j < getTargetOrigSize(); j++){
				asmNew.set(i, j, this.get(i, j));
			}
		}
		return asmNew;
	}
}
