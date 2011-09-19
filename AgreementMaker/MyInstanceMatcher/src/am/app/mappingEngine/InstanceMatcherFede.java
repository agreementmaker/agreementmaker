package am.app.mappingEngine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

import classification.Classificator;
import classification.TestSet;
import classification.TrainSet;

import edu.smu.tspell.wordnet.WordNetDatabase;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.StringUtil.StringMetrics;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.ontology.instance.Instance;
import am.output.AlignmentOutput;
import am.utility.EnglishUtility;

public class InstanceMatcherFede extends AbstractMatcher {

	int ambiguous;
	int noResult;
	int singleResult;
	
	int disambiguationMappings = 0;
	
	double labelSimThreshold = 0.9;
	double keyScoreThreshold = 1;
	
	double threshold;
	
	boolean disambiguate = true;
	
	WordNetUtils wordNetUtils;
	
	Porter stemmer = new Porter();
	
	public String referenceAlignmentFile = "C:/Users/federico/workspace/MyInstanceMatcher/OAEI2011/NYTReference/nyt-dbpedia-locations-mappings.rdf";
	
	ArrayList<MatchingPair> filePairs;
	
	TrainSet trainSet;
	
	Classificator classificator;
	
	boolean createTraining = false;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -8278698313888419789L;

	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		wordNetUtils = new WordNetUtils();
		
		performSelection = false;
		
		ReferenceAlignmentMatcher matcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters param = new ReferenceAlignmentParameters();
		param.fileName = referenceAlignmentFile;
		matcher.setParam(param);
		filePairs = matcher.parseStandardOAEI();
		
