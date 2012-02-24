package am.app.mappingEngine.instanceMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.instanceMatchers.combination.CombinationFunction;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.ScoredInstance;
import am.app.ontology.instance.ScoredInstanceComparator;

public class GenericInstanceMatcher extends BaseInstanceMatcher{
	
	private List<AbstractMatcher> matchers = new ArrayList<AbstractMatcher>();
		
	CombinationFunction combination;
	
	
	public GenericInstanceMatcher(){
				
	}
	
	public void addInstanceMatcher(AbstractMatcher matcher){
		matchers.add(matcher);
	}
	
	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		
		List<Double> similarities = new ArrayList<Double>();
		for (AbstractMatcher matcher : matchers) {
			similarities.add(matcher.instanceSimilarity(source, target));
		}		
		
		return combination.combine(similarities);
	}
	
	public void setCombination(CombinationFunction combination) {
		this.combination = combination;
	}
}
