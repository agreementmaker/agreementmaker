package am.app.mappingEngine.utility;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.AbstractSelectionAlgorithm;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public class CopySelection extends AbstractSelectionAlgorithm {

	@Override
	public String getName() {
		return "Copy Selection";
	}

	@Override
	protected Alignment<Mapping> oneToOneMatching(SimilarityMatrix matrix) {
		
		if( params.matchingTask != null ) {
			
			if( matrix.getAlignType() == alignType.aligningClasses ) {
				return params.matchingTask.matchingAlgorithm.getClassAlignmentSet();					
			}
			else if( matrix.getAlignType() == alignType.aligningProperties ) {
				return params.matchingTask.matchingAlgorithm.getPropertyAlignmentSet();
			}
		}
		else {
			throw new RuntimeException("No matching task has been set in the selection parameters.");
		}
		
		return null;
	}

	
	
}
