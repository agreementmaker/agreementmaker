package am.app.mappingEngine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import am.app.mappingEngine.instance.AbstractInstanceMatcher;

public class InstanceMatchingReport {
	private List<AbstractInstanceMatcher> matchers;
	private Map<String,List<Double>> similarities;
	private Set<String> solutions;
	
	public InstanceMatchingReport(){
		similarities = new LinkedHashMap<String, List<Double>>();
	}
	
	public List<AbstractInstanceMatcher> getMatchers() {
		return matchers;
	}
	
	public void setMatchers(List<AbstractInstanceMatcher> matchers) {
		this.matchers = matchers;
	}
	
	public void putSim(String key, List<Double> value){
		//System.out.println(key);
		similarities.put(key, value);
	}
	
	public List<Double> getSim(String key){
		return similarities.get(key);
	}
	
	public Set<String> keySet(){
		return similarities.keySet();
	}
	
	public String printTable(){
		StringBuffer sb = new StringBuffer();
		for (AbstractMatcher matcher : matchers) {
			sb.append(matcher.getName() + "\t");
		}
		sb.append("\n");
		
		Set<String> keys = similarities.keySet();
		for (String string : keys) {
			sb.append(string).append("\t");
			List<Double> sims = similarities.get(string);
			for (Double sim : sims) {
				sb.append(sim).append("\t");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public void setSolutions(Set<String> solutions) {
		this.solutions = solutions;
	}
	
	public Set<String> getSolutions() {
		return solutions;
	}
}