		trainSet = new TrainSet();
		trainSet.addClasses("match");
		trainSet.addClasses("noMatch");
		classificator = new Classificator(trainSet,"peopleClassificator.model");
	}
	
	@Override
	protected MatchingPair alignInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		
		//System.out.println("Source instance: " + sourceInstance );
		//System.out.println("Target instance list: " + targetCandidates );
		//System.out.println("");
		
		//progressDisplay.appendToReport(sourceInstance.toString() + "\n");
		
		int size = targetCandidates.size();
		if(size == 0) noResult++;
		else if(size == 1) singleResult++;
		else if(size > 1) ambiguous++;
		
		
		//System.out.println("SOURCE:" + sourceInstance);
		//System.out.println(targetCandidates);
		
		String sourceLabel = sourceInstance.getSingleValuedProperty("label");
		
		System.out.println(sourceLabel);
		
		List<String> sourceKeywords = new ArrayList<String>();
		
		sourceLabel = processLabel(sourceLabel, sourceKeywords);
		
		System.out.println(sourceLabel);
		
		if(size == 0) return null;
		
		List<String> articles = sourceInstance.getProperty("article");
		Instance article;
		List<String> desKeywords;
		List<String> orgKeywords;	
		for (int i = 0; i < articles.size(); i++) {
			//Get the first of the list
			String articleURI = articles.get(i);
			article = sourceOntology.getInstances().getInstance(articleURI);
			//System.out.println(article);
			
			desKeywords = article.getProperty("descriptionKeywords");
			orgKeywords = article.getProperty("organizationKeywords");
			
			String title = article.getSingleValuedProperty("title");
			List<String> titleKeywords = new ArrayList<String>();
			titleKeywords.add(title);
			titleKeywords = processKeywords(titleKeywords);
			
			if(desKeywords == null) desKeywords = orgKeywords;
						
			if(desKeywords != null){
				//desKeywords.addAll(titleKeywords);
				if(orgKeywords != null)
					desKeywords.addAll(orgKeywords);
				String keyword;
				for (int j = 0; j < desKeywords.size(); j++) {
					keyword = desKeywords.get(j).toLowerCase();
					if(!sourceKeywords.contains(keyword)){
						sourceKeywords.add(keyword);
					}
				}
			}
//			System.out.println(article);
			//System.out.println("title: " + title);
		}
		
		if(size == 1){
			Instance target = targetCandidates.get(0);
			MatchingPair pair = null;
			
			double score = instanceSimilarity(sourceInstance, target, sourceLabel, sourceKeywords);
			
			if(!target.getUri().contains("wiki"))
				pair = new MatchingPair(sourceInstance.getUri(), target.getUri(), score, MappingRelation.EQUIVALENCE);
			debugMapping(pair);
			
			return pair;
		}
		
		else{
			if(disambiguate == false) return null;
			
//			Instance target = targetCandidates.get(0);
//			if(true)
//			return new MatchingPair(sourceInstance.getUri(), target.getUri(), 1.0, MappingRelation.EQUIVALENCE);
//			
			
			System.out.println("Case of ambiguity:" + sourceInstance.getUri() + " " + sourceLabel + " " + targetCandidates.size());
			
			System.out.println(sourceKeywords);
			sourceKeywords = processKeywords(sourceKeywords);
			System.out.println(sourceKeywords);
			
			Instance candidate;
			double score;
			List<ScoredInstance> scoredCandidates = new ArrayList<ScoredInstance>();
			for (int i = 0; i < targetCandidates.size(); i++) {
				candidate = targetCandidates.get(i);
				score = instanceSimilarity(sourceInstance, candidate, sourceLabel, sourceKeywords);
				scoredCandidates.add(new ScoredInstance(candidate, score));
			}
			
			Collections.sort(scoredCandidates, new ScoredInstanceComparator());	
			
			for (int i = 0; i < scoredCandidates.size(); i++) {
				ScoredInstance scoredInstance = scoredCandidates.get(i);
				if(scoredInstance.getInstance().getUri().contains("wiki")){
					scoredCandidates.remove(scoredInstance);
					i--;
				}
			}
			
			scoredCandidates = ScoredInstance.filter(scoredCandidates, 0.02);
			
			if(scoredCandidates.size() == 1){
				//System.out.println("mapping, score:" + scoredCandidates.get(0).getScore());
				disambiguationMappings++;
				MatchingPair pair = new MatchingPair(sourceInstance.getUri(), scoredCandidates.get(0).getInstance().getUri(), scoredCandidates.get(0).getScore(), MappingRelation.EQUIVALENCE);
				debugMapping(pair);
				
				System.out.println("Generated mapping: " + pair.similarity);
				
				return pair;
			}
		}
		return null;
	}
	
	

	private double instanceSimilarity(Instance sourceInstance,
			Instance candidate, String sourceLabel, List<String> sourceKeywords) {

		double keyScore = 0;
		double labelSim = 0;
		double freebaseScore = 0;
		
		String targetLabel;
		targetLabel = candidate.getSingleValuedProperty("label");
		labelSim = StringMetrics.AMsubstringScore(sourceLabel, targetLabel);
		
		List<String> aliases = candidate.getProperty("alias");
		if(aliases != null){
			double max = labelSim;
			double curr;
			for (int i = 0; i < aliases.size(); i++) {
				curr = StringMetrics.AMsubstringScore(sourceLabel, aliases.get(i));
				if(curr > max){
					//System.out.println("An alias weights more than the label");
					max = curr;
				}
			}
			if(max > labelSim) labelSim = (max + labelSim) / 2;
		}
		
		String value = candidate.getSingleValuedProperty("score");
		if(value != null) freebaseScore = Double.valueOf(value);
		freebaseScore /= 100;
		
		System.out.println("candidate: " + candidate.getUri() + " " + candidate.getSingleValuedProperty("label"));
		List<String> types = candidate.getProperty("type");
		
		//Specific for freebase
		if(types != null){
			types = processKeywords(types);
			//keyScore = keywordsSimilarity(sourceKeywords, types);
			System.out.println("types: " + types);
		}
		
		List<Statement> stmts = candidate.getStatements();
		List<String> candidateKeywords = new ArrayList<String>();
		for (int i = 0; i < stmts.size(); i++) {
			if(stmts.get(i).getPredicate().equals(RDFS.comment)){
				String comment = stmts.get(i).getObject().asLiteral().getString();
				candidateKeywords.add(comment);
				System.out.println("Comment:" + comment);
			}
		}
		candidateKeywords = processKeywords(candidateKeywords);
		System.out.println(sourceKeywords);
		System.out.println(candidateKeywords);
		keyScore = keywordsSimilarity(sourceKeywords, candidateKeywords);
		System.out.println(keyScore);
		
		//Math.min(types.size(), allDesKeywords.size())
		if(createTraining){
			String clazz = "match";
			if(!areMatched(sourceInstance.getUri(), candidate.getUri()))
				clazz = "noMatch";
		}
		
		//trainSet.addTrain(labelSim, freebaseScore, keyScore, clazz);		
		
		TestSet testSet = new TestSet();
		testSet.addTest(labelSim, freebaseScore, keyScore);
		
		//double[][] confidence = classificator.getConfidence(testSet);
				
		double stmtSim = matchStatements(sourceInstance, candidate);
		
				
		System.out.println("lab:" + labelSim + " frb:" + freebaseScore + " key:" + keyScore + "stmtSim:" + stmtSim);
		double score = labelSim + freebaseScore + 2*keyScore + stmtSim;
		//double score = confidence[0][0];
		System.out.println("score:" + score);
		
		return score;
	}

	private double matchStatements(Instance sourceInstance, Instance candidate) {
		List<Statement> sourceStmts = sourceInstance.getStatements();
		List<Statement> targetStmts = candidate.getStatements();
		
		if(sourceStmts.size() == 0 || targetStmts.size() == 0) return 0;
		
		Statement sourceStmt;
		Statement targetStmt;
		int count = 0;
		double totalSim = 0;
		for (int i = 0; i < sourceStmts.size(); i++) {
			for (int j = 0; j < targetStmts.size(); j++) {
				sourceStmt = sourceStmts.get(i);
				targetStmt = targetStmts.get(j);
				if(sourceStmt.getPredicate().equals(targetStmt.getPredicate())){
					if(!sourceStmt.getPredicate().getURI().contains("type") &&
							!sourceStmt.getPredicate().getURI().contains("label")){
						if(sourceStmt.getObject().isLiteral() && targetStmt.getObject().isLiteral()){
							count++;
							System.out.println(sourceStmt + "\n" + targetStmt);
							
							double sim = 0.0;
							
							String s1 = sourceStmt.getObject().asLiteral().getString();
							String s2 = targetStmt.getObject().asLiteral().getString();
							
							System.out.println(s1 + " " + s2);
							
							try{
								double d1 = Double.parseDouble(s1);
								double d2 = Double.parseDouble(s2);
								if(d1 == d2) sim = 1;
								else {
									sim = StringMetrics.AMsubstringScore(sourceStmt.getObject().asLiteral().getString(),targetStmt.getObject().asLiteral().getString());
									//sim = 1 - Math.abs(d1-d2)/50;
									if(sim < 0) sim = 0;
								}
										
							}
							catch (NumberFormatException e) {
								sim = StringMetrics.AMsubstringScore(sourceStmt.getObject().asLiteral().getString(),
										targetStmt.getObject().asLiteral().getString());
							}
							System.out.println(sim);
							totalSim += sim;
						}
					}
				}
			}
		}
		if(count == 0) return 0.0;
		return totalSim / count;
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
					//System.out.println("matched: " + source + "|" + target);
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
		score /= max;
		
		return score;
	}
	
	private List<String> processKeywords(List<String> list) {
		List<String> retValue = new ArrayList<String>();
		
		char[] charBlackList = { ',', '(' , ')', '{', '}', '.'};
		
		String toProcess;
		String curr;
		String[] splitted;
		for (int i = 0; i < list.size(); i++) {
			toProcess = list.get(i).toLowerCase();
			
			for (int j = 0; j < charBlackList.length; j++) {
				toProcess = toProcess.replace(charBlackList[j], ' ');
			}
			
			splitted = toProcess.split(" ");
			
			for (int j = 0; j < splitted.length; j++) {
				curr = splitted[j];
				if(curr.isEmpty()) continue;
				
				if(EnglishUtility.isStopword(curr)) continue;
				
				if(!retValue.contains(curr.trim()))
					retValue.add(curr.trim());
			}
		}
		return retValue;
	}

	@Override
	protected void afterAlignOperations() {
		super.afterAlignOperations();
		System.out.println("Ambiguous: " + ambiguous);
		System.out.println("No Results: " + noResult);
		System.out.println("Single result: " + singleResult);
		System.out.println("Total: " + (ambiguous + noResult + singleResult));
		
		System.out.println("Disambiguated: " + disambiguationMappings);
		
		//checkDoubleMappings();
		
		System.out.println("Writing on file...");
		String output = alignmentsToOutput(instanceAlignmentSet);
		FileOutputStream fos;
		
		try {
			fos = new FileOutputStream("alignment.rdf");
			fos.write(output.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
		
		if(createTraining){
			System.out.println("Storing training set");
			try {
				trainSet.storeFile("trainingSet.xml");
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			System.out.println("Stored successfully");
		}
		
		
		
	}
	
	private void checkDoubleMappings() {
		HashMap<String, List<MatchingPair>> pairsByTarget = new HashMap<String, List<MatchingPair>>(); 
		int count = 0;
		MatchingPair mp;
		for(int i = 0; i < instanceAlignmentSet.size(); i++){
			mp = instanceAlignmentSet.get(i);
			if(!pairsByTarget.containsKey(mp.targetURI)){
				List<MatchingPair> pairs = new ArrayList<MatchingPair>();
				pairs.add(mp);
				pairsByTarget.put(mp.targetURI, pairs);
			}
			else{
				List<MatchingPair> pairs = pairsByTarget.get(mp.targetURI);
				pairs.add(mp);
				System.out.println("Duplicated mapping: " + mp.targetURI);
				count++;
			}
		}
		System.out.println("We have " + count + " duplicated mappings");
		
		for(String key: pairsByTarget.keySet()){
			List<MatchingPair> pairs = pairsByTarget.get(key);
			if(pairs.size() > 1){
				System.out.println(pairs);
				for (int i = 0; i < pairs.size(); i++) {
					instanceAlignmentSet.remove(pairs.get(i));
				}
			}
		}
		
	}

	public String alignmentsToOutput(List<MatchingPair> mappings){
		AlignmentOutput ao = new AlignmentOutput(null);
		ao.stringNS();
        ao.stringStart("yes", "0", "11", "onto1", "onto2", "uri1", "uri2");
        
        for (int i = 0, n = mappings.size(); i < n; i++) {
            MatchingPair mapping = mappings.get(i);
            String e1 = mapping.sourceURI;
            String e2 = mapping.targetURI;
            String measure = Double.toString(mapping.similarity);
            ao.stringElement(e1, e2, measure);
        }
        
        ao.stringEnd();
        return ao.getString();
	}
	
	public static String processLabel(String label, List<String> keywords){
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			keywords.add(label.substring(beg + 1, end));	
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
		if(label.contains(",")){
			String[] splitted = label.split(",");
			return splitted[1].trim() + " " + splitted[0].trim();
		}
		return label; 
	}
	
	public void setThreshold(double threshold){
		this.threshold = threshold;
	}
	
	public boolean areMatched(String sourceURI, String targetURI){
		for (int i = 0; i < filePairs.size(); i++) {
			if(filePairs.get(i).sourceURI.equals(sourceURI)){
				if(filePairs.get(i).targetURI.equals(targetURI)){
					return true;
				}
				else return false;
			}
		}
		return false;
	}
	
	public void debugMapping(MatchingPair pair){
		if(pair == null) return;
		String source = pair.sourceURI;
		for (int i = 0; i < filePairs.size(); i++) {
			if(filePairs.get(i).sourceURI.equals(source)){
				if(filePairs.get(i).sameTarget(pair)){
					System.out.println("RIGHT MAPPING " + filePairs.get(i));
				}
				else System.out.println("WRONG MAPPING right:" + filePairs.get(i));
			}
		}
	}
}
