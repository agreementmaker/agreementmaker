package am.extension.feedback.matchers;

import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import am.extension.feedback.FilteredAlignmentMatrix;

public class ExtrapolatingDSI extends DescendantsSimilarityInheritanceMatcher {
	
	private static final long serialVersionUID = 8826757190647599700L;

	/**
     * Everything is the same of the DSI, except that if a cell has been filtered out, it is set as TRUE in the boolean matrix 
       so that it won't be modified.
     */
	@Override
	protected void initBooleanMatrix(SimilarityMatrix input) {
		super.initBooleanMatrix(input);
		if(input instanceof FilteredAlignmentMatrix) {
			FilteredAlignmentMatrix fInput = (FilteredAlignmentMatrix)input;
			for(int i = 0; i < input.getRows(); i++){
				for(int j = 0; j < input.getColumns(); j++){
					if(fInput.isCellFiltered(i, j)){
						isComputedAlready[i][j] = true;
					}
				}
			}
		}
		else{
			throw new RuntimeException("Extrapolating DSI should be running on a matcher that adopts FilteredAlignmentMatrices.");
		}
	}
	
}
