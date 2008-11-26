package agreementMaker.application.mappingEngine.Matchers;

import java.util.ArrayList;

import javax.swing.tree.TreeNode;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.AlignmentMatrix;
import agreementMaker.application.ontology.Node;
import agreementMaker.userInterface.vertex.Vertex;

public class SiblingsSimilarityContributionMatcher extends AbstractMatcher {

	// the Alignment Matrices from the Input Matching algorithm.
	private AlignmentMatrix inputClassesMatrix = null;
	private AlignmentMatrix inputPropertiesMatrix = null;
	
	private double MCP;
	
	public SiblingsSimilarityContributionMatcher(int key, String theName) {
		super(key, theName);
		// TODO Auto-generated constructor stub
		
		parametersPanel = new SiblingsSimilarityContributionParametersPanel(); 

		needsParam = true; // we need to set the MCP before running SSC
		
		
		// requires base similarity result (but can work on any alignment result) 
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
	}
	
	/**
	 * Before the align process, have a reference to the classes Matrix, and the properties Matrix of the input Matcher
	 * Also, get our MCP value, which is set by the user 
	 * @see agreementMaker.application.mappingEngine.AbstractMatcher#beforeAlignOperations()
	 */
	protected void beforeAlignOperations() {
    	classesMatrix = null;
    	propertiesMatrix = null;
    	modifiedByUser = false;
    	
    	if( inputMatchers.size() != 1 ) {
    		throw new RuntimeException("SSC Algorithm needs to have one input matcher.");
    	}
    	
    	AbstractMatcher input = inputMatchers.get(0);
    	
    	inputClassesMatrix = input.getClassesMatrix();
    	inputPropertiesMatrix = input.getPropertiesMatrix();
    	
    	// set our MCP
    	MCP = ((SiblingsSimilarityContributionParameters)this.param).MCP;
    	
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
			}
		}
		return matrix;
	}
    

	/**
	 * @author Cosmin Stroe
	 * @date Nov 26, 2008
	 * Align Two nodes using SSC algorithm.
	 * @see agreementMaker.application.mappingEngine.AbstractMatcher#alignTwoNodes(agreementMaker.application.ontology.Node, agreementMaker.application.ontology.Node)
	 */
	protected Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {

		
		/**
		 * @author Cosmin Stroe
		 * @date Nov 26, 2008
		 * 
		 * Definition: input_sim(node1, node2) = the similarity between node1 and node2, calculated from the previous algorithm (the input algorithm)
		 * 
		 * Definition: sibling_count(node) = number of siblings that node has
		 * 
		 * Definition: SiblingSet(node) = the set of nodes that are the siblings of node
		 * 
		 * Definition: MCP = a fractional constant which is tuned to give the similarity result  ( 0.75 is the default MCP )
		 * 
		 * SSC Algorithm:
		 * 
		 * if ( SiblingSet(source).isNotEmpty() AND SiblingSet(target).isNotEmpty() ) then {
		 *                                                           n
		 *                                                         _____ 
		 *                                               1 - MCP   \    |
		 * SSC_sim = MCP * input_sim(source,target) +   ----------  \     max( input_sim( source_sibling_i, target_sibling_1 ), ... , source_sibling_i, target_sibling_m ) 
		 * 												    n       /  
		 *                                                         /____|
		 *                                                           i=1
		 *                                                           
		 * } otherwise {  SSC_sim = input_sim(source,target) }
		 *                                                            
		 * Where n = sibling_count(source) and m = sibling_count(target)
		 */
		
		Vertex vsource = source.getVertex();
		Vertex vtarget = target.getVertex();
		
		TreeNode sourceParent = vsource.getParent(); // parent of source node
		TreeNode targetParent = vtarget.getParent(); // parent of target node
		
		// create the SiblingSets for the source and target nodes
		ArrayList<Vertex> sourceSiblingSet = new ArrayList<Vertex>();  // set of siblings of the source node
		for( int i = 0; i < sourceParent.getChildCount(); i++ ) {	
			Vertex currentChild = (Vertex) sourceParent.getChildAt(i);
			if( currentChild.equals(vsource) ) { continue; } // do not add the node to its own sibling set
			sourceSiblingSet.add( currentChild );
		}
		
		ArrayList<Vertex> targetSiblingSet = new ArrayList<Vertex>();  // set of siblings of the target node
		for( int i = 0; i < targetParent.getChildCount(); i++ ) {	
			Vertex currentChild = (Vertex) targetParent.getChildAt(i);
			if( currentChild.equals(vtarget) ) { continue; } // do not add the node to its own sibling set
			targetSiblingSet.add( currentChild );
		}
		
		int n = sourceSiblingSet.size();  // number of siblings of the source node  i.e. sibling_count(source)
		int m = targetSiblingSet.size();  // number of siblings of the target node  i.e. sibling_count(target)

		
		// calculate Summation: sum(i=1 to n: max( input_sim( source_sibling_i, target_sibling_1 ), ... , source_sibling_i, target_sibling_m ) );
		
		double summation = 0.0d;
		
		
		for( int i = 1; i <= n; i++ ) {
			
			Vertex currentSourceSibling = sourceSiblingSet.get(i-1);  // get the i-th sibling of the source node ( i-1 because i goes from 1 to n, while the index of the ArrayList goes from 0 to n-1 )
			
			double currentTerm = 0.0d;  // this is result of the current max function
			
			// calculate max( input_sim( source_sibling_i, target_sibling_1 ), ... , source_sibling_i, target_sibling_m )
			for( int j = 1; j <= m; j++ ) { 
				Vertex currentTargetSibling = targetSiblingSet.get(j-1);  // get the j-th sibling of the target node ( j-1 because j goes from 1 to m, while the index of the ArrayList goes from 0 to m-1 )
				
				Alignment inputSimilarity = null;  // this will store input_sim( source_sibling_i, target_sibling_j )
				int sourceMatrixIndex = currentSourceSibling.getNode().getIndex();  // index of the source node in the AlignmentMatrix 
				int targetMatrixIndex = currentTargetSibling.getNode().getIndex();  // index of the target node in the AlignmentMatrix 
				
				switch( typeOfNodes ) {
				case aligningClasses:
					// we are aligning class nodes, so lookup the similarity in the input classes matrix
					inputSimilarity = inputClassesMatrix.get(sourceMatrixIndex, targetMatrixIndex);
					break;
				case aligningProperties:
					// we are aligning property nodes, so lookup the similarity in the input property matrix
					inputSimilarity = inputPropertiesMatrix.get(sourceMatrixIndex, targetMatrixIndex);
					break;
				}
				
				// ok now, let's determine if the current similarity is the maximum
				if( inputSimilarity.getSimilarity() > currentTerm ) {
					// this is our new maximum
					currentTerm = inputSimilarity.getSimilarity();
				}
				
			}
						
			// now that we have evaluated the max() function, add it to the summation
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
		
		
		// the final SSC similarity computed between the current two nodes
		double SSC_similarity = 0.0d;
		if( n == 0 || m == 0 ) {
			// if n == 0 || m == 0, then there is no summation component.
			SSC_similarity = baseSimilarity.getSimilarity();
		} else {
			SSC_similarity = MCP * baseSimilarity.getSimilarity() + ((1 - MCP)/n)* summation;  // the SSC formula
		}
		
		// return the result
		return new Alignment(source, target, SSC_similarity, Alignment.EQUIVALENCE);
		
	}
	
	
}
