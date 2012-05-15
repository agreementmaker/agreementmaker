package am.app.mappingEngine.instanceMatchers.genericInstanceMatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Report;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.instanceMatchers.BaseInstanceMatcher;
import am.app.mappingEngine.instanceMatchers.UsesKB;
import am.app.mappingEngine.instanceMatchers.combination.CombinationFunction;
import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.LabeledDatasource;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.ScoredInstance;
import am.app.ontology.instance.ScoredInstanceComparator;

/**
 *	This matcher contains a list of matchers, which are all run and then combined 
 *	using a customizable combination function.
 *	It supports: 
 *		- multiple passes
 *		- logging a report with the similarities by matcher 
 *		- Passing the KBs to the matcher which need them
 * 
 * @author Federico Caimi
 *
 */
public class GenericInstanceMatcher extends BaseInstanceMatcher implements UsesKB{
	private static final long serialVersionUID = -5745262888574700843L;
	
	private List<AbstractMatcher> matchers = new ArrayList<AbstractMatcher>();
	CombinationFunction combination;

	private boolean generateReport = true;
	
	Logger log = Logger.getLogger(GenericInstanceMatcher.class);
	
	private String corefFolder = "CoreferenceReports";
	
	boolean useLWC = true;
			
	public GenericInstanceMatcher(){
		instanceMatchingReport = new Report();
		instanceMatchingReport.setMatchers(matchers);
	}
	
	@Override
	public void passStart() {
		if(generateReport){
			System.out.println("init instanceMatchingReport");
			System.out.println(this);
			instanceMatchingReport = new Report();
			instanceMatchingReport.setMatchers(matchers);
		}
		
		if(!firstPassDone){
			log.info("First pass, requiresTwoPasses=" + requiresTwoPasses());
		}
	}
	
