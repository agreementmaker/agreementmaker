package am.app.mappingEngine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.StringUtil.StringMetrics;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8278698313888419789L;

	@Override
	protected void beforeAlignOperations() throws Exception {
		// TODO Auto-generated method stub
		super.beforeAlignOperations();
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
		sourceLabel = processLabel(sourceLabel);
		
		if(size == 0) return null;
		
		if(size == 1){
			Instance target = targetCandidates.get(0);
			MatchingPair pair = null;
			if(!target.getUri().contains("wiki"))
				pair = new MatchingPair(sourceInstance.getUri(), target.getUri(), 1.0, MappingRelation.EQUIVALENCE);
			return pair;
		}
		
		else{
			if(disambiguate == false) return null;
			
			System.out.println("Case of ambiguity:" + sourceInstance.getUri() + " " + sourceLabel + " " + targetCandidates.size());
			
			List<String> articles = sourceInstance.getProperty("article");
			
			if(articles == null){
//				Instance target = targetCandidates.get(0);
//				MatchingPair pair = new MatchingPair(sourceInstance.getUri(), target.getUri(), 1.0, MappingRelation.EQUIVALENCE);
//				return pair;
				return null;
			}
			
			Instance article;
			List<String> allDesKeywords = new ArrayList<String>();
			List<String> desKeywords;
			for (int i = 0; i < articles.size(); i++) {
				//Get the first of the list
				String articleURI = articles.get(i);
				article = sourceOntology.getInstances().getInstance(articleURI);
				System.out.println(article);
				
				desKeywords = article.getProperty("descriptionKeywords");
				if(desKeywords != null){
					String keyword;
					for (int j = 0; j < desKeywords.size(); j++) {
						keyword = desKeywords.get(j).toLowerCase();
						if(!allDesKeywords.contains(keyword)){
							allDesKeywords.add(keyword);
						}
					}
				}
			}
			
			System.out.println(allDesKeywords);
			
			allDesKeywords = processKeywords(allDesKeywords);
				
			double keyScore;
			double labelSim;
			double freebaseScore = 0;
			Instance candidate;
			List<ScoredInstance> scoredCandidates = new ArrayList<ScoredInstance>();
			String targetLabel;
			for (int i = 0; i < targetCandidates.size(); i++) {
				
				candidate = targetCandidates.get(i);
				
				targetLabel = candidate.getSingleValuedProperty("label");
				labelSim = StringMetrics.AMsubstringScore(sourceLabel, targetLabel);
				
				String value = candidate.getSingleValuedProperty("score");
				if(value != null) freebaseScore = Double.valueOf(value);
				freebaseScore /= 100;
				
				System.out.println("candidate: " + candidate.getUri() + " " + candidate.getSingleValuedProperty("score"));
				List<String> types = candidate.getProperty("type");
				
				types = processKeywords(types);
				
				System.out.println("types: " + types);
				
				keyScore = keywordsSimilarity(allDesKeywords, types);
				
				//Math.min(types.size(), allDesKeywords.size())
				
				
				double score = labelSim + freebaseScore + 3*keyScore;
				
				scoredCandidates.add(new ScoredInstance(candidate, score));
				
				System.out.println("lab:" + labelSim + " frb:" + freebaseScore + " key:" + keyScore);
				System.out.println("score:" + score);
				
//				if(labelSim >= labelSimThreshold){
//					disambiguationMappings++;
//					return new MatchingPair(sourceInstance.getUri(), candidate.getUri());
//				}
				
			}
			
			Collections.sort(scoredCandidates, new ScoredInstanceComparator());	
			
			if(scoredCandidates.get(0).getInstance().getUri().contains("wikipedia")){
				scoredCandidates.remove(0);
			}
			
			scoredCandidates = ScoredInstance.filter(scoredCandidates, 0.02);
			
			if(scoredCandidates.size() == 1 && scoredCandidates.get(0).getScore() > threshold){
				//System.out.println("mapping, score:" + scoredCandidates.get(0).getScore());
				disambiguationMappings++;
				return new MatchingPair(sourceInstance.getUri(), scoredCandidates.get(0).getInstance().getUri());
			}
		}
		return null;
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
					System.out.println("matched: " + source + "|" + target);
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
			toProcess = list.get(i);
			
			for (int j = 0; j < charBlackList.length; j++) {
				toProcess = toProcess.replace(charBlackList[j], ' ');
			}
			
			splitted = toProcess.split(" ");
			
			for (int j = 0; j < splitted.length; j++) {
				curr = splitted[j];
				if(curr.isEmpty()) continue;
				
				if(!retValue.contains(curr.trim()) && !EnglishUtility.isStopword(curr))
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
	
}
