package am.extension.userfeedback.rankingStrategies;

import java.util.List;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;

public interface StrategyInterface {
	
	public List<Mapping> rank();

	
}
