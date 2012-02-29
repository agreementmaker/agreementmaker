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

public class TokenInstanceMatcher extends BaseInstanceMatcher{
	private static final long serialVersionUID = 7328940873670935546L;

	private Porter stemmer = new Porter();
	private WordNetUtils wordNetUtils = new WordNetUtils();

	private Logger log = Logger.getLogger(TokenInstanceMatcher.class);

	private String lastSourceURI = "";
	private List<String> lastSourceProcessed; 

	public enum Modality { ALL, ALL_SYNTACTIC, ALL_SEMANTIC, SELECTIVE };

	private Modality modality;

	public List<String> selectedProperties;

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

		List<String> sourceKeywords = new ArrayList<String>();

		//Check the last element cache
		if(lastSourceURI.equals(source.getUri()))
			sourceKeywords = lastSourceProcessed;
		else{

			//Modality ALL means that we have to take ALL the property values
			if(modality == Modality.ALL || modality == Modality.ALL_SYNTACTIC){
				List<String> values = source.getAllPropertyValues();
				sourceKeywords.addAll(values);
			}	
				
			if(modality == Modality.ALL || modality == Modality.ALL_SEMANTIC){
				List<Statement> sourceStmts = source.getStatements();
				for (Statement statement : sourceStmts) {
					String literal = statement.getObject().asLiteral().getString();
					String[] split = literal.split("\\s|,");
					for (int i = 0; i < split.length; i++) {
						split[i].trim();
						if(split[i].length() > 0)
							sourceKeywords.add(split[i]);
					}
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
				List<Statement> sourceStmts = source.getStatements();

				for (Statement statement : sourceStmts) {
					if(statement.getPredicate().getLocalName().toLowerCase().contains("keyword")){
						log.debug("Found keywords property: " + statement.getPredicate());
						String keywords = statement.getObject().asLiteral().getString();
						String[] split = keywords.split("\\s|,");
						for (int i = 0; i < split.length; i++) {
							split[i].trim();
							if(split[i].length() > 0)
								sourceKeywords.add(split[i]);
						}
					}
				}
			}


			log.debug(sourceKeywords);

			sourceKeywords = KeywordsUtils.processKeywords(sourceKeywords);

			String sourceLabel = source.getSingleValuedProperty("label");
			String betweenParentheses = getBetweenParentheses(sourceLabel);
			log.debug("between parentheses: " + betweenParentheses);
			if(betweenParentheses != null) sourceKeywords.add(betweenParentheses);

			//sourceKeywordsCache.put(source.getUri(), sourceKeywords);
			lastSourceURI = source.getUri();
			lastSourceProcessed = sourceKeywords;
		}

		List<String> types = target.getProperty("type");
		List<String> candidateKeywords = new ArrayList<String>();

		//Specific for freebase
		if(types != null){
			types = KeywordsUtils.processKeywords(types);
			//sim = keywordsSimilarity(sourceKeywords, types);
			log.debug("types: " + types);
			candidateKeywords.addAll(types);
		}

		List<Statement> stmts = target.getStatements();
		for (int i = 0; i < stmts.size(); i++) {
			if(stmts.get(i).getPredicate().equals(RDFS.comment)){
				String comment = stmts.get(i).getObject().asLiteral().getString();
				candidateKeywords.add(comment);
				log.debug("Comment:" + comment);
			}
		}

		candidateKeywords = KeywordsUtils.processKeywords(candidateKeywords);

		log.debug("source: " + sourceKeywords);
		log.debug("target: " + candidateKeywords);

		//if(types != null)
		//	candidateKeywords.addAll(types);	

		sim = keywordsSimilarity(sourceKeywords, candidateKeywords);

		log.debug(sim);

		return sim;
	}

	private double keywordsSimilarity(List<String> sourceList, List<String> targetList){
		//Compute score
		double score = 0;
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

				if(wordNetUtils.areSynonyms(source, target) ){
					score += 0.5;
					//System.out.println("matched syn: " + source + "|" + target);
				}
				else{
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
		}

		double avg = (Math.min(sourceList.size(), targetList.size()) 
				+ Math.max(sourceList.size(), targetList.size())) / 2;
		double max = Math.max(sourceList.size(), targetList.size());

		if(max == 0) return 0;

		score /= max;

		return score;
	}

	public static String getBetweenParentheses(String label){
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			return label.substring(beg + 1, end);
		}
		return null; 
	}
}
