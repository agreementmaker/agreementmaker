package agreementMaker.application.mappingEngine.dsi;

import java.util.ArrayList;

import javax.swing.tree.TreeNode;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.AlignmentMatrix;
import agreementMaker.application.ontology.Node;
import agreementMaker.userInterface.vertex.Vertex;

public class DescendantsSimilarityInheritanceMatcher extends AbstractMatcher {

	// the Alignment Matrices from the Input Matching algorithm.
	private AlignmentMatrix inputClassesMatrix = null;
	private AlignmentMatrix inputPropertiesMatrix = null;
	

	
	private double MCP;
	
	public DescendantsSimilarityInheritanceMatcher() {
		super();
		

		parametersPanel = new DescendantsSimilarityInheritanceParametersPanel();
		needsParam = true; // we need to set the MCP before running DSI
		
		
		// requires base similarity result (but can work on any alignment result) 
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
	}
	
	
	/**
	 * Before the align process, have a reference to the classes Matrix, and the properties Matrix of the input Matcher
	 * Also, get our MCP value, which is set by the user 
	 * @see agreementMaker.application.mappingEngine.AbstractMatcher#beforeAlignOperations()
	 */
	protected void beforeAlignOperations()throws Exception {
		super.beforeAlignOperations();
    	if( inputMatchers.size() != 1 ) {
    		throw new RuntimeException("DSI Algorithm needs to have one input matcher.");
    	}
    	
    	AbstractMatcher input = inputMatchers.get(0);
    	
    	inputClassesMatrix = input.getClassesMatrix();
    	inputPropertiesMatrix = input.getPropertiesMatrix();
    	
    	// set our MCP
    	MCP = ((DescendantsSimilarityInheritanceParameters)this.param).MCP;
    	
	}
	
	
	// overriding the abstract method in order to keep track of what kind of nodes we are aligning
    protected AlignmentMatrix alignProperties(ArrayList<Node> sourcePropList, ArrayList<Node> targetPropList) {
		return alignNodesOneByOne(sourcePropList, targetPropList, alignType.aligningProperties );
	}

	// overriding the abstract method in order to keep track of what kind of nodes we are aligning
    protected AlignmentMatrix alignClasses(ArrayList<Node> sourceClassList, ArrayList<Node> targetClassList) {
		return alignNodesOneByOne(sourceClassList, targetClassList, alignType.aligningClasses);
	}
	
	// this method is exactly similar to the abstract method, except we pass one extra parameters to the alignTwoNodes function
    protected AlignmentMatrix alignNodesOneByOne(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes) {
		AlignmentMatrix matrix = new AlignmentMatrix(sourceList.size(), targetList.size());
		Node source;
		Node target;
		Alignment alignment; //Temp structure to keep sim and relation between two nodes, shouldn't be used for this purpose but is ok
		for(int i = 0; i < sourceList.size(); i++) {
			source = sourceList.get(i);
			for(int j = 0; j < targetList.size(); j++) {
				target = targetList.get(j);
				alignment = alignTwoNodes(source, target, typeOfNodes);
				matrix.set(i,j,alignment);
				stepDone();  // Progress Dialog
			}
			updateProgress();  // Progress Dialog
		}
		return matrix;
	}
    

