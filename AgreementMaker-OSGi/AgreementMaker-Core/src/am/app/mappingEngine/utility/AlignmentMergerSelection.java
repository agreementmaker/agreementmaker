package am.app.mappingEngine.utility;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.AbstractSelectionAlgorithm;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public class AlignmentMergerSelection extends AbstractSelectionAlgorithm {

	@Override
	public String getName() {
		return "Alignment Merger Selection";
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	protected Alignment<Mapping> oneToOneMatching(SimilarityMatrix matrix) {
		
		Alignment<Mapping> newAlignment = new Alignment<Mapping>(params.inputResult.getSourceOntology().getID(), 
				params.inputResult.getTargetOntology().getID());
		
		if( params.matchingTask != null ) {
			if( params.matchingTask.inputMatchingTasks == null )
				throw new RuntimeException("No input matching tasks have been set for this matching task.");
			
			for( MatchingTask inputTask : params.matchingTask.inputMatchingTasks ) {
				if( matrix.getAlignType() == alignType.aligningClasses ) {
					
					Alignment<Mapping> classesAlignment = inputTask.selectionResult.getClassAlignmentSet();
					for( Mapping m : classesAlignment ) {
						if( !newAlignment.contains(m) ) {
							newAlignment.add(m);
						}
					}					
				}
				else if( matrix.getAlignType() == alignType.aligningProperties ) {
					
					Alignment<Mapping> propertiesAlignment = inputTask.selectionResult.getPropertyAlignmentSet();
					for( Mapping m : propertiesAlignment ) {
						if( !newAlignment.contains(m) ) {
							newAlignment.add(m);
						}
					}
				}
			} // for
		}
		else {
			throw new RuntimeException("No matching task has been set in the selection parameters.");
		}
		
		return newAlignment;
		
	}

}
