package am.app.mappingEngine.instanceMatchers;

import java.util.List;

import org.apache.log4j.Logger;

import am.app.mappingEngine.StringUtil.StringMetrics;
import am.app.ontology.instance.Instance;


public class LabelInstanceMatcher extends BaseInstanceMatcher {

	Logger log = Logger.getLogger(LabelInstanceMatcher.class);
	
	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		double sim;
		
		String sourceLabel = source.getSingleValuedProperty("label");
				
		log.debug("sourceLabel: " + sourceLabel);
		sourceLabel = processLabel(sourceLabel);
		log.debug("sourceLabel: " + sourceLabel);
				
		String targetLabel;
		targetLabel = target.getSingleValuedProperty("label");
		
		log.debug("targetLabel: " + targetLabel);
		
		sim = StringMetrics.AMsubstringScore(sourceLabel, targetLabel);
		
		log.debug("labelSim: " + sim);
		
		List<String> aliases = target.getProperty("alias");
		if(aliases != null){
			double max = sim;
			double curr;
			for (int i = 0; i < aliases.size(); i++) {
				curr = StringMetrics.AMsubstringScore(sourceLabel, aliases.get(i));
				if(curr > max){
					//System.out.println("An alias weighs more than the label");
					max = curr;
				}
			}
			if(max > sim) sim = (max + sim) / 2;
		}
		return sim;
	}
	
	public static String processLabel(String label){
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
		if(label.contains(",")){
			String[] splitted = label.split(",");
			return splitted[1].trim() + " " + splitted[0].trim();
		}
		return label; 
	}
}