	@Override
	public void passEnd() {
		if(generateReport){
			String output = instanceMatchingReport.printTable();
			
			Date date = new Date() ;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
			File folder = new File(corefFolder);
			if(!folder.exists()) folder.mkdir();
			
			try {
				FileOutputStream fos = new FileOutputStream(corefFolder + File.separator + "sims-" + dateFormat.format(date) + ".tab");
				fos.write(output.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (AbstractMatcher matcher : matchers) {
			matcher.passEnd();
		}
		
		super.passEnd();
	}
	
	public void addInstanceMatcher(AbstractMatcher matcher){
		matchers.add(matcher);
	}
	
	@Override
	public MatchingPair alignInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		List<ScoredInstance> scoredCandidates = rankInstanceCandidates(sourceInstance, targetCandidates);
		
		//System.out.println(scoredCandidates);
		
		ScoredInstance scoredInstance = selectInstanceFromCandidates(scoredCandidates);
		if(scoredInstance == null) return null;
		return new MatchingPair(sourceInstance.getUri(), scoredInstance.getInstance().getUri(), scoredInstance.getScore(), MappingRelation.EQUIVALENCE);	
	}
	
	@Override
	public List<ScoredInstance> rankInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		double similarity;
		List<ScoredInstance> scoredCandidates = new ArrayList<ScoredInstance>();
		if(targetCandidates.size() == 0) return scoredCandidates;
		
		if(!useLWC){
			for (Instance candidate: targetCandidates) {
				similarity = instanceSimilarity(sourceInstance, candidate);
				//System.out.println("sim: " + similarity);
				scoredCandidates.add(new ScoredInstance(candidate, similarity));
			}
		}
		else{
			List<List<Double>> similaritiesList = new ArrayList<List<Double>>();			
			List<Double> similarities;
			for (Instance candidate: targetCandidates) {
				similarities = instanceSimilarities(sourceInstance, candidate);
				similaritiesList.add(similarities);		
				
				if(generateReport && instanceMatchingReport != null){
					//System.out.println(source.getUri().startsWith("text"));
					instanceMatchingReport.putSim(sourceInstance.getUri() + "||" + candidate.getUri(), similarities);
				}
				
				//System.out.println("sim: " + similarity);
				//scoredCandidates.add(new ScoredInstance(candidate, similarity));
			}	
			
			if(targetCandidates.size() == 1){
				double max = 0;
				similarities = similaritiesList.get(0);
				for (int i = 0; i < similarities.size(); i++) {
					if(similarities.get(i) > max) max = similarities.get(i);
				}
				
				similarities.add(max);
				
				if(generateReport && instanceMatchingReport != null){
					//System.out.println(source.getUri().startsWith("text"));
					instanceMatchingReport.putSim(sourceInstance.getUri() + "||" + targetCandidates.get(0).getUri(), similarities);
				}
				scoredCandidates.add(new ScoredInstance(targetCandidates.get(0), combination.combine(similarities)));
				return scoredCandidates;		
			}
			
			
			//for every candidate
			for (int i = 0; i < similaritiesList.size(); i++) {
				double[] weights = new double[matchers.size()];
				
				//compute all the weights
				for (int j = 0; j < weights.length; j++) {
					
					//compute the average for matcher j excluding the candidate i
					double avg = 0;
					for (int t = 0; t < similaritiesList.size(); t++) {
						if(t != i) avg += similaritiesList.get(t).get(j);
					}
					avg = avg / (similaritiesList.size() - 1);
					weights[j] = similaritiesList.get(i).get(j) - avg; 
					weights[j] = Utility.getSigmoidFunction(weights[j]);
				}
				
				
				log.debug(Arrays.toString(weights));
				
				//all the weights have been computed, we can add the LWC combination 
				//to the similarities list for matcher i
				double sim = 0;
				similarities = similaritiesList.get(i);				
				double weightsSum = 0;
				for (int j = 0; j < weights.length; j++) {
					sim += similarities.get(j) * weights[j];
					weightsSum += weights[j];
				}
				sim /= weightsSum;
				
				log.debug(sim);
				
				similarities.add(sim);
				
				if(generateReport && instanceMatchingReport != null){
					//System.out.println(source.getUri().startsWith("text"));
					instanceMatchingReport.putSim(sourceInstance.getUri() + "||" + targetCandidates.get(i).getUri(), similarities);
				}
				scoredCandidates.add(new ScoredInstance(targetCandidates.get(i), combination.combine(similarities)));
			}
		}
		
		Collections.sort(scoredCandidates, new ScoredInstanceComparator());	

		return scoredCandidates;
	}
	
	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		//In this case we have to first run just the matchers requiring two passes
		//and we do not care about the similarity
		if(requiresTwoPasses && !firstPassDone){
			for (AbstractMatcher matcher : matchers) {
				if(matcher.requiresTwoPasses())
					matcher.instanceSimilarity(source, target);
			}			
			return 0.0;
		}
		List<Double> similarities = instanceSimilarities(source, target);
		return combination.combine(similarities);
	}
	
	public List<Double> instanceSimilarities(Instance source, Instance target) throws Exception{
		List<Double> similarities = new ArrayList<Double>();
		for (AbstractMatcher matcher : matchers) {
			similarities.add(matcher.instanceSimilarity(source, target));
		}		
		return similarities;
	}
	
	public void setCombination(CombinationFunction combination) {
		this.combination = combination;
	}
	
	@Override
	public String getName() {
		return "Generic Instance Matcher" + matchers + (useLWC ? " LWC" : "");
	}
	
	@Override
	public String toString() {
		return matchers.toString();
	}
	
	@Override
	public boolean requiresTwoPasses() {
		for (AbstractMatcher matcher : matchers) {
			if(matcher.requiresTwoPasses())
				requiresTwoPasses = true;
		}
		return requiresTwoPasses;
	}

	@Override
	/**
	 * Make sure this method is called after adding all the matchers!
	 */
	public void setSourceKB(LabeledDatasource sourceKB) {
		for (AbstractMatcher matcher : matchers) {
			if(matcher instanceof UsesKB){
				if(sourceKB != null)
					((UsesKB) matcher).setSourceKB(sourceKB);
			}
		}	
	}

	@Override
	/**
	 * Make sure this method is called after adding all the matchers!
	 */
	public void setTargetKB(LabeledDatasource targetKB) {
		for (AbstractMatcher matcher : matchers) {
			if(matcher instanceof UsesKB){
				if(targetKB != null){
					System.out.println("Setting target Knowledge Base " + matcher.getName());
					((UsesKB) matcher).setTargetKB(targetKB);
				}
			}
		}		
	}
}
