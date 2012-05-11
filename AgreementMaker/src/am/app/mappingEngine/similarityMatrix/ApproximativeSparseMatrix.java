package am.app.mappingEngine.similarityMatrix;

import java.util.ArrayList;

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
	}
	
	public ApproximativeSparseMatrix(SparseMatrix s){super(s);}
/**------------------------------------------------------------------------------------------------*/
	
	
/**---------------------Constructors that include the threshold as a param-------------------------*/	
		public ApproximativeSparseMatrix(Ontology sourceOntology, Ontology targetOntology,
				alignType typeOfMatrix, double threshold) {
			super(sourceOntology, targetOntology, typeOfMatrix);
			this.threshold=threshold;
		}
		
		public ApproximativeSparseMatrix(SparseMatrix s, double threshold){
			super(s);
			this.threshold=threshold;
		}
/**------------------------------------------------------------------------------------------------*/
	
	
	/** getters */
	public double getThreshold() {return threshold;}
	
	/** setters */
	public void setThreshold(double threshold) {this.threshold = threshold;}
}