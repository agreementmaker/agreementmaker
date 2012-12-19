package am.app.mappingEngine.oneToOneSelection;

import java.util.Collection;
import java.util.Iterator;

import am.app.mappingEngine.AbstractSelectionAlgorithm;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SelectionResult;
import am.app.mappingEngine.SimilarityMatrix;

public class MwbmSelection extends AbstractSelectionAlgorithm {

	@Override
	public SelectionResult getResult() {
		return result;
	}

	@Override
	public String getName() {
		return "Maximum Weighted Bi-partite Matching Selection";
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	protected Alignment<Mapping> oneToOneMatching(SimilarityMatrix matrix) {
		Alignment<Mapping> aset = new Alignment<Mapping>(params.inputResult.getSourceOntology().getID(), params.inputResult.getTargetOntology().getID());
		double[][] similarityMatrix = matrix.getCopiedSimilarityMatrix();  // in order of our selection algorithm to be scalable, this has to change! we cannot allocate an NxM matrix if N and M are large! - Cosmin.
		MaxWeightBipartiteMatching<Integer> mwbm = new MaxWeightBipartiteMatching<Integer>(similarityMatrix, params.threshold);
		Collection<MappingMWBM<Integer>> mappings = mwbm.execute();
		Iterator<MappingMWBM<Integer>> it = mappings.iterator();
		Mapping a;
		MappingMWBM<Integer>  m;
		while(it.hasNext()){
			if( this.isCancelled() ) { return null; }
			m = it.next();
			a = matrix.get(m.getSourceNode(), m.getTargetNode());
			if( a != null ) aset.add(a);
		}
		
		/* CODE FOR THE HUNGARIAN
		 * 		//we can use the hungarian algorithm which provide the optimal solution in polynomial time
		//the hungarian can be used to compute the maxim 1-1 matching or the minimum one, and ofc we need the maximum
		double[][] similarityMatrix = matrix.getSimilarityMatrix(); //hungarian alg needs a double matrix
		double[][] cuttedMatrix = Utility.cutMatrix(similarityMatrix, threshold); //those similarity values lower than the threshold cannot be selected so we remove them from the matrix setting them to 0
		int[][] assignments = HungarianAlgorithm.hgAlgorithm(cuttedMatrix, HungarianAlgorithm.MAX_SUM_TYPE);
		
		//the array keeps the assignments
		//if the rows are <= cols assignments are [row][col] else they are [col][row]
		for(int i = 0; i < assignments.length; i++) {
			int row = assignments[i][0];
			int col = assignments[i][1];
			if(matrix.getRows() > matrix.getColumns()) {
				row = assignments[i][1];
				col = assignments[i][0];
			}
			if(row != -1 && col != -1) { //if the node was matched
				Alignment a = matrix.get(row, col);
				//i still need to check this even if similarity values have been cutted because the hungarian algorithm can select also values equals to 0 if there is nothing else
				if(a.getSimilarity() >= threshold)
					aset.addAlignment(a);
			}
		}
		*/
		
		return aset;
	}

}
