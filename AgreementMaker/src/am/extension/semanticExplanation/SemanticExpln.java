package am.extension.semanticExplanation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;

public class SemanticExpln {
	

	private ExplanationNode[][] explanationMatrix;
	
	
	
	public SemanticExpln(int row, int col) {
		super();
		this.explanationMatrix = new ExplanationNode[row][col];
	}


	/**
	 *  Finds the Most significant path for the whole ontology. 
	 * @param nodeMatrix
	 * @param alignmentMappings
	 * @return
	 */
	public static List<ExplanationNode> findUniversalMostSignificantPath(ExplanationNode[][] nodeMatrix, Alignment<Mapping> alignmentMappings){
		Map<ExplanationNode,Integer> trafficMap = new HashMap<ExplanationNode, Integer>();
		for(Mapping m:alignmentMappings){
			ExplanationNode explanationNode = nodeMatrix[m.getEntity1().getIndex()][m.getEntity2().getIndex()];
			List<ExplanationNode> significantPathForNode = ExplanationNode.findMostSignificantPath(explanationNode);
			for(ExplanationNode node:significantPathForNode){
				if(!trafficMap.containsKey(node)){
					trafficMap.put(node, 1);
				}
				else{
					int newValue = trafficMap.get(node) + 1;
					trafficMap.put(node,newValue);
				}
			}
		}
		System.out.println("The final significantPathValues are:");
		for(ExplanationNode key: trafficMap.keySet()){
			System.out.println("Key= "+key.description+" value="+trafficMap.get(key));
		}
		return null;
	}




	public ExplanationNode[][] getExplanationMatrix() {
		return explanationMatrix;
	}


	public void setExplanationMatrix(ExplanationNode[][] explanationMatrix) {
		this.explanationMatrix = explanationMatrix;
	}
	
}