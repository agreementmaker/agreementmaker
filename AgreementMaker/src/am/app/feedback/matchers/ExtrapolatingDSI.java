package am.app.feedback.matchers;

import java.util.ArrayList;
import java.util.Iterator;

import am.app.feedback.FilteredAlignmentMatrix;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import am.app.ontology.Node;
import am.app.ontology.TreeToDagConverter;

public class ExtrapolatingDSI extends DescendantsSimilarityInheritanceMatcher {

	
	protected FilteredAlignmentMatrix inputClassesMatrix = null;
	protected FilteredAlignmentMatrix inputPropertiesMatrix = null;
	
	protected FilteredAlignmentMatrix matrix;
	
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
    		matrix.filter( inputClassesMatrix );
    		break;
    	case aligningProperties:
    		matrix.filter( inputPropertiesMatrix );
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
    
    
	
}
