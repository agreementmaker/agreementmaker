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
import am.app.mappingEngine.InstanceMatchingReport;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.instance.AbstractInstanceMatcher;
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

	private List<AbstractInstanceMatcher> matchers = new ArrayList<AbstractInstanceMatcher>();
	CombinationFunction combination;

	private boolean generateReport = true;

	private static final Logger sLog = Logger.getLogger(GenericInstanceMatcher.class);

	private String corefFolder = "CoreferenceReports";

	boolean useAdditionalFeatures = true;
	boolean useLWC = true;
	boolean useCandidateSize = true;
	boolean useAllConfidence = false;

	public GenericInstanceMatcher(){
		instanceMatchingReport = new InstanceMatchingReport();
		instanceMatchingReport.setMatchers(matchers);
	}

	@Override
	public void passStart() {
		if(generateReport){
			System.out.println("init instanceMatchingReport");
			System.out.println(this);
			instanceMatchingReport = new InstanceMatchingReport();
			instanceMatchingReport.setMatchers(matchers);
		}

		if(!firstPassDone){
			sLog.info("First pass, requiresTwoPasses=" + requiresTwoPasses());
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
				sLog.error("", e);
			}
		}

		for (AbstractInstanceMatcher matcher : matchers) {
			matcher.passEnd();
		}

		super.passEnd();
	}

	public void addInstanceMatcher(AbstractInstanceMatcher matcher){
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

	/**
	 * <p>
	 * This method generates a similarity vector for each pair of instances. The
	 * source instance of the pair will always be {@link sourceInstance} but the
	 * target instance of the pair will be one of the instances in
	 * {@link targetCandidates}.
	 * </p>
	 * <p>
	 * The values in the similarity vector can be the following:
	 * <ul>
	 * <li>The similarity score for the source and target instance, computed by
	 * {@link #instanceSimilarities(Instance, Instance)}.</li>
	 * <li>... (to be continued) ...</li>
	 * </ul>
	 * </p>
	 * 
	 * @return A list containing each candidate, but with an attached score. The
	 *         ranking is done according to the score.
	 */
	@Override
	public List<ScoredInstance> rankInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		double similarity;
		List<ScoredInstance> scoredCandidates = new ArrayList<ScoredInstance>();
		if(targetCandidates.size() == 0) return scoredCandidates;


		if(!useAdditionalFeatures){
			for (Instance candidate: targetCandidates) {
				similarity = instanceSimilarity(sourceInstance, candidate);
				//System.out.println("sim: " + similarity);
				scoredCandidates.add(new ScoredInstance(candidate, similarity));
			}
		}
		else{
			List<List<Double>> similaritiesVectors = new ArrayList<List<Double>>();			
			List<Double> similarities;

			int numMatchers = matchers.size();

			for (Instance candidate: targetCandidates) {
				similarities = instanceSimilarities(sourceInstance, candidate);
				similaritiesVectors.add(similarities);		

				if(generateReport && instanceMatchingReport != null){
					//System.out.println(source.getUri().startsWith("text"));
					instanceMatchingReport.putSim(sourceInstance.getUri() + "||" + candidate.getUri(), similarities);
				}

				//System.out.println("sim: " + similarity);
				//scoredCandidates.add(new ScoredInstance(candidate, similarity));
			}	
			
			similaritiesVectors = normalizeSimilaritiesList(similaritiesVectors);

			if(targetCandidates.size() == 1){
				double max = 0;
				similarities = similaritiesVectors.get(0);
				for (int i = 0; i < similarities.size(); i++) {
					if(similarities.get(i) > max) max = similarities.get(i);
				}

				//Make sure they are in the same order below!!
				if(useAllConfidence){
					for (int i = 0; i < numMatchers; i++) {
						similarities.add(similarities.get(i));
					}
				}

				if(useLWC)
					similarities.add(max);

				if(useCandidateSize)
					similarities.add(1d);


				if(generateReport && instanceMatchingReport != null){
					//System.out.println(source.getUri().startsWith("text"));
					instanceMatchingReport.putSim(sourceInstance.getUri() + "||" + targetCandidates.get(0).getUri(), similarities);
				}
				scoredCandidates.add(new ScoredInstance(targetCandidates.get(0), combination.combine(similarities)));
				return scoredCandidates;		
			}


			//for every candidate
			for (int i = 0; i < similaritiesVectors.size(); i++) {
				similarities = similaritiesVectors.get(i);
				//compute all the weights

				double[] weights = computeLwcWeights(similaritiesVectors, i, matchers.size());

				if(useAllConfidence){
					for (int j = 0; j < weights.length; j++) {
						similarities.add(weights[j]);
					}
				}

				sLog.debug(Arrays.toString(weights));


				//all the weights have been computed, we can add the LWC combination 
				//to the similarities list for matcher i
				similarities = similaritiesVectors.get(i);				

				double sim = computeLinearWeightedCombination(similarities, weights);

				sLog.debug(sim);

				if(useLWC)
					similarities.add(sim);

				if(useCandidateSize)
					similarities.add((double)targetCandidates.size());

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
	
	public static List<List<Double>> normalizeSimilaritiesList(List<List<Double>> similaritiesList){
		List<List<Double>> normalized = new ArrayList<List<Double>>();
		double[] maximums = new double[similaritiesList.get(0).size()];
		for (int i = 0; i < similaritiesList.size(); i++) {
			List<Double> similarities = similaritiesList.get(i);
			for (int j = 0; j < similarities.size(); j++) {
				if(similarities.get(j) > maximums[j])
					maximums[j] = similarities.get(j);
			}
		}
		//System.out.println(Arrays.toString(maximums));
		for (int i = 0; i < similaritiesList.size(); i++) {
			List<Double> similarities = new ArrayList<Double>();
			for (int j = 0; j < similaritiesList.get(i).size(); j++) {
				if(maximums[j] == 0) similarities.add(0d);
				else similarities.add(similaritiesList.get(i).get(j) / maximums[j]); 
			}
			normalized.add(similarities);
		}
		return normalized;
	}	

	public static double computeLinearWeightedCombination(List<Double> similarities,
			double[] weights) {
		double sim = 0;
		double weightsSum = 0;
		for (int j = 0; j < weights.length; j++) {
			sim += similarities.get(j) * weights[j];
			weightsSum += weights[j];
		}
		sim /= weightsSum;
		return sim;
	}

	public static double[] computeLwcWeights(List<List<Double>> similaritiesList,
			int i, int matchersSize) {
		double[] weights = new double[matchersSize];
		for (int j = 0; j < weights.length; j++) {
			List<Double> candidatesSim = new ArrayList<Double>();
			for (int k = 0; k < similaritiesList.size(); k++) {
				candidatesSim.add(similaritiesList.get(k).get(j));
			}
			weights[j] = computeConfidence(candidatesSim, i);
		}
		return weights;
	}

	/**
	 * 
	 * 
	 * @param candidatesSim
	 * @param k
	 * @return
	 */
	public static double computeConfidence(List<Double> candidatesSim, int k) {
		double confidence = 0;
		double avg = 0;
		//compute the average excluding the candidate k
		for (int t = 0; t < candidatesSim.size(); t++) {
			if(t != k) avg += candidatesSim.get(t);
		}
		avg = avg / (candidatesSim.size() - 1);
		confidence = candidatesSim.get(k) - avg; 
		confidence = Utility.getSigmoidFunction(confidence);
		return confidence;
	}

	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		//In this case we have to first run just the matchers requiring two passes
		//and we do not care about the similarity
		if(requiresTwoPasses && !firstPassDone){
			for (AbstractInstanceMatcher matcher : matchers) {
				if(matcher.requiresTwoPasses())
					matcher.instanceSimilarity(source, target);
			}			
			return 0.0;
		}
		List<Double> similarities = instanceSimilarities(source, target);

		if(generateReport && instanceMatchingReport != null){
			//System.out.println(source.getUri().startsWith("text"));
			instanceMatchingReport.putSim(source.getUri() + "||" + target.getUri(), similarities);
		}

		return combination.combine(similarities);
	}

	public List<Double> instanceSimilarities(Instance source, Instance target) throws Exception{
		List<Double> similarities = new ArrayList<Double>();
		for (AbstractInstanceMatcher matcher : matchers) {
			similarities.add(matcher.instanceSimilarity(source, target));
		}		
		return similarities;
	}

	public void setCombination(CombinationFunction combination) {
		this.combination = combination;
	}

	@Override
	public String getName() {
		return "Generic Instance Matcher" + matchers + (useAdditionalFeatures ? " LWC" : "");
	}

	@Override
	public String toString() {
		return matchers.toString();
	}

	@Override
	public boolean requiresTwoPasses() {
		for (AbstractInstanceMatcher matcher : matchers) {
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
