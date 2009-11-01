package am.app.feedback.matchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import am.app.feedback.FilteredAlignmentMatrix;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.AlignmentSet;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceParameters;
import am.app.mappingEngine.oneToOneSelection.MappingMWBM;
import am.app.mappingEngine.oneToOneSelection.MaxWeightBipartiteMatching;
import am.app.ontology.Node;
import am.app.ontology.TreeToDagConverter;

public class ExtrapolatingDSI extends DescendantsSimilarityInheritanceMatcher {

	
	protected FilteredAlignmentMatrix inputClassesMatrix = null;
	protected FilteredAlignmentMatrix inputPropertiesMatrix = null;
	
	protected FilteredAlignmentMatrix matrix;
	
	protected void beforeAlignOperations()throws Exception {
		super.beforeAlignOperations();
    	if( inputMatchers.size() != 1 ) {
    		throw new RuntimeException("DSI Algorithm needs to have one input matcher.");
    	}
    	
    	AbstractMatcher input = inputMatchers.get(0);
    	
    	inputClassesMatrix = (FilteredAlignmentMatrix) input.getClassesMatrix();
    	inputPropertiesMatrix = (FilteredAlignmentMatrix) input.getPropertiesMatrix();
    	
    	// set our MCP
    	MCP = ((DescendantsSimilarityInheritanceParameters)this.param).MCP;
    	
	}
	
	/**
	 * This method is almost exactly the same as the original DSI, but it keeps track of which rows/cols are filtered, therefore
	 * speeding up the execution, because it does not compute the DSI for the filtered nodes.
	 */
    protected AlignmentMatrix alignNodesOneByOne(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes) throws Exception {
    	//this the structure used in the recursive algorithms to keep track of the DSI computed for the parents of each node
    	//at the end it is returned and becomes the class or property matrix of the case
    	matrix = new FilteredAlignmentMatrix(sourceList.size(), targetList.size(), typeOfNodes, relation);
		
    	// copy the filtered rows/cols, so they can be skipped
    	switch( typeOfNodes ) {
    	case aligningClasses:
    		matrix.validateAlignments( inputClassesMatrix );
    		break;
    	case aligningProperties:
    		matrix.validateAlignments( inputPropertiesMatrix );
    		break;
    	}
    	
    	//we need to work on a DAG not on the vertex
		TreeToDagConverter sourceDag;
		TreeToDagConverter targetDag;
		AlignmentMatrix input;

    	if(typeOfNodes.equals(alignType.aligningClasses)){
    		sourceDag = new TreeToDagConverter(sourceOntology.getClassesTree());
    		targetDag = new TreeToDagConverter(targetOntology.getClassesTree());
    		input = inputClassesMatrix;
    	}
    	else{
    		sourceDag = new TreeToDagConverter(sourceOntology.getPropertiesTree());
    		targetDag = new TreeToDagConverter(targetOntology.getPropertiesTree());
    		input = inputPropertiesMatrix;
    	}
    	
    	ArrayList<Node> sourceConcepts = sourceList;
    	ArrayList<Node> targetConcepts = targetList;
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
    			if( !this.isCancelled() ) { recursiveDSI(sourceNode, targetNode, input, sourceDag, targetDag); }
				else { return matrix; }
    			
    		}
    	}
    	return matrix;
	}
    

	protected Alignment recursiveDSI(Node sourceNode, Node targetNode, AlignmentMatrix input, TreeToDagConverter sourceDag, TreeToDagConverter targetDag) {
		int sourceIndex = sourceNode.getIndex();
		int targetIndex = targetNode.getIndex();
		double mySim = input.get(sourceIndex, targetIndex).getSimilarity();
		ArrayList<Node> sourceParents = sourceNode.getParents();
		ArrayList<Node> targetParents = targetNode.getParents();
		Iterator<Node> itSource = sourceParents.iterator();
		Iterator<Node> itTarget = targetParents.iterator();
		Node sourceParent;
		Node targetParent;
		int sourceParentIndex;
		int targetParentIndex;
		Alignment alignParents;
		Alignment maxAlignParents;
		//sumOfMaxParents will keep the information related to the parents similarity (the 1-MCP part)
		double sumOfMaxParents;
		
		if( this.isCancelled() ) { return null; }
		
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
					alignParents = matrix.get(sourceParentIndex,targetParentIndex); // TODO: the get() function no longer returns null! (cos,10-29-09)
					//if there is already an alignment it means that I have already processed the DSI of these two nodes
					if( matrix.isCellEmpty( sourceParentIndex, targetParentIndex) ){
						//if it's not processed I run the recusive method to process it and to set it in the matrix
						alignParents = recursiveDSI(sourceParent, targetParent, input, sourceDag, targetDag);
					}
					if(maxAlignParents == null || maxAlignParents.getSimilarity() < alignParents.getSimilarity()){
						maxAlignParents = alignParents;
					}
				}
				sumOfMaxParents += maxAlignParents.getSimilarity();
			}
			sumOfMaxParents/=sourceParents.size();
		}
		else if(itSource.hasNext() || itTarget.hasNext()){
			//only one of the two nodes has parents. 
			//this case is in the middle between the previous one in which this case would have been 0 and the next one in which is 1.
			//there is some sort of dissimilarity but it may also be that the nodes are the same but the fathers of one of the two nodes have not been considered in this ontology.
			//given this doubt we set the parents contribution to 0.5 which penalizes it a little but not too much
			sumOfMaxParents = 0.5d;
		}
		else{
			//none of them have a parent so their parents contribution is identical
			sumOfMaxParents = 1;
		}

		double mcp = MCP;
		double finalSim = (mcp*mySim) + ((1 - mcp) * sumOfMaxParents);
		Alignment result = new Alignment(sourceNode, targetNode, finalSim, Alignment.EQUIVALENCE);
		matrix.set(sourceIndex, targetIndex, result);
		
		return result;
		
		
	}
	
}
