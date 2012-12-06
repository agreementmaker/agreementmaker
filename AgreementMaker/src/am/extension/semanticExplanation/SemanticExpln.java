package am.extension.semanticExplanation;

import java.util.List;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;

public class SemanticExpln {
	

	private ExplanationNode[][] explanationMatrix;
	private static SemanticExpln instance = null;

	
	private void SemanticExpln(){

	}
	 
	  public static SemanticExpln getInstance(){
		    if(instance==null){
		       instance = new SemanticExpln();
		      }
		      return instance;
	  }
	/**
	 *  Finds the Most significant path for the whole ontology. 
	 * @param nodeMatrix
	 * @param alignmentMappings
	 * @return
	 */
	public static ExplanationNode findUniversalMostSignificantPath(ExplanationNode[][] nodeMatrix, Alignment<Mapping> alignmentMappings){
		ExplanationNode returnStructure = nodeMatrix[1][1].deepCopyStructure();
//		returnStructure
		for(Mapping m:alignmentMappings){
			ExplanationNode explanationNode = nodeMatrix[m.getEntity1().getIndex()][m.getEntity2().getIndex()];
			List<ExplanationNode> significantPathForNode = ExplanationNode.findMostSignificantPath(explanationNode);

			for(ExplanationNode node:significantPathForNode){
				returnStructure.addCountIntelligently(node);
			}
		}
		
//		returnStructure.describeExplanation();
		return returnStructure;
	}


	public ExplanationNode[][] getExplanationMatrix() {
		return this.explanationMatrix;
	}


	public void setExplanationMatrix(int row, int col) {
		this.explanationMatrix = new ExplanationNode[row][col];
	}
	
}