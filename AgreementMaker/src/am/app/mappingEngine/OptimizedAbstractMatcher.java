package am.app.mappingEngine;

import java.util.ArrayList;

import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.ontology.Node;

//If a matcher is provided as input to this type of matcher
//the matcher will only try to map those concepts that have not been mapped yet by the matcher in input
//according to the selected cardinality
//if no matchers are provided in input to this matcher, it will run as generic matcher (quadratic number of comparisons)
//For this reason the MinInputMatcher must be 0 and the MaxInputMatcher has to be 1.
public class OptimizedAbstractMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = 1852460707983840543L;

	public OptimizedAbstractMatcher() {
		super();
		maxInputMatchers = 1;
		minInputMatchers = 0;
	}
	
	
	@Override
    protected SimilarityMatrix alignProperties(ArrayList<Node> sourcePropList, ArrayList<Node> targetPropList) throws Exception {
    	if(inputMatchers.size() == 0){ //run as a generic matcher who maps all concepts by doing a quadratic number of comparisons
			return super.alignProperties(sourcePropList, targetPropList);
		}
    	else{
    		return alignUnmappedNodes(sourcePropList, targetPropList, inputMatchers.get(0).getPropertiesMatrix(), inputMatchers.get(0).getPropertyAlignmentSet(), alignType.aligningProperties);
    	}
	}

	@Override
	protected SimilarityMatrix alignClasses(ArrayList<Node> sourceClassList, ArrayList<Node> targetClassList)  throws Exception{
    	if(inputMatchers.size() == 0){ //run as a generic matcher who maps all concepts by doing a quadratic number of comparisons
			return super.alignClasses(sourceClassList, targetClassList);
		}
    	else{
    		return alignUnmappedNodes(sourceClassList, targetClassList, inputMatchers.get(0).getClassesMatrix(), inputMatchers.get(0).getClassAlignmentSet(), alignType.aligningClasses);
    	}
	}
	
	@Override
    protected SimilarityMatrix alignUnmappedNodes(ArrayList<Node> sourceList, ArrayList<Node> targetList, SimilarityMatrix inputMatrix,
			Alignment<Mapping> inputAlignmentSet, alignType typeOfNodes) throws Exception {
    	
    	MappedNodes mappedNodes = new MappedNodes(sourceList, targetList, inputAlignmentSet, getMaxSourceAlign(), getMaxTargetAlign());
    	SimilarityMatrix matrix = new ArraySimilarityMatrix(sourceList.size(), targetList.size(), typeOfNodes, relation);
		Node source;
		Node target;
		Mapping alignment; //Temp structure to keep sim and relation between two nodes, shouldn't be used for this purpose but is ok
		Mapping inputAlignment;
		for(int i = 0; i < sourceList.size(); i++) {
			source = sourceList.get(i);
			for(int j = 0; j < targetList.size(); j++) {
				target = targetList.get(j);
				
				if( !this.isCancelled() ) {
					//if both nodes have not been mapped yet enough time
					//we map them regularly
					if(!mappedNodes.isSourceMapped(source) && !mappedNodes.isTargetMapped(target)){
						alignment = alignTwoNodes(source, target, typeOfNodes); 
					}
					//else we take the alignment that was computed from the previous matcher
					else if (mappedNodes.isSourceMapped(source) && mappedNodes.isTargetMapped(target)){
						inputAlignment = inputMatrix.get(i, j);
						if(inputAlignment == null)
							alignment = null;
						else
							alignment = new Mapping(inputAlignment.getEntity1(), inputAlignment.getEntity2(), inputAlignment.getSimilarity(), inputAlignment.getRelation());
					}
					else{
						alignment = null;
					}
					if(alignment == null)
						alignment = new Mapping(source, target, 0.0d, MappingRelation.SUBSET);
					matrix.set(i,j,alignment);
					if( isProgressDisplayed() ) stepDone(); // we have completed one step
				}
				else { return matrix; }
			}
			if( isProgressDisplayed() ) updateProgress(); // update the progress dialog, to keep the user informed.
		}
		return matrix;
	}
    
	@Override
    protected Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes) throws Exception {
		//TO BE IMPLEMENTED BY THE ALGORITHM, THIS IS JUST A FAKE ABSTRACT METHOD
		double sim;
		MappingRelation rel = MappingRelation.EQUIVALENCE;
		if(source.getLocalName().equals(target.getLocalName())) {
			sim = 1;
		}
		else {
			sim = 0;
		}
		return new Mapping(source, target, sim, rel);
	}

}
