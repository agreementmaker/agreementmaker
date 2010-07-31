package am.extension;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.ontology.Node;

public class MyMatcher_LayerII extends AbstractMatcher {

	// Constructor
	public MyMatcher_LayerII() {
		super();

		// This matcher requires the input of one other matcher.  
		// These variables are set to let AgreementMaker how many input matchers the current matcher requires. 
		minInputMatchers = 1;
		maxInputMatchers = 1;
	}

	// the Alignment Matrices from the input matcher.
	private AlignmentMatrix inputClassesMatrix = null;
	private AlignmentMatrix inputPropertiesMatrix = null;
	
	@Override
	protected void beforeAlignOperations()throws Exception {
		super.beforeAlignOperations();

		if( inputMatchers.size() != 1 ) {
    		throw new RuntimeException("MyMatcher_LayerII needs to have one input matcher.");
    	}
    	
    	AbstractMatcher input = inputMatchers.get(0);
    	
    	// we must clone the AlignmentMatrix, otherwise we will be changing the alignment matrix of the input matcher.
    	inputClassesMatrix = (AlignmentMatrix) input.getClassesMatrix().clone();  // clone
    	inputPropertiesMatrix = (AlignmentMatrix)input.getPropertiesMatrix().clone(); // clone
    	
	}
	
	@Override
	public Alignment alignTwoNodes( Node source, Node target, alignType typeOfNodes ) {
	
		AlignmentMatrix currentInputMatrix;
		
		// depending on what kind of nodes we're working with, their similarities are stored in different matrices (either classesMatrix or propertiesMatrix).
		if(typeOfNodes.equals(alignType.aligningClasses)){
			currentInputMatrix = inputClassesMatrix;
		}
		else{
			currentInputMatrix = inputPropertiesMatrix;
		}

		// now, we get the index of the source node, and the index of the target node.
		int sourceIndex = source.getIndex();
		int targetIndex = target.getIndex();
		
		// the alignment that is returned represents the alignment between these specific source and target concepts.
		Alignment myAlignment = currentInputMatrix.get(sourceIndex, targetIndex);  

		if( myAlignment != null ) { // the matrix can contain null values ( usually these mean a similarity value of 0. ) 
			double mySim = myAlignment.getSimilarity();
			System.out.println("Similarity between " + source + " and " + target + " is " + mySim + " in the input matcher.");
		} else {
			System.out.println("Similarity between " + source + " and " + target + " is not defined in the input matcher.");
		}
		
		// do stuff
		
		return null;
	}
}
