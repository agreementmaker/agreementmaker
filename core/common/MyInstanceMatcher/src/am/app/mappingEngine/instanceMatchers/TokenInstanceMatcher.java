package am.app.mappingEngine.instanceMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

import am.app.mappingEngine.KeywordsUtils;
import am.app.mappingEngine.Porter;
import am.app.mappingEngine.WordNetUtils;
import am.app.ontology.instance.Instance;

public class TokenInstanceMatcher extends BaseInstanceMatcher{

	private static final long serialVersionUID = 7328940873670935546L;
	
	Porter stemmer = new Porter();
	WordNetUtils wordNetUtils = new WordNetUtils();
	
	Logger log = Logger.getLogger(TokenInstanceMatcher.class);
	
	
	String lastSourceURI = "";
	List<String> lastSourceProcessed; 
		
	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		
		double sim = 0.0;
		
		List<String> sourceKeywords;
		
		if(lastSourceURI.equals(source.getUri()))
			sourceKeywords = lastSourceProcessed;
		else{
			sourceKeywords = new ArrayList<String>();
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
