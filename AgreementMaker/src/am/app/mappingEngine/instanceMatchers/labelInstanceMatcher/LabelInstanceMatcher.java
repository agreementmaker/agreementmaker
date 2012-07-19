package am.app.mappingEngine.instanceMatchers.labelInstanceMatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import am.app.mappingEngine.StringUtil.StringMetrics;
import am.app.mappingEngine.instance.EntityTypeMapper.EntityType;
import am.app.mappingEngine.instanceMatcher.LabelUtils;
import am.app.mappingEngine.instanceMatchers.BaseInstanceMatcher;
import am.app.ontology.instance.Instance;
import am.app.similarity.StringSimilarityMeasure;
import am.utility.StringUtility;

/**
 * An instance matching algorithm that looks only at the labels associated with
 * instances.
 * 
 * @author Federico Caimi
 * 			Initial implementation.
 * @author Cosmin Stroe
 * 			
 * 
 */
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

	/** Instantiated from {@link LabelInstanceMatcherParameters#metric} */
	private StringSimilarityMeasure ssm;
	
	// instance initializer to set the name and category of this matcher.
	// see http://stackoverflow.com/questions/1355810/how-is-an-instance-initializer-different-from-a-constructor
	{
		setProperty(PropertyKey.NAME, "Label Instance Matcher");
		setCategory(MatcherCategory.INSTANCE);
	}
	
	public LabelInstanceMatcher(LabelInstanceMatcherParameters param) {
		super(param);
		StringMetrics metric = ((LabelInstanceMatcherParameters)param).metric;
		ssm = metric.getMeasure();
	}
		
	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		
		List<String> processedSourceLabels;
		
		// 1) process the source labels (optimized for processing the 
		//    same source instance multiple times)
		if( lastSourceURI == null || !lastSourceURI.equals(source.getUri()) ) {
			// the last source instance is different from this source instance 
			Set<String> sourceLabels = source.getProperty(Instance.INST_LABEL);
			
			processedSourceLabels = new ArrayList<String>(sourceLabels.size());
			
			for(String currentLabel : sourceLabels) {
				processedSourceLabels.add(processLabel(currentLabel, source.getType(), false));
			}
		
			// save our processing work for the next invocation of the method
			lastSourceURI = source.getUri();
			lastProcessedSourceLabels = processedSourceLabels;
		}
		else {
			// reuse our last processing of the source labels
			processedSourceLabels = lastProcessedSourceLabels;
		}
				
		// 2) process the target labels
		Set<String> targetLabels = target.getProperty(Instance.INST_LABEL);
		List<String> processedTargetLabels = new ArrayList<String>(targetLabels.size());

		if(targetLabels == null || targetLabels.isEmpty()) {
			targetLabels = LabelUtils.getLabelsFromStatements(target);
		}
		
		for(String currentLabel : targetLabels) {
			processedTargetLabels.add(processLabel(currentLabel, target.getType(), true));
		}
		
		// 3) compute the similarity
		LabelInstanceMatcherParameters p = (LabelInstanceMatcherParameters) param;
		
		double sim = 0.0d;
		if( p.computeTypedSimilarity && source.getType() == target.getType()) {
			sim = computeTypedSimilarity(processedSourceLabels, processedTargetLabels, source.getType());			
		}
		else {
			sim = StringUtility.getMaxStringSimilarity(processedSourceLabels, processedTargetLabels, ssm);
		}
		log.debug("labelSim: " + sim);
		
		//System.out.println(aliases);
		
		// 4) check for aliases
		// TODO: include the aliases in the original labels.
		Set<String> aliases = target.getProperty(Instance.INST_ALIAS);
		if(aliases == null) aliases = new HashSet<String>();
		aliases.addAll(LabelUtils.getAliasesFromStatements(target));
		
		if(aliases != null){
			double max = sim;
			double curr = 0.0d;
			for (String currentAlias : aliases) {
				for( String currentLabel : processedSourceLabels ) {
					curr = ssm.getSimilarity(currentLabel, currentAlias);
				}

				max = Math.max(curr, max);
				
			}
			if(max > sim) sim = (max + sim) / 2.0d;
		}
		return sim;
	}

	/**
	 * This method implements type specific similarity computation for labels.
	 * If there isn't a type-specific way of computing similarity, it resorts to
	 * the generic way of computing the label similarity.
	 */
	private double computeTypedSimilarity(
			List<String> processedSourceLabels,
			List<String> processedTargetLabels,
			EntityType type) {

		if( type == EntityType.PERSON ) {
			// a custom string similarity measure for person names
			StringSimilarityMeasure custom_ssm = new StringSimilarityMeasure() {
				
				@Override
				public double getSimilarity(String s1, String s2) {
					String[] s1Tokens = s1.split("\\s+"); // tokenize
					String[] s2Tokens = s2.split("\\s+"); // tokenize
					return LabelUtils.computeWesternPersonNameSimilarity(s1Tokens, s2Tokens, LabelInstanceMatcher.this.ssm);
				}
			};
			
			// get the best match between the names.
			return StringUtility.getMaxStringSimilarity(processedSourceLabels, processedTargetLabels, custom_ssm);
		}
		else {
			// don't know what to do for this type of entities.
			return StringUtility.getMaxStringSimilarity(processedSourceLabels, processedTargetLabels, ssm);
		}

	}

	public static String processLabel(String label, EntityType type, boolean processComma){
		
		String processedLabel;
		
		if(type == null) {
			processedLabel = LabelUtils.processLabel(label);
		}
		else if(type == EntityType.ORGANIZATION) {
			processedLabel = LabelUtils.processOrganizationLabel(label);
		}
		else if(type == EntityType.PERSON) {
			processedLabel = LabelUtils.processPersonLabel(label);
		}
		else if(type == EntityType.LOCATION){
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
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		StringMetrics metric = ((LabelInstanceMatcherParameters)param).metric;
		ssm = metric.getMeasure();
	}
}
