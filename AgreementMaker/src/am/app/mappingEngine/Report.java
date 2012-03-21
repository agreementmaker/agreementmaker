package am.app.mappingEngine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Report {
	private List<AbstractMatcher> matchers;
	private Map<String,List<Double>> similarities;
	private Set<String> solutions;
	
	public Report(){
		similarities = new HashMap<String, List<Double>>();
	}
	
	public List<AbstractMatcher> getMatchers() {
		return matchers;
	}
	
	public void setMatchers(List<AbstractMatcher> matchers) {
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
