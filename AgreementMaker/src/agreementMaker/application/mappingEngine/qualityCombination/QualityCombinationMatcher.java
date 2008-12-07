package agreementMaker.application.mappingEngine.qualityCombination;

import java.util.ArrayList;

import javax.swing.tree.TreeNode;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.AlignmentMatrix;
import agreementMaker.application.ontology.Node;
import agreementMaker.userInterface.vertex.Vertex;

public class QualityCombinationMatcher extends AbstractMatcher {

	
	public QualityCombinationMatcher() {
		super();
		

		needsParam = true; // we need to set the MCP before running DSI
		
		
		minInputMatchers = 2;
		maxInputMatchers = ANY_INT;
		parametersPanel = new QualityCombinationParametersPanel();
		
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
    

	protected Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		return null;
	}

	
}
