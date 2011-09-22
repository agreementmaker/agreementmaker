package am.app.mappingEngine.boosting;

import java.util.ArrayList;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;

/**
 * This matcher takes another matcher as input and increases the similarities between reciprocal best matches by a factor.
 * @author cpesquita
 *
 */
public class BestMatchBoosting extends AbstractMatcher{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6613515980650628944L;

	/**
	 * 
	 */

	private AbstractMatcher input;



	public BestMatchBoosting() {
		super();
		needsParam = true;
	}

	protected void initializeVariables() {
		super.initializeVariables();
		minInputMatchers = 1;

	}

	@Override
	protected void beforeAlignOperations() throws Exception{
		super.beforeAlignOperations();
		if( inputMatchers.size() != 1 ) {
			throw new RuntimeException("Best Match Boosting needs to have one input matcher.");
		}

		input = inputMatchers.get(0);

		

	}

	@Override
	public void afterAlignOperations() {
		super.afterAlignOperations();
	

		// we must clone the AlignmentMatrix, otherwise we will be changing the alignment matrix of the input matcher.
		classesMatrix =input.getClassesMatrix().clone();  // clone
	
		BestMatchBoostingParameters bp = (BestMatchBoostingParameters)param;
		//double[][] similarityMatrix = matrix.getCopiedSimilarityMatrix(); //hungarian alg needs a double matrix

		int rows = classesMatrix.getRows();
		int cols = classesMatrix.getColumns();
		ArrayList<Mapping> bestmatchesRows = new ArrayList<Mapping>();
		ArrayList<Mapping> bestmatchesCols = new ArrayList<Mapping>();

		for(int i=0;i<rows;i++){
			Mapping m=classesMatrix.getRowMaxValues(i, 1)[0];
			bestmatchesRows.add(m);
		}
		for(int j=0;j<cols;j++){
			Mapping m=classesMatrix.getColMaxValues(j, 1)[0];
			bestmatchesCols.add(m);
		}

		for(int i=0; i<bestmatchesRows.size();i++){
			for(int j=0; j<bestmatchesCols.size();j++){
				if(	bestmatchesRows.get(i).getEntity1()!=null && bestmatchesRows.get(i).getEntity2()!=null && bestmatchesCols.get(j).getEntity2()!=null && bestmatchesCols.get(j).getEntity1()!=null ){
					if(bestmatchesRows.get(i).equals(bestmatchesCols.get(j))){


						Mapping newMap = classesMatrix.get(i, j);
						double newSim=newMap.getSimilarity()*bp.boostPercent;
						if(newSim>1.0)
							newSim=1.0;
						newMap.setSimilarity(newSim);
						classesMatrix.set(i, j, newMap);
					}

				}
			}
		}


	}

	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new BestMatchBoostingParametersPanel();
		}
		return parametersPanel;
	}


}