	/**
	 * @author Cosmin Stroe
	 * @date Nov 23, 2008
	 * Align Two nodes using DSI algorithm.
	 * @see agreementMaker.application.mappingEngine.AbstractMatcher#alignTwoNodes(agreementMaker.application.ontology.Node, agreementMaker.application.ontology.Node)
	 */
	protected Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {

		
		/**
		 * @author Cosmin Stroe
		 * @date Nov 23, 2008
		 * 
		 * Definition: input_sim(node1, node2) = the similarity between node1 and node2, calculated from the previous algorithm (the input algorithm)
		 * 
		 * Definition:  path_len_root(node) = number of edges between node and root of the tree
		 * 
		 * Definition: parent_i(node) = the i-th parent of node ( i=1 is the father, i=2 is the grandfather, etc..)
		 * 
		 * Definition: MCP = a fractional constant which is tuned to give the similarity result  ( 0.75 is the default MCP )
		 * 
		 * DSI Algorithm:
		 *                                                           n
		 *                                                         _____ 
		 *                                              2(1 - MCP) \    |
		 * DSI_sim = MCP * input_sim(source,target) +   ----------  \     (n + 1 - i) * input_sim( parent_i(source), parent_i(target) )
		 * 												  n(n+1)    /  
		 *                                                         /____|
		 *                                                           i=1
		 *                                                           
		 *  Where n = min( path_len_root(source), path_len_root(target) )  ( also represents the number of ancestors the node with the least ancestors has)
		 */
		
		Vertex vsource = source.getVertex();
		Vertex vtarget = target.getVertex();
		
		TreeNode[] sourcePath = vsource.getPath();  // get the path to root from source vertex
		TreeNode[] targetPath = vtarget.getPath();  // get the path to root from target vertex
		
		int n = 0;  // n = number of ancestors of the node with the least ancestors
		
		
		if( sourcePath.length > targetPath.length ) {
			// the target node is closer to its root (fewer ancestors)
			n = targetPath.length - 2 - 1;  // minus 2 because the first two levels of the Vertex hierarchy are not real nodes, and minus 1 because the last entry of the Path array is the node itself and not a parent
		} else {
			// the source node is closer to its root (fewer ancestors)
			n = sourcePath.length - 2 - 1;  // minus 2 because the first two levels of the Vertex hierarchy are not real nodes, and minus 1 because the last entry of the Path array is the node itself and not a parent
		}
		
		
		// calculate Summation: sum(i=1 to n: (n + 1 - i) * input_sim( parent_i(source), parent_i(target) );
		
		double summation = 0.0d;
		
		
		for( int i = 1; i <= n; i++ ) {
			
			// Here we are using the TreeNode array returned by getPath();
			// The last entry of the array is the node,
			// the previous to last is the parent of the node, and so on
			
			// sourcePath.length - 1 gives the index to the last element of the matrix (which is the current node);
			// we then subtract i, to get the index of the i-th parent of the current node 
			int sourceIndex = sourcePath.length - 1 - i; 
			int targetIndex = targetPath.length - 1 - i;
			
			// now that we have the index or the i-th parent, retrieve that parent
			Vertex sourceParent_i = (Vertex) sourcePath[sourceIndex];
			Vertex targetParent_i = (Vertex) targetPath[targetIndex];
			
			// get the index of the source and target nodes in the AlignmentMatrix
			int sourceMatrixIndex = sourceParent_i.getNode().getIndex();
			int targetMatrixIndex = targetParent_i.getNode().getIndex();
			
			// now, we need to get the similarity of the parents to eachother, which is stored in the AlignmentMatrix
			Alignment parentSimilarity = null;
			switch( typeOfNodes ) {
			case aligningClasses:
				// we are aligning class nodes, so lookup the similarity in the classes matrix
				parentSimilarity = inputClassesMatrix.get(sourceMatrixIndex, targetMatrixIndex);
				break;
			case aligningProperties:
				// we are aligning property nodes, so lookup the similarity in the properties matrix
				parentSimilarity = inputPropertiesMatrix.get(sourceMatrixIndex, targetMatrixIndex);
				break;	 
			}
			
			// we got the similarity between the parents, now we can finally calculate the current term of the summation
			
			double currentTerm = (n + 1 - i) * parentSimilarity.getSimilarity();
			
			
			// add the current term to the summation result
			summation += currentTerm;
			
			
		}
		
		
		// so, at this point, we have computed the summation, 
		// next let's finish up the formula
		
		// we need to get the input similarity between the two nodes we are comparing, so get that now 
		
		int sourceIndex = source.getIndex();
		int targetIndex = target.getIndex();
		
		Alignment baseSimilarity = null;
		switch( typeOfNodes ) {
		case aligningClasses:
			baseSimilarity = inputClassesMatrix.get(sourceIndex, targetIndex);
			break;
		case aligningProperties:
			baseSimilarity = inputPropertiesMatrix.get(sourceIndex, targetIndex);
			break;
		}
		
		
		// the final DSI similarity computed between the current two nodes
		double DSI_similarity = 0.0d;
		if( n == 0 ) {
			// if n == 0, then no summation component.
			DSI_similarity = baseSimilarity.getSimilarity();
		} else {
			DSI_similarity = MCP * baseSimilarity.getSimilarity() + ((2*(1 - MCP))/(n*(n+1)))* summation;
		}
		
		// return the result
		return new Alignment(source, target, DSI_similarity, Alignment.EQUIVALENCE);
		
	}

	
	public String getDescriptionString() {
		String description;
		
		
		description = "Descendant's Similarity Inheritance (DSI for short) is a matching method that considers the parent concepts of \nthe nodes being compared.  The idea is that, if the parents of two nodes are very similar to eachother, \nthat should mean that the children of those parents should be related to some degree.\n\n";
		description += "The DSI method is a refining method (we call it a Second Layer Matcher), meaning that it \nrequires another Matcher to create the initial similarity values between the nodes (called the Base Similarity), \nand then operates using the already computed similarities.\n\n";
		description += "The only parameter required by the DSI is the Main Contribution Percentage (MCP).  \nThe MCP controls how much of the computed DSI similarity should come from the previous \nMatcher, and how much should come from the current DSI Matcher.  \n\nFor example, if MCP=0.75, the output of the DSI will be 75% from the previous Matcher, and 25% from the DSI.\nIf you set the MCP close to 0%, the alignment that will be computed \nby the DSI will be almost completely based on the parents of every node, ignoring how similar \nthe actual nodes are to eachother.\n\n";
		description += "Therefore, the MCP cannot be too low, or the relations between the actual nodes \nwill be ignored, while it cannot be to high, because the contribution from the DSI will be negligible.  \nThe MCP value is subject to experimentation, as there is no automatic way of choosing the MCP (yet).\n\n";
		return description;
	}
	
}
