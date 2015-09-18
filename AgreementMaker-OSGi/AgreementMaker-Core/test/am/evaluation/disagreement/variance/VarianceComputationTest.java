package am.evaluation.disagreement.variance;

import org.junit.Test;
import static am.evaluation.disagreement.variance.VarianceComputation.computeVariance;
import static org.junit.Assert.*;

public class VarianceComputationTest {

	private Double[] similarityValues = { 0.6, 0.3, 0.4, 0.88 };

	@Test
	public void testVarianceComputation() {
		final Double variance = computeVariance(similarityValues);
		assertEquals("Variance must be correct", 0.049075, variance, 0.00000001);
	}
}
