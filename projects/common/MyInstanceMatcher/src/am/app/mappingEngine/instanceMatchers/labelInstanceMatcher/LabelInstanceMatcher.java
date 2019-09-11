package am.app.mappingEngine.instanceMatchers.labelInstanceMatcher;

import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;

import am.app.mappingEngine.StringUtil.ISub;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.similarity.StringMetrics;
import am.app.mappingEngine.instanceMatcher.LabelUtils;
import am.app.mappingEngine.instanceMatchers.BaseInstanceMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.ontology.instance.Instance;


public class LabelInstanceMatcher extends BaseInstanceMatcher {

	Logger log = Logger.getLogger(LabelInstanceMatcher.class);
	
	String metric;
	
	public LabelInstanceMatcher(){
		metric = StringMetrics.SUB;
		if(param != null && (param instanceof LabelInstanceMatcherParameters))
			metric = ((LabelInstanceMatcherParameters) param).metric;
	}
		
	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		double sim;
		
		String sourceLabel = source.getSingleValuedProperty("label");
				
		log.debug("sourceLabel: " + sourceLabel);
		sourceLabel = processLabel(sourceLabel, source.getType());
		log.debug("sourceLabel: " + sourceLabel);
				
		String targetLabel;
		targetLabel = target.getSingleValuedProperty("label");
		
		log.debug("targetLabel: " + targetLabel);
		
		
		sim = computeStringSimilarity(sourceLabel, targetLabel);
		
		log.debug("labelSim: " + sim);
		
		List<String> aliases = target.getProperty("alias");
		if(aliases != null){
			double max = sim;
			double curr;
			for (int i = 0; i < aliases.size(); i++) {
				curr = computeStringSimilarity(sourceLabel, aliases.get(i));
				if(curr > max){
					//System.out.println("An alias weighs more than the label");
					max = curr;
				}
			}
			if(max > sim) sim = (max + sim) / 2;
		}
		return sim;
	}
	
	private double computeStringSimilarity(String source,
			String target) {
		double sim = 0.0;
		if(metric.equals(ParametricStringParameters.AMSUB)) {
			sim = StringMetrics.AMsubstringScore(source,target);
		}
		else if(metric.equals(ParametricStringParameters.AMSUB_AND_EDIT)) {
			Levenshtein lv = new Levenshtein();
			double lsim = lv.getSimilarity(source, target);
			double AMsim = StringMetrics.AMsubstringScore(source,target);
			sim = (0.65*AMsim)+(0.35*lsim); 
		}
		else if(metric.equals(ParametricStringParameters.EDIT)) {
			Levenshtein lv = new Levenshtein();
			sim = lv.getSimilarity(source, target);
		}
		else if(metric.equals(ParametricStringParameters.JARO)) {
			JaroWinkler jv = new JaroWinkler();
			sim =jv.getSimilarity(source, target);
		}
		else if(metric.equals(ParametricStringParameters.QGRAM)) {
			QGramsDistance q = new QGramsDistance();
			sim = q.getSimilarity(source, target);
		}
		else if(metric.equals(ParametricStringParameters.SUB)) {
			sim = StringMetrics.substringScore(source,target);
		}
		else if(metric.equals(ParametricStringParameters.ISUB)) {
			sim = ISub.getSimilarity(source,target);
		}
		return sim;
	}

	public static String processLabel(String label, String type){
		if(type == null) return label;
		
		if(type.toLowerCase().endsWith("organization"))
			return LabelUtils.processOrganizationLabel(label);
		
		else if(type.toLowerCase().endsWith("person"))
			return LabelUtils.processPersonLabel(label);
		
		else if(type.toLowerCase().endsWith("location")){
			return LabelUtils.processLocationLabel(label);
			//System.out.println("processing location label");
			//label = label.replace("(","");
			//label = label.replace(")","");
		}
		return label;
	}	
}
