package am.app.mappingEngine.instanceMatchers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.instanceMatchers.AlignmentsOutput;
import am.app.mappingEngine.instanceMatchers.KeywordsUtils;
import am.app.mappingEngine.instanceMatchers.Porter;
import am.app.mappingEngine.instanceMatchers.ScoredInstance;
import am.app.mappingEngine.instanceMatchers.ScoredInstanceComparator;
import am.app.mappingEngine.instanceMatchers.WordNetUtils;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.instanceMatcher.LabelUtils;
import am.app.mappingEngine.instanceMatchers.labelInstanceMatcher.LabelInstanceMatcher;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.instance.Instance;
import am.utility.referenceAlignment.AlignmentUtilities;

public class InstanceMatcherFedeNew extends AbstractMatcher {

	int ambiguous;
	int noResult;
	int singleResult;
	int solvable;
	
	int disambiguationMappings = 0;
	
	double labelSimThreshold = 0.9;
	double keyScoreThreshold = 1;
	
	double threshold = 0.8;
	
	boolean disambiguate = true;
	
	boolean matchingDBPedia = false;
	boolean verbose = false;
	private String outputFilename = "alignments.rdf";
	
	private static final long serialVersionUID = -8278698313888419789L;
	
	Logger log = Logger.getLogger(InstanceMatcherFedeNew.class);
	
	boolean useSTIM = true;	
	boolean useLIM = true;	
	boolean useTIM = true;	
		
	StatementsInstanceMatcher stim = new StatementsInstanceMatcher();
	LabelInstanceMatcher lim = new LabelInstanceMatcher();
	TokenInstanceMatcher tim = new TokenInstanceMatcher();
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		//log.setLevel(Level.DEBUG);
		
