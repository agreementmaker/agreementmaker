package am.app.mappingEngine.qualityEvaluation.metrics.ufl;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import static am.evaluation.disagreement.variance.VarianceComputation.computeVariance;

/**
 * Compute the disagreement between matching algorithms given their individual
 * similarity matrices. The constructor takes only one kind of matrix, either
 * the classes matrices or the properties matrices. You must instantiate a
 * separate object for each type of matrices.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 * 
 */
public class VarianceMatcherDisagreement extends AbstractQualityMetric {

	private SimilarityMatrix[] matrices;
	
	public VarianceMatcherDisagreement(List<SimilarityMatrix> matrices) {
		super();
		this.matrices = matrices.toArray(new SimilarityMatrix[0]);
	}
	
	/**
	 * @param type
	 *            This parameter is ignored.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) {
		// create signature vector
		double[] signatureVector = new double[matrices.length];
		for(int k = 0; k < matrices.length; k++) {
			signatureVector[k] = matrices[k].getSimilarity(i, j);
		}
		
		// return the computed variance
		// because variance of similarity values can be a maximum of 0.25, we
		// multiply by 4 to get a metric from 0 to 1.0
		return 4 * computeVariance(signatureVector);
	}
}
