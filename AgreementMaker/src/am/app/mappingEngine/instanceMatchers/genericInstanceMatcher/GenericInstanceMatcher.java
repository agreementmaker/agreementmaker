package am.app.mappingEngine.instanceMatchers.genericInstanceMatcher;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Report;
import am.app.mappingEngine.instanceMatchers.BaseInstanceMatcher;
import am.app.mappingEngine.instanceMatchers.combination.CombinationFunction;
import am.app.ontology.instance.Instance;

/**
 * 
 * @author federico
 *
 */
public class GenericInstanceMatcher extends BaseInstanceMatcher{
	
	private static final long serialVersionUID = -5745262888574700843L;

	private List<AbstractMatcher> matchers = new ArrayList<AbstractMatcher>();
	
	private boolean generateReport = true;
		
	CombinationFunction combination;
	
	
	public GenericInstanceMatcher(){
		
	}
	
	@Override
	public void matchStart() {
		if(generateReport){
			System.out.println("init instanceMatchingReport");
			instanceMatchingReport = new Report();
			instanceMatchingReport.setMatchers(matchers);
		}
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
		
		if(generateReport){
			instanceMatchingReport.putSim(source.getUri() + "||" + target.getUri(), similarities);
		}
		
		return combination.combine(similarities);
	}
	
	public void setCombination(CombinationFunction combination) {
		this.combination = combination;
	}
	
	@Override
	public String getName() {
		return "Generic Instance Matcher";
	}
}
