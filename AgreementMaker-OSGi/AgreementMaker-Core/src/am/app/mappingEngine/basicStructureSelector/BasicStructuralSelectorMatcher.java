package am.app.mappingEngine.basicStructureSelector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;

/**
 * The Basic Structural Selector Matcher (BSS) looks at the structure of the source and target and the alignments given in input to see
 * which mappings respect the structure of both ontologies and returns a new alignment that increases the similarity value
 * to those mappings that respect the structure 
 * @author Michele Caci
 *
 */
public class BasicStructuralSelectorMatcher extends AbstractMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5031932189615614189L;
	
	// the AlignmentMatrices from the input matching algorithm
	protected SimilarityMatrix inputClassesMatrix = null;
	protected SimilarityMatrix inputPropertiesMatrix = null;
	
	// Increase bonus depending on the level (can be modified in order to have different results)
	private final double level0Bouns = 1.15; // 0.86 -> 1 (0.86 is the min similarity value to reach 1)
	private final double level1Bouns = 1.25; // 0.8 -> 1
	private final double levelNBouns = 1.4; // 0.71 -> 1
	
	/**
	 * Empty constructor
	 */
	public BasicStructuralSelectorMatcher() {
		super();
		
		// requires one (and only one) alignment 
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
		setName("Basic Structural Selector Matcher");
		setCategory(MatcherCategory.STRUCTURAL);
	}

	/**
	 * Constructor with parameters: no real need so far
	 * @param params_new parameters to give to the BSS
	 */
	public BasicStructuralSelectorMatcher(DefaultMatcherParameters params_new) {
		super(params_new);
		
		// requires one (and only one) alignment 
		minInputMatchers = 1;
		maxInputMatchers = 1;
	}
	
	/**
	 * Before the align process, we specify the references to the classes Matrix and the properties Matrix of the input Matcher
	 * @see am.app.mappingEngine.AbstractMatcher#beforeAlignOperations()
	 */
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		/*if( inputMatchers.size() != 1 ) {
    		throw new RuntimeException("BSS algorithm needs to have one and only one input matcher.");
    	}*/
		
		AbstractMatcher input = inputMatchers.get(0);
		
		inputClassesMatrix = (SimilarityMatrix) input.getClassesMatrix().clone();
    	inputPropertiesMatrix = (SimilarityMatrix)input.getPropertiesMatrix().clone();
	}
	
	/**
	 * Overridden method 
	 * @see am.app.mappingEngine.AbstractMatcher#alignNodesOneByOne(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes)
	 * @author michele
	 * NOTE: parameters sourceList and targetList are NOT used
	 */
	@Override
	protected SimilarityMatrix alignNodesOneByOne(List<Node> sourceList, List<Node> targetList, alignType typeOfNodes) throws Exception {
		
		// step 0: gathering all the data
		List<Node> source; // list of the source ontology concepts
		List<Node> target; // list of the target ontology concepts
		SimilarityMatrix input;  // input matrix from previous matching

    	if(typeOfNodes.equals(alignType.aligningClasses)){
    		source = sourceOntology.getClassesList();
    		target = targetOntology.getClassesList();
    		input = inputClassesMatrix;
    	}
    	else{
    		source = sourceOntology.getPropertiesList();
    		target = targetOntology.getPropertiesList();
    		input = inputPropertiesMatrix;
    	}
    	
    	// step 1: taking level 0 (= concepts with no ancestor) couples of best-matching concepts...
    	int currentLevel = 0;
    	source = groupElementsByLevel(source).get(currentLevel);
    	target = groupElementsByLevel(target).get(currentLevel);
    	List<Integer> rows = createIntList(source);
    	List<Integer> cols = createIntList(target);
    	List<Mapping> best = input.chooseBestN(rows, cols); // best-matching concepts selection here
    	// ...and increasing slightly their similarity value (depending on "currentLevel" now being 0)
		computeNewValues(input, best, currentLevel);
		currentLevel++;
		
    	// step 2: for every best mapping found in level 0 we recursively look at the children of both source and target concept ontology
		for(int i = 0; i < best.size(); i++){
			recursiveSSM(input, best.get(i), currentLevel);
		}
    	
		return input;
	}
	
	/**
	 * recursiveSSM: recursively computes the values of the alignment matrix on children of the concepts participating in a given mapping  
	 * @author michele 
	 * @param input the AligmentMatrix given from the previous matching algorithm
	 * @param currentMapping the current alignment to analyze
	 * @param currentLevel parameter needed to retrieve the increase bonus 
	 */
	protected void recursiveSSM(SimilarityMatrix input, Mapping currentMapping, int currentLevel){
		
		// step 0: gathering all the data
		List<Node> sourceChildren = currentMapping.getEntity1().getChildren(); // children of the source concept
		List<Node> targetChildren = currentMapping.getEntity2().getChildren(); // children of the target concept (both from the alignment)
		List<Integer> rowsChildren = createIntList(sourceChildren);	// matrix rows corresponding to sourceChildren
		List<Integer> colsChildren = createIntList(targetChildren);	// matrix columns corresponding to targetChildren
		List<Mapping> best = input.chooseBestN(rowsChildren, colsChildren); // best mappings within these children
		
		/* DEBUG INFORMATION
		 System.out.println(currentMapping.getString() + " " + best.size() + " " + hasOnlyLeaves(currentMapping.getEntity1()) + " " + hasOnlyLeaves(currentMapping.getEntity2()));
		*/
		/*
		 * (stop) condition for computing new values for the alignment:
		 * Either target or source concepts (or both) have only leaves
		 */
		if( (hasOnlyLeaves(currentMapping.getEntity1()) || hasOnlyLeaves(currentMapping.getEntity2())) ){
			// computation occurs when both concepts have children (computation is made on them)
			if(sourceChildren.size() > 0 && targetChildren.size() > 0){
				/* DEBUG INFORMATION
				System.out.println("Computation: " + currentLevel + " " + currentMapping.getString() + " A-" + sourceChildren.size() + " B-" + targetChildren.size() + " " + best.size());
				*/
				computeNewValues(input, best, currentLevel);
			}
			else {
				return; // otherwise do nothing
			}
		}
		else {
			// otherwise for every mapping found recursively call the recursiveSSM method 
	    	for(int i = 0; i < best.size(); i++){
	    		/* DEBUG INFORMATION
	    		System.out.println("Recursive call: " + currentLevel + " " + currentMapping.getString() + " A-" + sourceChildren.size() + " B-" + targetChildren.size() + " " + best.size());
	    		*/
				recursiveSSM(input, best.get(i), currentLevel + 1);
	    	}
		}
	}
	
	/**
	 * Overridden method
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#oneToOneMatching(SimilarityMatrix matrix)
	 * @author michele
	 */
	protected Alignment<Mapping> oneToOneMatching(SimilarityMatrix matrix){
		List<Mapping> list = matrix.chooseBestN();
		Alignment<Mapping> result = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getSimilarity() < getParam().threshold){
				break;
			}
			result.add(list.get(i));
		}
		return result;
		
	}
	
	public String getDescriptionString() {
		return "The Basic Structural Selector (BSS for short) is a matching method that visits the structure of the source\n" +
				"and the target ontologies to see whether the mappings between nodes of the two ontologies is respected.\n" +
				"The idea is that, if the two concepts are very similar to eachother, the mappings between the subconcepts\n" +
				"are more meaningful than those that map other concepts located in a different place in the structure.\n\n" +
				"The BSS method is a refining method (we call it a Second Layer Matcher), meaning that it requires\n" +
				"another Matcher to create the initial similarity values between the nodes and then operates\n" +
				"using the already computed similarities. BSS needs no parameters (so far)\n\n";
	}
	
	/******************************************* SUPPORT METHODS *******************************************************/
	
	/**
	 * groupElementsByLevel: takes an Ontology and partitions it with respect to the depth of every element
	 * actually it takes the concepts or the properties separately, so in order to run on the whole ontology
	 * it should run twice, once for the concepts and once for the properties.
	 * Takes O(n) where n is the size of the inputOntology
	 * @param inputOntology the ontology that has to be grouped by depth
	 * @author michele 
	 */
	protected ArrayList<ArrayList<Node>> groupElementsByLevel(List<Node> inputOntology) {
		
		// Scan once to get the maximum depth of a concept in the ontology, that will be the size of the external list
		int maxLevel = 0;
		Iterator<Node> nodeScanner = inputOntology.iterator();
		Node scannedNode;
		while( nodeScanner.hasNext() ) {
			scannedNode = nodeScanner.next();
			if( maxLevel < scannedNode.getLevel() && !this.isCancelled()  ) {
				maxLevel = scannedNode.getLevel();
			}
		}
		maxLevel++;
		
		// Now that we know maxLevel, we can create all the internal ArrayLists
		ArrayList<ArrayList<Node>> outputOntology = new ArrayList<ArrayList<Node>>(maxLevel);
		for(int i = 0; i < maxLevel; i++) {
			outputOntology.add(new ArrayList<Node>());
		}		
		
		// Now we'll scan the ontology again to place every element to the appropriate ArrayList
		nodeScanner = inputOntology.iterator();
		int currentNodeLevel = 0;
		while( nodeScanner.hasNext() ) {
			scannedNode = nodeScanner.next();
			currentNodeLevel = scannedNode.getLevel();
			if( !this.isCancelled() ) {
				outputOntology.get(currentNodeLevel).add(scannedNode);
			}
		}
		
		/*/ DEBUG INFORMATION
		for(int i = 0; i < outputOntology.size(); i++) {
			System.out.println("level " + i);
			for(int j = 0; j < outputOntology.get(i).size(); j++) {
				System.out.println(
						
						outputOntology.get(i).get(j).getLevel() + 
						" " + 
						outputOntology.get(i).get(j).getLocalName() +
						" " +
						outputOntology.get(i).get(j).getChildren().size() +
						" " +
						outputOntology.get(i).get(j).getIndex()
						
						);
			}
		}
		*/
		
		return outputOntology;	
	}

	/**
	 * createIntList: creates an ArrayList of integers with the concepts of the ontology provided 
	 * Takes O(n)
	 * @param inputNodes list of nodes we want to get the value of the rows/columns  
	 * @author michele 
	 */
	private void computeNewValues(SimilarityMatrix input, List<Mapping> best, int currentLevel){
		
		// we check for the increasing amount first
		double bonus = 0;
		for(int i = 0; i < best.size(); i++){
    		switch(currentLevel){
	    		case 0:
	    			bonus = level0Bouns;
	    			break;
	    		case 1:
	    			bonus = level1Bouns;
	    			break;
    			default:
    				bonus = levelNBouns;
    				break;
    		}
    		// we compute the similarity then
    		Mapping m = input.get(best.get(i).getSourceKey(), best.get(i).getTargetKey());
    		if( m != null )	m.setSimilarity(Math.min(1.0, best.get(i).getSimilarity() * bonus));

    		
			// and we discard from further computation couples of best-matching concepts that don't have high similarity value
    		if(best.get(i).getSimilarity() < getParam().threshold){
    			best.remove(i);
    			i--; // we need to balance i++ at the end of the for loop when an element is removed
    		}
    		
		}
	}
	
	/**
	 * createIntList: creates an ArrayList of integers with the concepts of the ontology provided 
	 * Takes O(n)
	 * @param inputNodes list of nodes we want to get the value of the rows/columns  
	 * @author michele 
	 */
	private List<Integer> createIntList(List<Node> inputNodes){
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < inputNodes.size(); i++){
			list.add(inputNodes.get(i).getIndex());
		}
		return list;		
	}
	
	/**
	 * hasOnlyLeaves: checks if the input node has only leaves (nodes with no more children)
	 * @param n the node we are checking
	 * @author michele 
	 */
	protected boolean hasOnlyLeaves(Node n){
		List<Node> children = n.getChildren();
		for(int i = 0; i < children.size(); i++){
			if(!children.get(i).isLeaf()) return false;
		}
		return true;
	}
}

