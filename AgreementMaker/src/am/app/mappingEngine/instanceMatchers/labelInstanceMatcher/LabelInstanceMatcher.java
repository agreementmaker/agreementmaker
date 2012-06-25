package am.app.mappingEngine.instanceMatchers.labelInstanceMatcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.mappingEngine.StringUtil.StringMetrics;
import am.app.mappingEngine.instanceMatcher.LabelUtils;
import am.app.mappingEngine.instanceMatchers.BaseInstanceMatcher;
import am.app.ontology.instance.Instance;
import am.app.similarity.StringSimilarityMeasure;


public class LabelInstanceMatcher extends BaseInstanceMatcher {
	
	private static final long serialVersionUID = -8556251076642309404L;

	private Logger log = Logger.getLogger(LabelInstanceMatcher.class);
	
	/**
	 * Keep track of the last processed source URI, so that on the next
	 * iteration we won't have to process those labels again.
	 */
	private String lastSourceURI;

	/** The processed labels of the lastSourceURI. */
	private List<String> lastProcessedSourceLabels;

	private StringSimilarityMeasure ssm;
	
	public LabelInstanceMatcher(LabelInstanceMatcherParameters param) {
		super(param);
	}
		
	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		
		// 1) initialize our string similarity measure
		if( ssm == null ) {
			StringMetrics metric = ((LabelInstanceMatcherParameters)param).metric;
			ssm = metric.getMeasure();
		}
		
		List<String> processedSourceLabels;
		
		// 2) process the source labels
		if( lastSourceURI == null || !lastSourceURI.equals(source.getUri()) ) {
			List<String> sourceLabels = source.getProperty(Instance.INST_LABEL);
			
			processedSourceLabels = new ArrayList<String>(sourceLabels.size());
			
			for(String currentLabel : sourceLabels) {
				processedSourceLabels.add(processLabel(currentLabel, source.getTypeValue(), false));
			}
		
			// save our processing work for the next invocation of the method
			lastSourceURI = source.getUri();
			lastProcessedSourceLabels = processedSourceLabels;
		}
		else {
			// reuse our last processing of the source labels
			processedSourceLabels = lastProcessedSourceLabels;
		}
				
		// 3) process the target labels
		List<String> targetLabels = target.getProperty(Instance.INST_LABEL);
		List<String> processedTargetLabels = new ArrayList<String>(targetLabels.size());

		if(targetLabels == null || targetLabels.isEmpty()) {
			targetLabels = LabelUtils.getLabelsFromStatements(target);
		}
		
		//targetLabel = LabelUtils.processOrganizationLabel(targetLabel);
		for(String currentLabel : targetLabels) {
			processedTargetLabels.add(processLabel(currentLabel, source.getTypeValue(), true));
		}
		
		//System.out.println(source.getUri() + "||" + target.getUri() + "  " + sourceLabel + " | " + targetLabel);
		
		
		// 4) compute the similarity
		double sim = computeStringSimilarity(processedSourceLabels, processedTargetLabels);
		
		log.debug("labelSim: " + sim);
		
		List<String> aliases = target.getProperty(Instance.INST_ALIAS);
		if(aliases == null) aliases = new ArrayList<String>();
		aliases.addAll(LabelUtils.getAliasesFromStatements(target));
		
		//System.out.println(aliases);
		
		// 5) check for aliases
		if(aliases != null){
			double max = sim;
			double curr = 0.0d;
			for (int i = 0; i < aliases.size(); i++) {
				for( String currentLabel : processedSourceLabels ) {
					curr = ssm.getSimilarity(currentLabel, aliases.get(i));
				}

				max = Math.max(curr, max);
				
			}
			if(max > sim) sim = (max + sim) / 2.0d;
		}
		return sim;
	}
	
	private double computeStringSimilarity(List<String> source,
			List<String> target) {
				
		double bestStringSimilarity = 0.0d;
		for( String sourceLabel : source ) {
			for( String targetLabel : target ) {
				double currentSim = ssm.getSimilarity(sourceLabel, targetLabel);
				bestStringSimilarity = Math.max(currentSim, bestStringSimilarity);
			}
		}
		
		return bestStringSimilarity;
	}

	public static String processLabel(String label, String type, boolean processComma){
		
		String processedLabel;
		
		if(type == null) {
			processedLabel = LabelUtils.processLabel(label);
		}
		else if(type.toLowerCase().endsWith("organization")) {
			processedLabel = LabelUtils.processOrganizationLabel(label);
		}
		else if(type.toLowerCase().endsWith("person")) {
			processedLabel = LabelUtils.processPersonLabel(label);
		}
		else if(type.toLowerCase().endsWith("location")){
			processedLabel = LabelUtils.processLocationLabel(label);
			//System.out.println("processing location label");
			//label = label.replace("(","");
			//label = label.replace(")","");
		}
		else{
			processedLabel = LabelUtils.processLabel(label);			
		}
		
		if(processComma) {
			if(processedLabel.contains(",")){
				String[] returnSplit = processedLabel.split(",");
				return returnSplit[0];
			}
		}
		
		return processedLabel;
	}	
	

	
	@Override
	public String getName() {
		return "Label Instance Matcher";
	}
}
