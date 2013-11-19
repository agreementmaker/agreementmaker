package am.evaluation.disagreement.variance;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;

public class VarianceDisagreementComparator implements Comparator<Mapping> {

	private final List<AbstractMatcher> matchersToConsider;
	
	public VarianceDisagreementComparator(List<AbstractMatcher> matchers) {
		this.matchersToConsider = matchers;
	}
	
	
	@Override
	public int compare(Mapping o1, Mapping o2) {
		
		Vector<Double> o1SimilarityValues = new Vector<Double>();
		for( int k = 0; k < matchersToConsider.size(); k++ ) {
			o1SimilarityValues.add(matchersToConsider.get(k).getClassesMatrix().getSimilarity(o1.getSourceKey(), o1.getTargetKey()));
		}
		
		double o1Disagreement = VarianceDisagreement.computeVariance(o1SimilarityValues);
		
		Vector<Double> o2SimilarityValues = new Vector<Double>();
		for( int k = 0; k < matchersToConsider.size(); k++ ) {
			o2SimilarityValues.add(matchersToConsider.get(k).getClassesMatrix().getSimilarity(o2.getSourceKey(), o2.getTargetKey()));
		}
		
		double o2Disagreement = VarianceDisagreement.computeVariance(o2SimilarityValues);
		
		double diff = o1Disagreement - o2Disagreement;
		
		if( diff > 0d ) return 1;
		if( diff < 0d ) return -1;
		return 0;	
	}
	
	


}
