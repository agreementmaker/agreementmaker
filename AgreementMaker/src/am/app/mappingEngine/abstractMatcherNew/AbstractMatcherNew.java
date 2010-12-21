package am.app.mappingEngine.abstractMatcherNew;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Ontology;
import am.utility.Pair;

/**
 * @author michele
 * This class is intended to reduce and split the previous AbstractMatcher
 * with the aim of improving the readability of the code and the production of new matchers
 */
public abstract class AbstractMatcherNew implements MatcherNew {
	
	// TODO: understand how the optimized matching works and include it
	
	// MATCHER FIELDS //
	
	protected AbstractInfoParameters informations;
	protected AbstractMatchingParameters matchingParameters;
	protected AbstractSelectionParameters selectionParameters;

	/**
	 * This is the pair of ontologies you need as input for the matching process 
	 */
	protected Pair<Ontology, Ontology> inputOntologies;

	/**
	 * This is the pair of similarity matrices
	 * for classes and properties
	 * which is the output of the matching phase
	 * and the input of the selection phase 
	 */
	protected Pair<SimilarityMatrix, SimilarityMatrix> similarityMatrices;
	
	/**
	 * This is the pair of alignments of classes and properties
	 * which has to be returned as the final result of the selection phase
	 * a value of NULL for it or for one of the elements of the pair means that
	 * either classes or properties or both alignment are empty
	 * or haven't been calculated
	 */
	protected Pair<Alignment<Mapping>, Alignment<Mapping>> finalAlignment; 
	
	protected VoidMatchingMethod matcher; // TODO: replace with appropriate abstract methods
	protected VoidSelectionMethod selector;
	
	private int matcherID; // ID number to identify the matcher in the list
	
	// CONSTRUCTOR //
	
	/**
	 * TODO: Needs to add: loading the ontologies
	 */
	public AbstractMatcherNew() {
		this.informations = new AbstractInfoParameters();
		this.matchingParameters = new AbstractMatchingParameters();
		this.selectionParameters = new AbstractSelectionParameters();
	}
	
	// MAIN METHOD //
	@Override
	public void runMatcher(){
		this.similarityMatrices = this.matcher.match(inputOntologies);
		assert (this.similarityMatrices != null);
		this.finalAlignment = this.selector.select(similarityMatrices, 0, 0, 0); // TODO: to change parameters accordingly
		assert (this.finalAlignment != null);
	}
	
	// OTHER METHODS (TODO: to classify) //
	
	public abstract AbstractMatcherParametersPanel callParametersPanel();
	
	public void setID(int nextMatcherID) { matcherID = nextMatcherID; }
	public int  getID()                  { return matcherID; }
}
