package am.app.mappingEngine.boosting;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;

/**
 * This matcher takes another matcher as input and increases the similarities between reciprocal best matches by a factor.
 * @author cpesquita
 * @date Sept 22, 2011
 */
public class BestMatchBoosting extends AbstractMatcher {

	private static final long serialVersionUID = -6613515980650628944L;

	public BestMatchBoosting() { super(); }

	@Override
	protected void initializeVariables() {
		super.initializeVariables();
		minInputMatchers = 1;
		maxInputMatchers = 1;
		needsParam = true;
	}

	@Override
	protected SimilarityMatrix alignNodesOneByOne(List<Node> sourceList,
			List<Node> targetList, alignType typeOfNodes) throws Exception {
	
		BestMatchBoostingParameters bp = (BestMatchBoostingParameters)param;

		SimilarityMatrix matrix = null;
		
		// we must clone the AlignmentMatrix, otherwise we will be changing the alignment matrix of the input matcher.
		if( bp.deepCopy ) { // clone
			if( typeOfNodes == alignType.aligningClasses ) {
				matrix = inputMatchers.get(0).getClassesMatrix().clone();
			}
			else { 
				matrix = inputMatchers.get(0).getPropertiesMatrix().clone();
			}
		} else { // the user doesn't mind if we edit the matrix of the original matcher
			if( typeOfNodes == alignType.aligningClasses ) {
				matrix = inputMatchers.get(0).getClassesMatrix();
			}
			else { 
				matrix = inputMatchers.get(0).getPropertiesMatrix();
			}
		}
	
		//double[][] similarityMatrix = matrix.getCopiedSimilarityMatrix(); //hungarian alg needs a double matrix

		int rows = matrix.getRows();
		int cols = matrix.getColumns();
		ArrayList<Mapping> bestmatchesRows = new ArrayList<Mapping>();
		ArrayList<Mapping> bestmatchesCols = new ArrayList<Mapping>();

		for(int i=0;i<rows;i++){
			Mapping m=matrix.getRowMaxValues(i, 1)[0];
			bestmatchesRows.add(m);
		}
		for(int j=0;j<cols;j++){
			Mapping m=matrix.getColMaxValues(j, 1)[0];
			bestmatchesCols.add(m);
		}

		for(int i=0; i<bestmatchesRows.size();i++){
			for(int j=0; j<bestmatchesCols.size();j++){
				if(	bestmatchesRows.get(i).getEntity1()!=null && bestmatchesRows.get(i).getEntity2()!=null && bestmatchesCols.get(j).getEntity2()!=null && bestmatchesCols.get(j).getEntity1()!=null ){
					if(bestmatchesRows.get(i).equals(bestmatchesCols.get(j))){


						Mapping newMap = matrix.get(i, j);
						double newSim=newMap.getSimilarity()*bp.boostPercent;
						if(newSim>1.0)
							newSim=1.0;
						newMap.setSimilarity(newSim);
						matrix.set(i, j, newMap);
					}

				}
				
				if( isProgressDisplayed() ) {
					stepDone();
					updateProgress();
				}
			}
		}

		return matrix;
	}

	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new BestMatchBoostingParametersPanel();
		}
		return parametersPanel;
	}


}
