/**
 * 
 */
package am.app.mappingEngine.groupFinder;

import java.util.ArrayList;
import java.util.Iterator;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.Alignment;
import am.app.ontology.Node;

/**
 * @author nikiforos
 *
 */
public class GroupFinderMatcher extends AbstractMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2823104411109824547L;
	// the AlignmentMatrices from the input matching algorithm
	protected SimilarityMatrix inputClassesMatrix = null;
	protected SimilarityMatrix inputPropertiesMatrix = null;
	
	protected ArrayList<Node> source_root_list;
	protected ArrayList<Node> target_root_list;
	
	protected static double scaling_factor = 0.8;
	
	/**
	 * 
	 */
	public GroupFinderMatcher() {
		super();
		
		// requires one (and only one) alignment 
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
		source_root_list = new ArrayList<Node>();
		target_root_list = new ArrayList<Node>();
	}
	
	/**
	 * Constructor with parameters: no real need so far
	 * @param params_new parameters to give to the CFM
	 */
	public GroupFinderMatcher(AbstractParameters params_new) {
		super(params_new);
		
		// requires one (and only one) alignment 
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
		source_root_list = new ArrayList<Node>();
		target_root_list = new ArrayList<Node>();
	}
	
	/**
	 * Before the align process, we specify the references to the classes Matrix and the properties Matrix of the input Matcher
	 * @see am.app.mappingEngine.AbstractMatcher#beforeAlignOperations()
	 */
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		if( inputMatchers.size() != 1 ) {
    		throw new RuntimeException("GFM algorithm needs to have one and only one input matcher.");
    	}
		
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
	protected SimilarityMatrix alignNodesOneByOne(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes) throws Exception {
		
		// step 0: gathering all the data
		ArrayList<Node> source; // list of the source ontology concepts
		ArrayList<Node> target; // list of the target ontology concepts
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
    	
    	// building local matrix
	    
    	ArrayList<Mapping> localList = new ArrayList<Mapping>();
	    localList = selectGroups(groupElementsByLevel(source).get(0), groupElementsByLevel(target).get(0), input, typeOfNodes);
	    
	    for(int i = 0; i < localList.size(); i++){
	    	Node sourceRoot = localList.get(i).getEntity1();
	    	Node targetRoot = localList.get(i).getEntity2();
	    	// step 2: match within groups
	    	matchGroups(input, sourceRoot, targetRoot, typeOfNodes);
	    }
		return input;
	}
	
	/**
	 * Overridden method
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#oneToOneMatching(SimilarityMatrix matrix)
	 * @author michele
	 */
	protected Alignment<Mapping> oneToOneMatching(SimilarityMatrix matrix){
		ArrayList<Mapping> list = matrix.chooseBestN();
		Alignment<Mapping> result = new Alignment<Mapping>();
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getSimilarity() < getThreshold()){
				break;
			}
			result.addMapping(list.get(i));
		}
		return result;
	}
	
	/**
	 * Overridden method
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#getDescriptionString()
	 * @author michele
	 */
	public String getDescriptionString() {
		return "The Group Finder Matcher (GFM for short) is a matching method that visits the structure of the source\n" +
				"and the target ontologies to see whether the mappings between nodes of the two ontologies is respected.\n" +
				"The idea is that, if the two concepts are sibling in some ontolgy, the mappings should be between siblings\n" +
				"node concepts in target or in a different relation that is not subclass/subproperty relation\n\n" +
				"The GFM method is a refining method (we call it a Second Layer Matcher), meaning that it requires\n" +
				"another Matcher to create the initial similarity values between the nodes and then operates\n" +
				"using the already computed similarities. GFM needs no parameters (so far)\n\n";
	}
	
	/******************************************* SUPPORT METHODS *******************************************************/
	

	/**
	 * matchGroups: takes an Ontology and partitions it with respect to the depth of every element
	 * actually it takes the concepts or the properties separately, so in order to run on the whole ontology
	 * it should run twice, once for the concepts and once for the properties.
	 * @param inputOntology the ontology that has to be grouped by depth
	 * @author michele 
	 */
	protected ArrayList<Mapping> selectGroups(ArrayList<Node> sourceRoots, ArrayList<Node> targetRoots, SimilarityMatrix input, alignType typeOfNodes){
		source_root_list = sourceRoots;
    	target_root_list = targetRoots;
    	SimilarityMatrix localMatrix = new SimilarityMatrix(source_root_list.size(), target_root_list.size(), typeOfNodes);
    	localMatrix.initFromNodeList(source_root_list, target_root_list);
    	
    	// step 1: taking level 0 source concepts with their descendants and assigning groups
    	ArrayList<Mapping> localList = new ArrayList<Mapping>();
    	ArrayList<Integer> localCount = new ArrayList<Integer>(target_root_list.size());
    	for(int i = 0; i < target_root_list.size(); i++){
    		localCount.add(new Integer(0));
    	}
    	
    	ArrayList<Node> sourceSet, targetSet;
    	boolean targetSetFlag;
    	for(int i = 0; i < source_root_list.size(); i++){
    		sourceSet = new ArrayList<Node>(); // contains source current group	
        	targetSet = new ArrayList<Node>(); // contains target nodes to be used for selecting target group
        	
        	targetSetFlag = false;
    		// setting source current group and target node set
	    	sourceSet.add(source_root_list.get(i));
	    	sourceSet.addAll(source_root_list.get(i).getDescendants());
	    	for(int j = 0; j < target_root_list.size(); j++){
	    		targetSet.add(target_root_list.get(j));
		    	targetSet.addAll(target_root_list.get(j).getDescendants());
	    	}

	    	// computing best root
	    	localList = input.chooseBestN(createIntList(sourceSet), createIntList(targetSet));
	    	Mapping selectedMapping = null;
	    	double newSim = 0.0;
    		Node sourceRoot, targetRoot;
	    	
    		if(localList.size() > 0){
		    	for(int k = 0; k < localList.size(); k++){
		    		selectedMapping = localList.get(k);
		    		//System.out.println();
		    		//System.out.println("start:");
		    		sourceRoot = selectedMapping.getEntity1().getRoot();
		    		//System.out.println("source root " + sourceRoot.getLocalName());
		    		//System.out.println("target " + a.getEntity2());
		    		//System.out.println("target root " + a.getEntity2().getRoot().getLocalName());
		    		targetRoot = selectedMapping.getEntity2().getRoot();
		    		//System.out.println("target root " + targetRoot.getLocalName());
		    		int sourceInd = source_root_list.indexOf(sourceRoot);
		    		//System.out.println("sourceIndex " + sourceInd);
		    		int targetInd = target_root_list.indexOf(targetRoot);
		    		//System.out.println("targetIndex " + targetInd);
		    		newSim = selectedMapping.getSimilarity() + localMatrix.getSimilarity(sourceInd, targetInd);
		    		//System.out.println("newSim " + newSim);
		    		localMatrix.setSimilarity(sourceInd, targetInd, newSim);
		    		//System.out.println("localMatrix updated ");
		    		localCount.set(targetInd, localCount.get(targetInd) + 1);
		    		//System.out.println("localCount updated ");
		    	}
	    	}
	    	/*/ computing similarity average
	    	for(int j = 0; j < localCount.size(); j++){
	    		newSim = localMatrix.getSimilarity(i, j);
	    		localMatrix.setSimilarity(i, j, newSim / localCount.get(j));
	    		localCount.set(j, 0);
    		}*/
    	}
    	
		return localMatrix.chooseBestN();
	}
	
	protected void matchGroups(SimilarityMatrix input, Node sourceRoot, Node targetRoot, alignType typeOfNodes){
    	ArrayList<Node> sourceSet, targetSet;

    	sourceSet = new ArrayList<Node>(); // contains source current group	
    	targetSet = new ArrayList<Node>();
    	
    	sourceSet.add(sourceRoot);
    	sourceSet.addAll(sourceRoot.getDescendants());
  		targetSet.add(targetRoot);
		targetSet.addAll(targetRoot.getDescendants());
    	
		/** DEBUG INFO
		System.out.println("sourceSet " + sourceSet.toString());
		System.out.println("targetSet " + targetSet.toString());
		System.out.println();
		*/
		
    	computeNewValues(input, sourceSet, targetSet);
	}
	
	/**
	 * groupElementsByLevel: takes an Ontology and partitions it with respect to the depth of every element
	 * actually it takes the concepts or the properties separately, so in order to run on the whole ontology
	 * it should run twice, once for the concepts and once for the properties.
	 * Takes O(n) where n is the size of the inputOntology
	 * @param inputOntology the ontology that has to be grouped by depth
	 * @author michele 
	 */
	protected ArrayList<ArrayList<Node>> groupElementsByLevel(ArrayList<Node> inputOntology) {
		
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
	 * computeNewValues: modifies matrix by looking checking if every alignment has the source concept contained in the source list and
	 * the target concept contained in its one 
	 * @param inputMatrix the AlignmentMatrix where to get the n elements with highest similarity from
	 * @param sourceList list of source nodes 
	 * @param targetList list of target nodes
	 * @author michele 
	 */
	private void computeNewValues(SimilarityMatrix inputMatrix, ArrayList<Node> sourceList, ArrayList<Node> targetList){
		ArrayList<Integer> sList = createIntList(sourceList);
		ArrayList<Integer> tList = createIntList(targetList);
		
		for(int i = 0; i < inputMatrix.getRows(); i++){
			for(int j = 0; j < inputMatrix.getColumns(); j++){
				if((sList.contains(i) && !tList.contains(j)) || (!sList.contains(i) && tList.contains(j)))
				{
					inputMatrix.setSimilarity(i, j, inputMatrix.getSimilarity(i, j) * scaling_factor);
				}
			}
		}
	}
	
	/**
	 * createIntList: creates an ArrayList of integers with the concepts of the ontology provided 
	 * Takes O(n)
	 * @param inputNodes list of nodes we want to get the value of the rows/columns  
	 * @author michele 
	 */
	private ArrayList<Integer> createIntList(ArrayList<Node> inputNodes){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < inputNodes.size(); i++){
			list.add(inputNodes.get(i).getIndex());
		}
		return list;		
	}
	
	/**
	 * createIntListToN: creates an ArrayList of n integers from 0 to n-1
	 * useful to create a list for considering all the values of the rows or columns of the alignment matrix
	 * Takes O(n)
	 * @param n size of the ArrayList (n-1 is the last value) 
	 * @author michele 
	 */
	public static ArrayList<Integer> createIntListToN(int n){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < n; i++){
			list.add(i);
		}
		return list;		
	}
	
	/**
	 * chooseBestRoot: 
	 * @param 
	 * @author michele 
	 */
	protected Node chooseBestRoot(SimilarityMatrix input, ArrayList<Node> list, ArrayList<Node> target){
		
		
		
		ArrayList<Node> local_list = list;
		Node localNode, finalNode = null;
		int count = 0, max = 0;
		
		while(!local_list.isEmpty()){
			
			localNode = local_list.get(0);
			while(local_list.remove(localNode)){
				count++;
			}
			//System.out.println(localNode.getLocalName() + " " + count);

			if(count > max){
				max = count;
				finalNode = localNode;
			}
			else{}
			count = 0;
		}
		
		return finalNode;
	}

}