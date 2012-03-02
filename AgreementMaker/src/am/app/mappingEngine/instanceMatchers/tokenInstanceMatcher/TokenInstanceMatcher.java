package am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.instanceMatchers.BaseInstanceMatcher;
import am.app.mappingEngine.instanceMatchers.KeywordsUtils;
import am.app.mappingEngine.instanceMatchers.Porter;
import am.app.mappingEngine.instanceMatchers.WordNetUtils;
import am.app.mappingEngine.instanceMatchers.labelInstanceMatcher.LabelInstanceMatcher;
import am.app.ontology.instance.Instance;

/**
 * Instance matcher based on bag-of-words comparison.
 * 
 * @author federico
 *
 */
public class TokenInstanceMatcher extends BaseInstanceMatcher{
	private static final long serialVersionUID = 7328940873670935546L;

	private Porter stemmer = new Porter();
	private WordNetUtils wordNetUtils = new WordNetUtils();

	private Logger log = Logger.getLogger(TokenInstanceMatcher.class);

	private String lastSourceURI = "";
	private List<String> lastSourceProcessed; 

	private boolean useSynonyms = false;

	LabeledKnowledgeBase sourceKB;
	LabeledKnowledgeBase targetKB;

	/**
	 * Modality ALL means that we have to take ALL the property values
	 * 
	 * @author federico
	 *
	 */
	public enum Modality { ALL, ALL_SYNTACTIC, ALL_SEMANTIC, SELECTIVE };

	private Modality modality;

	/**
	 * List of properties of which we need to gather values
	 */
	public List<String> selectedProperties;

	/**
	 * Default modality is all
	 */
	public TokenInstanceMatcher(){
		modality = Modality.ALL;
	}

	@Override
	public void setParam(DefaultMatcherParameters param) {
		if(param instanceof TokenInstanceMatcherParameters){
			TokenInstanceMatcherParameters p = (TokenInstanceMatcherParameters) param;
			modality = p.modality;
			selectedProperties = p.selectedProperties;
		}
		super.setParam(param);
	}

	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		double sim = 0.0;

		log.debug("TIM matching: " + source.getUri() + " " + target);		

		List<String> sourceKeywords = new ArrayList<String>();

		//Check the last element cache
		//TODO check URIs when packing entities
		if(lastSourceURI.equals(source.getUri()))
			sourceKeywords = lastSourceProcessed;
		else{
			sourceKeywords = buildKeywordsList(source, sourceKB);
			log.debug("S Preprocess:" + sourceKeywords);
			sourceKeywords = KeywordsUtils.processKeywords(sourceKeywords);
			log.debug("S Postprocess:" + sourceKeywords);
		}

		List<String> targetKeywords = buildKeywordsList(target, targetKB);

		lastSourceURI = source.getUri();
		lastSourceProcessed = sourceKeywords;

		log.debug("T Preprocess:" + targetKeywords);
		targetKeywords = KeywordsUtils.processKeywords(targetKeywords);
		log.debug("T Postprocess:" + targetKeywords);

		sim = keywordsSimilarity(sourceKeywords, targetKeywords);

		log.debug(sim);

