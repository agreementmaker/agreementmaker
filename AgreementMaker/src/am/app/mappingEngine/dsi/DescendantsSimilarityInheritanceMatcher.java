package am.app.mappingEngine.dsi;

import java.util.Iterator;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.TreeToDagConverter;

public class DescendantsSimilarityInheritanceMatcher extends AbstractMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1974656931030493132L;
	
	// the Alignment Matrices from the Input Matching algorithm.
	protected SimilarityMatrix inputClassesMatrix = null;
	protected SimilarityMatrix inputPropertiesMatrix = null;
	
	protected double MCP;
	protected boolean[][] isComputedAlready;
	
	public DescendantsSimilarityInheritanceMatcher() {
		super();
		
		needsParam = true; // we need to set the MCP before running DSI
		
		// requires base similarity result (but can work on any alignment result) 
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
		setName("Descendants Similarity Inheritance Matcher");
		setCategory(MatcherCategory.STRUCTURAL);
	}
	
	/**
	 * Before the align process, have a reference to the classes Matrix, and the properties Matrix of the input Matcher
	 * Also, get our MCP value, which is set by the user 
	 * @see am.app.mappingEngine.AbstractMatcher#beforeAlignOperations()
	 */
	@Override
	protected void beforeAlignOperations()throws Exception {
		super.beforeAlignOperations();
    	if( inputMatchers.size() != 1 ) {
    		throw new RuntimeException("DSI Algorithm needs to have one input matcher.");
    	}
    	
    	AbstractMatcher input = inputMatchers.get(0);
    	
    	inputClassesMatrix = (SimilarityMatrix) input.getClassesMatrix().clone();
    	inputPropertiesMatrix = (SimilarityMatrix)input.getPropertiesMatrix().clone();
    	
    	// set our MCP
    	MCP = ((DescendantsSimilarityInheritanceParameters)this.param).MCP;
    	
	}

	@Override
    protected SimilarityMatrix alignNodesOneByOne(List<Node> sourceList, List<Node> targetList, alignType typeOfNodes) throws Exception {
    	
    	//we need to work on a DAG not on the vertex
		TreeToDagConverter sourceDag;
		TreeToDagConverter targetDag;
		SimilarityMatrix input;

    	if(typeOfNodes.equals(alignType.aligningClasses)){
    		sourceDag = new TreeToDagConverter(sourceOntology.getClassesRoot());
    		targetDag = new TreeToDagConverter(targetOntology.getClassesRoot());
    		input = inputClassesMatrix;
    	}
    	else{
    		sourceDag = new TreeToDagConverter(sourceOntology.getPropertiesRoot());
    		targetDag = new TreeToDagConverter(targetOntology.getPropertiesRoot());
    		input = inputPropertiesMatrix;
    	}
    	initBooleanMatrix(input);
    	List<Node> sourceConcepts = sourceList;
    	List<Node> targetConcepts = targetList;
    	Iterator<Node> itSource = sourceConcepts.iterator();
    	Iterator<Node> itTarget;
    	Node sourceNode;
    	Node targetNode;
    	while(itSource.hasNext()){
    		sourceNode = itSource.next();
    		itTarget = targetConcepts.iterator();
    		while(itTarget.hasNext()){
    			targetNode = itTarget.next();
    			//this method compute the dsi alignment between the two nodes and also between all of their parents
    			if( !this.isCancelled() ) {
    				recursiveDSI(sourceNode, targetNode, input, sourceDag, targetDag); }
				else {
					return input; 
				}

    		}
    	}
    	return input;
	}

	
	protected void initBooleanMatrix(SimilarityMatrix input) {
		isComputedAlready = new boolean[input.getRows()][input.getColumns()];
	}


	protected Mapping recursiveDSI(Node sourceNode, Node targetNode, SimilarityMatrix input, TreeToDagConverter sourceDag, TreeToDagConverter targetDag) {
		int sourceIndex = sourceNode.getIndex();
		int targetIndex = targetNode.getIndex();
		double mySim = 0.0d;
		if( input.get(sourceIndex, targetIndex) != null ) mySim = input.get(sourceIndex, targetIndex).getSimilarity();
		List<Node> sourceParents = sourceNode.getParents();
		List<Node> targetParents = targetNode.getParents();
		Iterator<Node> itSource = sourceParents.iterator();
		Iterator<Node> itTarget = targetParents.iterator();
		Node sourceParent;
		Node targetParent;
		int sourceParentIndex;
		int targetParentIndex;
		Mapping alignParents;
		Mapping maxAlignParents;
		//sumOfMaxParents will keep the information related to the parents similarity (the 1-MCP part)
		double sumOfMaxParents;
		Mapping result;
		if(isComputedAlready[sourceIndex][targetIndex]){
			//the DSI has already been computed and stored for this mapping and their parents
			result = input.get(sourceIndex,targetIndex);
		}
		else{
			if(itSource.hasNext() && itTarget.hasNext()){
				
				//if both nodes have at least one parent
				//for each source parent I find the most similar target parent
				//then I take the average of all similarities of the most similar parents
				//that is the parents contribution
				sumOfMaxParents = 0;
				while(itSource.hasNext()){
					sourceParent = itSource.next();
					sourceParentIndex = sourceParent.getIndex();
					itTarget = targetParents.iterator();
					maxAlignParents = null;
					while(itTarget.hasNext()){
						targetParent = itTarget.next();
						targetParentIndex = targetParent.getIndex();
						//if there is already an alignment it means that I have already processed the DSI of these two nodes
						if( isComputedAlready[ sourceParentIndex][ targetParentIndex] ){
							alignParents = input.get(sourceParentIndex,targetParentIndex); 
						}
						else{
							//if it's not processed I run the recusive method to process it and to set it in the matrix
							alignParents = recursiveDSI(sourceParent, targetParent, input, sourceDag, targetDag);
						}
						if(maxAlignParents == null || maxAlignParents.getSimilarity() < alignParents.getSimilarity()){
							maxAlignParents = alignParents;
						}
					}
					if( maxAlignParents != null ) sumOfMaxParents += maxAlignParents.getSimilarity();
				}
				sumOfMaxParents/=sourceParents.size();
				double mcp = MCP;
				double finalSim = (mcp*mySim) + ((1 - mcp) * sumOfMaxParents);
				result = new Mapping(sourceNode, targetNode, finalSim, MappingRelation.EQUIVALENCE);
				input.set(sourceIndex, targetIndex, result);
				
				result = input.get(sourceIndex,targetIndex);
			}
			else if(itSource.hasNext() || itTarget.hasNext()){
				//only one of the two nodes has parents. 
				//this case is in the middle between the previous one in which this case would have been 0 and the next one in which is 1.
				//there is some sort of dissimilarity but it may also be that the nodes are the same but the fathers of one of the two nodes have not been considered in this ontology.
				//given the uncertainty we don't modify their similarity
				result = input.get(sourceIndex,targetIndex);
			}
			else{
				//none of them have a parent so their parents contribution is identical
				//given the uncertainty we don't modify their similarity
				result = input.get(sourceIndex,targetIndex);
			}
			isComputedAlready[sourceIndex][targetIndex] = true;
		}
		return result;
	}

	@Override
	public String getDescriptionString() {
		String description;
		
		
		description = "Descendant's Similarity Inheritance (DSI for short) is a matching method that considers the parent concepts of \nthe nodes being compared.  The idea is that, if the parents of two nodes are very similar to eachother, \nthat should mean that the children of those parents should be related to some degree.\n\n";
		description += "The DSI method is a refining method (we call it a Second Layer Matcher), meaning that it \nrequires another Matcher to create the initial similarity values between the nodes (called the Base Similarity), \nand then operates using the already computed similarities.\n\n";
		description += "The only parameter required by the DSI is the Main Contribution Percentage (MCP).  \nThe MCP controls how much of the computed DSI similarity should come from the previous \nMatcher, and how much should come from the current DSI Matcher.  \n\nFor example, if MCP=0.75, the output of the DSI will be 75% from the previous Matcher, and 25% from the DSI.\nIf you set the MCP close to 0%, the alignment that will be computed \nby the DSI will be almost completely based on the parents of every node, ignoring how similar \nthe actual nodes are to eachother.\n\n";
		description += "Therefore, the MCP cannot be too low, or the relations between the actual nodes \nwill be ignored, while it cannot be to high, because the contribution from the DSI will be negligible.  \nThe MCP value is subject to experimentation, as there is no automatic way of choosing the MCP (yet).\n\n";
		return description;
	}
	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new DescendantsSimilarityInheritanceParametersPanel();
		}
		return parametersPanel;
	}
	
}
