package am.evaluation.disagreement.variance;

import static am.evaluation.disagreement.variance.VarianceComputation.computeVariance;

import java.util.Comparator;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;

public class VarianceDisagreementComparator implements Comparator<Mapping> {

	private final AbstractMatcher[] matchersToConsider;
	
	public VarianceDisagreementComparator(List<AbstractMatcher> matchers) {
		this.matchersToConsider = matchers.toArray(new AbstractMatcher[0]);
	}
	
	@Override
	public int compare(Mapping o1, Mapping o2) {
		
		double[] o1SimilarityValues = new double[matchersToConsider.length];
		for( int k = 0; k < matchersToConsider.length; k++ ) {
			o1SimilarityValues[k] = matchersToConsider[k].getClassesMatrix().getSimilarity(o1.getSourceKey(), o1.getTargetKey());
		}
		
		double o1Disagreement = computeVariance(o1SimilarityValues);
		
		double[] o2SimilarityValues = new double[matchersToConsider.length];
		for( int k = 0; k < matchersToConsider.length; k++ ) {
			o2SimilarityValues[k] = matchersToConsider[k].getClassesMatrix().getSimilarity(o2.getSourceKey(), o2.getTargetKey());
		}
		
		double o2Disagreement = computeVariance(o2SimilarityValues);
		
		double diff = o1Disagreement - o2Disagreement;
		
		if( diff > 0d ) return 1;
		if( diff < 0d ) return -1;
		return 0;	
	}
	
	


}
