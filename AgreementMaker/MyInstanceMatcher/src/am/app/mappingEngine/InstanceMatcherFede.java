package am.app.mappingEngine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

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
	
	public String referenceAlignmentFile = "C:/Users/federico/workspace/MyInstanceMatcher/OAEI2011/NYTReference/nyt-freebase-locations-mappings.rdf";
	
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
		
		sourceLabel = processLabel(sourceLabel);
		
		System.out.println(sourceLabel);
		
		if(size == 0) return null;
		
		List<String> articles = sourceInstance.getProperty("article");
		Instance article;
		List<String> allDesKeywords = new ArrayList<String>();
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
					if(!allDesKeywords.contains(keyword)){
						allDesKeywords.add(keyword);
					}
				}
			}
//			System.out.println(article);
			//System.out.println("title: " + title);
		}
		
		if(size == 1){
			Instance target = targetCandidates.get(0);
			MatchingPair pair = null;
			
			double score = instanceSimilarity(sourceInstance, target, sourceLabel, allDesKeywords);
			
			if(!target.getUri().contains("wiki") && score > threshold)
				pair = new MatchingPair(sourceInstance.getUri(), target.getUri(), 1.0, MappingRelation.EQUIVALENCE);
			debugMapping(pair);
			return pair;
		}
		
		else{
			if(disambiguate == false) return null;
			
			System.out.println("Case of ambiguity:" + sourceInstance.getUri() + " " + sourceLabel + " " + targetCandidates.size());
			
			System.out.println(allDesKeywords);
			allDesKeywords = processKeywords(allDesKeywords);
			System.out.println(allDesKeywords);
			
			Instance candidate;
			List<ScoredInstance> scoredCandidates = new ArrayList<ScoredInstance>();
			for (int i = 0; i < targetCandidates.size(); i++) {
				
				candidate = targetCandidates.get(i);
				
				double score = instanceSimilarity(sourceInstance, candidate, sourceLabel, allDesKeywords);
				
				scoredCandidates.add(new ScoredInstance(candidate, score));
	
//				if(labelSim >= labelSimThreshold){
//					disambiguationMappings++;
//					return new MatchingPair(sourceInstance.getUri(), candidate.getUri());
//				}
				
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
			
			if(scoredCandidates.size() == 1 && scoredCandidates.get(0).getScore() > threshold){
				//System.out.println("mapping, score:" + scoredCandidates.get(0).getScore());
				disambiguationMappings++;
				MatchingPair pair = new MatchingPair(sourceInstance.getUri(), scoredCandidates.get(0).getInstance().getUri());
				debugMapping(pair);
				return pair;
			}
		}
		return null;
	}
	
	

	private double instanceSimilarity(Instance sourceInstance,
			Instance candidate, String sourceLabel, List<String> sourceKeywords) {

		double keyScore;
		double labelSim;
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
		
		System.out.println("candidate: " + candidate.getUri() + " " + candidate.getSingleValuedProperty("score"));
		List<String> types = candidate.getProperty("type");
		
		types = processKeywords(types);
		
		System.out.println("types: " + types);
		
		keyScore = keywordsSimilarity(sourceKeywords, types);
		
		//Math.min(types.size(), allDesKeywords.size())
		if(createTraining){
			String clazz = "match";
			if(!areMatched(sourceInstance.getUri(), candidate.getUri()))
				clazz = "noMatch";
			
		}
		
		//trainSet.addTrain(labelSim, freebaseScore, keyScore, clazz);		
		
		TestSet testSet = new TestSet();
		testSet.addTest(labelSim, freebaseScore, keyScore);
		
		double[][] confidence = classificator.getConfidence(testSet);
				
		System.out.println("lab:" + labelSim + " frb:" + freebaseScore + " key:" + keyScore);
		double score = labelSim + freebaseScore + 2*keyScore;
		//double score = confidence[0][0];
		System.out.println("score:" + score);
		
		return score;
	}

	private double keywordsSimilarity(List<String> sourceList, List<String> targetList){
		//Compute score
		double score = 0;
		String source;
		String target;
		for (int j = 0; j < sourceList.size(); j++) {
			source = sourceList.get(j);
			source = source.toLowerCase();
			for (int t = 0; t < targetList.size(); t++) {
				target = targetList.get(t).toLowerCase();
				//System.out.println(type + "|" + keyword);
				if(source.equals(target)){
					score++;
				}
				
				if(wordNetUtils.areSynonyms(source, target) ){
					score += 0.5;
					//System.out.println("matched: " + source + "|" + target);
				}
				else if(stemmer.stripAffixes(source).equals(stemmer.stripAffixes(target))){
					score += 0.5;
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
		
		char[] charBlackList = { ',', '(' , ')', '{', '}'};
		
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
	
	public String alignmentsToOutput(List<MatchingPair> mappings){
		AlignmentOutput ao = new AlignmentOutput(null);
		ao.stringNS();
        ao.stringStart("yes", "0", "11", "onto1", "onto2", "uri1", "uri2");
        
        for (int i = 0, n = mappings.size(); i < n; i++) {
            MatchingPair mapping = mappings.get(i);
            String e1 = mapping.sourceURI;
            String e2 = mapping.targetURI;
            String measure = Double.toString(1.0);
            ao.stringElement(e1, e2, measure);
        }
        
        ao.stringEnd();
        return ao.getString();
	}
	
	public static String processLabel(String label){
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
