package am.app.feedback.matchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import am.app.feedback.FilteredAlignmentMatrix;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceParameters;
import am.app.mappingEngine.oneToOneSelection.MappingMWBM;
import am.app.mappingEngine.oneToOneSelection.MaxWeightBipartiteMatching;
import am.app.ontology.Node;
import am.app.ontology.TreeToDagConverter;

public class ExtrapolatingDSI extends DescendantsSimilarityInheritanceMatcher {
	
    /**
     * Everything is the same of the DSI, except that if a cell has been filtered out, it is set as TRUE in the boolean matrix 
       so that it won't be modified.
     */
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
