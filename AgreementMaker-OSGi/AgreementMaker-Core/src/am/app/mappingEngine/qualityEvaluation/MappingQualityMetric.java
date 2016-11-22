package am.app.mappingEngine.qualityEvaluation;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.utility.parameters.HasParameters;

/**
 * Compute the quality of a mapping or alignment.
 */
public interface MappingQualityMetric extends HasParameters {
	/**
	 * @param type which matrix to compute the quality of
	 * @param i the row index of the source concept row in the similarity matrix
	 * @param j the column index of the target concept in the similarity matrix
	 * @return the quality of the mapping between the source concept and the target concept
	 */
	double getQuality(alignType type, int i, int j);

	QualityEvaluationData getQuality(AbstractMatcher matcher) throws Exception;
}