		return sim;
	}

	private List<String> buildKeywordsList(Instance instance, LabeledKnowledgeBase kb) {
		List<String> values = new ArrayList<String>();	

		//In case modality is ALL or ALL_SYNTACTIC, we have to have to gather all the properties 
		//from the properties map
		if(modality == Modality.ALL || modality == Modality.ALL_SYNTACTIC){
			values.addAll(instance.getAllPropertyValues());
		}	

		//In case modality is ALL or ALL_SEMANTIC, we have to have to gather all the properties 
		//from the list of statements
		if(modality == Modality.ALL || modality == Modality.ALL_SEMANTIC){
			List<Statement> stmts = instance.getStatements();
			for (Statement statement : stmts) {
				String literal = statement.getObject().asLiteral().getString();

				if(literal.startsWith("http://")){
					//TODO manage URI

					if(kb == null){
						//TODO Figure out if we need to take the URI fragment when we cannot access to the label
						continue;
					}

					String label = kb.getLabelFromURI(literal);
					if(label != null)
						values.add(label);					
				}
				else{
					int limit = 300;				
					if(literal.length() < limit)					
						values.add(literal);
					else values.add(literal.substring(0, limit - 1));
				}

				//				String[] split = literal.split("\\s|,");
				//				for (int i = 0; i < split.length; i++) {
				//					split[i].trim();
				//					if(split[i].length() > 0)
				//						sourceKeywords.add(split[i]);
				//				}		
			}
		}

		//				for ( statement : sourceStmts) {
		//					String literal = statement.getObject().asLiteral().getString();
		//					String[] split = literal.split("\\s|,");
		//					for (int i = 0; i < split.length; i++) {
		//						split[i].trim();
		//						if(split[i].length() > 0)
		//							sourceKeywords.add(split[i]);
		//					}
		//				}				

		//Modality SELECTIVE means that we have to take only some of the property values,
		//for which the strings in selectedProperties are matching
		//TODO implement this
		if(modality == Modality.SELECTIVE){
			// TODO manage selective modality						

			//			List<Statement> sourceStmts = source.getStatements();
			//
			//			for (Statement statement : sourceStmts) {
			//				if(statement.getPredicate().getLocalName().toLowerCase().contains("keyword")){
			//					log.debug("Found keywords property: " + statement.getPredicate());
			//					String keywords = statement.getObject().asLiteral().getString();
			//					String[] split = keywords.split("\\s|,");
			//					for (int i = 0; i < split.length; i++) {
			//						split[i].trim();
			//						if(split[i].length() > 0)
			//							sourceKeywords.add(split[i]);
			//					}
			//				}
			//			}
			//			
			//			List<String> types = target.getProperty("type");
			//			List<String> candidateKeywords = new ArrayList<String>();
			//
			//			//Specific for freebase
			//			if(types != null){
			//				types = KeywordsUtils.processKeywords(types);
			//				//sim = keywordsSimilarity(sourceKeywords, types);
			//				log.debug("types: " + types);
			//				candidateKeywords.addAll(types);
			//			}
			//
			//			List<Statement> stmts = target.getStatements();
			//			for (int i = 0; i < stmts.size(); i++) {
			//				if(stmts.get(i).getPredicate().equals(RDFS.comment)){
			//					String comment = stmts.get(i).getObject().asLiteral().getString();
			//					candidateKeywords.add(comment);
			//					log.debug("Comment:" + comment);
			//				}
			//			}
		}

		String label = instance.getSingleValuedProperty("label");

		if(label != null){
			String betweenParentheses = getBetweenParentheses(label);
			log.debug("between parentheses: " + betweenParentheses);
			if(betweenParentheses != null) values.add(betweenParentheses);		
		}

		return values;
	}

	private double keywordsSimilarity(List<String> sourceList, List<String> targetList){
		//Compute score
		double score = 0;
		
		List<String> sourceStemmed = stemList(sourceList);
		
		String source;
		String target;
		for (int j = 0; j < sourceList.size(); j++) {
			source = sourceList.get(j);
			if(source.isEmpty()) continue;
			source = source.toLowerCase();
			for (int t = 0; t < targetList.size(); t++) {
				target = targetList.get(t).toLowerCase();
				if(target.isEmpty()) continue;
				//System.out.println(type + "|" + keyword);
				if(source.equals(target)){
					score++;
				}

				if(useSynonyms){
					if(wordNetUtils.areSynonyms(source, target) ){
						score += 0.5;
						//System.out.println("matched syn: " + source + "|" + target);
					}
					continue;
				}

				boolean condition = false;
				try{
					condition = stemmer.stripAffixes(source).equals(stemmer.stripAffixes(target));
					if(condition)
						score += 0.5;
				}
				catch (Exception e) {
					System.err.println("Error when stemming " + source + " with " + target);
				}

			}
		}

		double avg = (Math.min(sourceList.size(), targetList.size()) 
				+ Math.max(sourceList.size(), targetList.size())) / 2;
		double max = Math.max(sourceList.size(), targetList.size());

		if(max == 0) return 0;

		score /= max;

		return score;
	}

	private List<String> stemList(List<String> list) {
		List<String> stemmed = new ArrayList<String>();
		for (String string : list) {
			stemmed.add(stemmer.stripAffixes(string));
		}		
		return stemmed;
	}

	public static String getBetweenParentheses(String label){
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			return label.substring(beg + 1, end);
		}
		return null; 
	}

	public void setSourceKB(LabeledKnowledgeBase sourceKB) {
		this.sourceKB = sourceKB;
	}

	public void setTargetKB(LabeledKnowledgeBase targetKB) {
		this.targetKB = targetKB;
	}
}