		performSelection = false;
	}
	
	@Override
	public MatchingPair alignInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		
		//System.out.println("Source instance: " + sourceInstance );
		//System.out.println("Target instance list: " + targetCandidates );
		//System.out.println("");
		
		//progressDisplay.appendToReport(sourceInstance.toString() + "\n");
		
		int size = targetCandidates.size();
		if(size == 0){
			noResult++;
			return null;
		}
		else if(size == 1) singleResult++;
		else if(size > 1) ambiguous++;
		
		if(referenceAlignment != null && AlignmentUtilities.candidatesContainSolution(referenceAlignment, 
				sourceInstance.getUri(), targetCandidates) != null)
			solvable++;
				
		String sourceLabel = sourceInstance.getSingleValuedProperty("label");
		
		if(size == 1){
			Instance target = targetCandidates.get(0);
			MatchingPair pair = null;
			
			double score = instanceSimilarity(sourceInstance, target);
			
			// && score >= threshold
			if(!target.getUri().contains("wiki"))
				pair = new MatchingPair(sourceInstance.getUri(), target.getUri(), score + 0.2, MappingRelation.EQUIVALENCE);
			debugMapping(pair);
			
			return pair;
		}
		
		else{
			if(disambiguate == false) return null;
			
			log.debug("Case of ambiguity:" + sourceInstance.getUri() + " " + sourceLabel + " " + targetCandidates.size());
			
			Instance candidate;
			double score;
			List<ScoredInstance> scoredCandidates = new ArrayList<ScoredInstance>();
			for (int i = 0; i < targetCandidates.size(); i++) {
				candidate = targetCandidates.get(i);
				score = instanceSimilarity(sourceInstance, candidate);
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
			
			scoredCandidates = ScoredInstance.filter(scoredCandidates, 0.01);
			
			if(scoredCandidates.size() == 1){
				//System.out.println("mapping, score:" + scoredCandidates.get(0).getScore());
				disambiguationMappings++;
				double candidateScore = scoredCandidates.get(0).getScore();
				MatchingPair pair = new MatchingPair(sourceInstance.getUri(), scoredCandidates.get(0).getInstance().getUri(), candidateScore, MappingRelation.EQUIVALENCE);
				debugMapping(pair);
				log.debug("Generated mapping: " + pair.sourceURI + " " + pair.targetURI);
				//System.out.println("About to match: " + candidateScore);
				if (candidateScore < threshold) return null;
				return pair;
			}
		}
		return null;
	}
	
	public double instanceSimilarity(Instance sourceInstance,
			Instance candidate) throws Exception {

		double keyScore = 0;
		double freebaseScore = 0;
		double labelSim = 0;
		double stmtSim = 0;
		
		if(useLIM)
			labelSim = lim.instanceSimilarity(sourceInstance, candidate);
		
		String value = candidate.getSingleValuedProperty("score");
		if(value != null) freebaseScore = Double.valueOf(value);
		else freebaseScore = -1;
		if(freebaseScore != -1) freebaseScore /= 100;
		
		log.debug("candidate: " + candidate.getUri() + " " + candidate.getSingleValuedProperty("label"));
		
		if(useSTIM)
			stmtSim = stim.instanceSimilarity(sourceInstance, candidate);
		
		
		if(freebaseScore == -1) freebaseScore = 0;
		//if(stmtSim == -1) stmtSim = 0;
		
		if(useTIM)
			keyScore = tim.instanceSimilarity(sourceInstance, candidate);
		
		log.debug("lab:" + labelSim + " frb:" + freebaseScore + " key:" + keyScore + " stmtSim:" + stmtSim);
				
		double score = labelSim/2 + stmtSim/2 + 1*keyScore + freebaseScore/2;
		if(stmtSim == -1){
			score = labelSim + 1*keyScore + freebaseScore/2;	
		}
		
		//double score = labelSim;
				
		log.debug(score);
		
		//double score = (labelSim + stmtSim)/1.5 + 1*keyScore;
		
		//double score = stmtSim;
		
		//double score = keyScore * 3;
		
		//double score = 0;
		
		//if(score > 0) System.err.println("SCORE: " + score);
		
		if(verbose) System.out.println("score:" + score);
	
		
		return score;
	}

	@Override
	protected void afterAlignOperations() {
		super.afterAlignOperations();
		System.out.println("Ambiguous: " + ambiguous);
		System.out.println("No Results: " + noResult);
		System.out.println("Single result: " + singleResult);
		System.out.println("Total: " + (ambiguous + noResult + singleResult));
		if(referenceAlignment != null)
			System.out.println("InReference: " + referenceAlignment.size());
		
		System.out.println("Solvable: " + solvable);
		
		System.out.println("Disambiguated: " + disambiguationMappings);
		
		//checkDoubleMappings();
		
		if(matchingDBPedia){
			//cleanDBPediaMappings();
		}
		
		System.out.println("Writing on file...");
		String output = AlignmentsOutput.alignmentsToOutput(instanceAlignmentSet);
		FileOutputStream fos;
		
		try {
			fos = new FileOutputStream("alignment.rdf");
			fos.write(output.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
		
		AlignmentsOutput.writeMappingsOnDisk(outputFilename, instanceAlignmentSet);
		
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
				log.info("Duplicated mapping: " + mp.targetURI);
				count++;
			}
		}
		log.info("We have " + count + " duplicated mappings");
		
		for(String key: pairsByTarget.keySet()){
			List<MatchingPair> pairs = pairsByTarget.get(key);
			if(pairs.size() > 1){
				log.info(pairs);
				for (int i = 0; i < pairs.size(); i++) {
					instanceAlignmentSet.remove(pairs.get(i));
				}
			}
		}
	}
	
	public void setThreshold(double threshold){
		this.threshold = threshold;
	}
	
	public boolean areMatched(String sourceURI, String targetURI){
		for (int i = 0; i < referenceAlignment.size(); i++) {
			if(referenceAlignment.get(i).sourceURI.equals(sourceURI)){
				if(referenceAlignment.get(i).targetURI.equals(targetURI)){
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
		for (int i = 0; i < referenceAlignment.size(); i++) {
			if(referenceAlignment.get(i).sourceURI.equals(source)){
				if(referenceAlignment.get(i).sameTarget(pair)){
					log.debug("RIGHT MAPPING " + referenceAlignment.get(i));
				}
				else{
					log.debug("GENERATED: " + pair);
					log.debug("WRONG MAPPING right:" + referenceAlignment.get(i));
				}
			}
		}
	}
	
	public void setOutputFile(String fileName){
		outputFilename = fileName;
	}
	
	@Override
	public String processLabelBeforeCandidatesGeneration(String label, String type) {
		log.debug(label + "\t" + type);
		
		if(type == null) return super.processLabelBeforeCandidatesGeneration(label, type);
		
		if(type.toLowerCase().endsWith("organization"))
			return LabelUtils.processOrganizationLabel(label);
		
		else if(type.toLowerCase().endsWith("person"))
			return LabelUtils.processPersonLabel(label);
		
		else if(type.toLowerCase().endsWith("location"))
			return LabelUtils.processLocationLabel(label);
		
		return super.processLabelBeforeCandidatesGeneration(label, type);
	}
}
