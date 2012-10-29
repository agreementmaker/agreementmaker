package am.app.mappingEngine.instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import am.AMException;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.InstanceMatchingReport;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.instance.EntityTypeMapper.EntityType;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.InstanceDataset;
import am.app.ontology.instance.ScoredInstance;
import am.app.ontology.instance.ScoredInstanceComparator;
import am.userInterface.MatchingProgressDisplay;

/**
 * <p>
 * Represents an instance matching algorithm. This extends the standard
 * AbstractMatcher framework, but is meant for matching instances.
 * </p>
 * 
 * <p>
 * NOTE: If you want to match only instances and not do any schema matching,
 * you can set {@link AbstractMatcher#alignProp} and
 * {@link AbstractMatcher#alignClass} to false.
 * </p>
 * 
 * <p>
 * HISTORICAL NOTE: This work is after Federico's implementation, where he added
 * the instance matching functionality directly into AbstractMatcher. I am
 * separating those features out of AbstractMatcher in order to simplify
 * AbstractMatcher. -- Cosmin.
 * </p>
 * 
 * @author Cosmin Stroe
 * 
 */
public abstract class AbstractInstanceMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -9168709062686600220L;

	private static final Logger sLog = Logger.getLogger(AbstractInstanceMatcher.class);
	
	/** True if the matcher will match instances, false otherwise. */
	protected boolean alignInstances;


	protected transient InstanceDataset sourceInstanceDataset;
	protected transient InstanceDataset targetInstanceDataset;

	protected transient List<MatchingPair> instanceAlignmentSet;

	protected InstanceMatchingReport instanceMatchingReport;

	protected boolean requiresTwoPasses;

	protected boolean firstPassDone;

	public AbstractInstanceMatcher() {
		super();
	}

	public AbstractInstanceMatcher(DefaultInstanceMatcherParameters param) {
		super(param);
	}
	
	@Override
	protected void align() throws Exception {
		super.align(); // do the schema matching part .. if any.

		if (alignInstances == true
				&& (sourceOntology.getInstances() == null || 
				targetOntology.getInstances() == null)) {
			// the source ontology or target ontology have no instances
			for (MatchingProgressDisplay mpd : progressDisplays) {
				mpd.appendToReport("Instances were NOT matched since they were not loaded properly.");
			}
		}
		else if( alignInstances && !this.isCancelled() ){
			sourceInstanceDataset = sourceOntology.getInstances();
			targetInstanceDataset = targetOntology.getInstances();

			// compile a list of source instances
			if( !sourceInstanceDataset.isIterable() && !targetInstanceDataset.isIterable() ) {
				throw new AMException("Neither the source or the target instance datasets are iterable.  We cannot instance match.");
			}

			if( !sourceInstanceDataset.isIterable() ) {
				throw new AMException("The source MUST be an iterable instance dataset.");
			}

			Iterator<Instance> sourceInstances = sourceInstanceDataset.getInstances();

			// for every individual in the source list, look for candidate individuals in the target
			instanceAlignmentSet = alignInstances(sourceInstances);
		}
	}

	@Override
	protected void setupProgress() {
		super.setupProgress();

		if(alignInstances){
			try {
				InstanceDataset instances = sourceOntology.getInstances();
				if(instances != null && instances.isIterable())    		
					stepsTotal += sourceOntology.getInstances().size();
			} catch (Exception e) {
				sLog.error("", e);
			}
		}
	}

	/** Possibly remove this function. -- Cosmin. */
	public boolean isUseInstanceSchemaMappings() {
		return ((DefaultInstanceMatcherParameters)param).useInstanceSchemaMappings;
	}

	/** Possibly remove this function. -- Cosmin. */
	public void setUseInstanceSchemaMappings(boolean useInstanceSchemaMappings) {
		((DefaultInstanceMatcherParameters)param).useInstanceSchemaMappings = useInstanceSchemaMappings;
	}

	public boolean requiresTwoPasses() {
		return requiresTwoPasses;
	}

	//Invoked by wrapping matchers which run multiple passes on inner matchers  
	public void passStart() {}

	//Invoked by wrapping matchers which run multiple passes on inner matchers  
	public void passEnd() {
		if(!firstPassDone) firstPassDone = true;
	}

	public InstanceMatchingReport getInstanceMatchingReport() {
		return instanceMatchingReport;
	}

	/**
	 * 
	 * @param sourceInstances
	 * @return
	 * @throws Exception
	 */
	protected List<MatchingPair> alignInstances(Iterator<Instance> sourceInstances) throws Exception {

		List<MatchingPair> mappings = new ArrayList<MatchingPair>();

		while (sourceInstances.hasNext()) {
			Instance currentInstance = sourceInstances.next();
			Set<String> labelList = currentInstance.getProperty(Instance.INST_LABEL);

			if(labelList == null) continue;    		

			// FIXME: Manage multiple labels.
			String label = labelList.iterator().next();

			label = processLabelBeforeCandidatesGeneration(label, currentInstance.getType());

			String sourceType = currentInstance.getTypeValue();
			List<MatchingPair> targetTypes = null;

			if( sourceType != null ) {
				if(isUseInstanceSchemaMappings() && sourceOntology.getInstanceTypeMapping() != null){
					HashMap<String, List<MatchingPair>> typeMapping = sourceOntology.getInstanceTypeMapping();
					if( typeMapping.containsKey(sourceType) ) {
						targetTypes = typeMapping.get(sourceType);
					} else {
						targetTypes = null;
						//targetTypes = new ArrayList<MatchingPair>();
						//targetTypes.add( new MatchingPair(sourceType, sourceType)); // same type in the target
					}
				}

			}

			//targetTypes = null;


			List<Instance> allCandidates = new ArrayList<Instance>();

			MatchingPair mapping = null;
			if( targetTypes != null ){
				for( MatchingPair mp : targetTypes ) {
					List<Instance> targetCandidates = targetInstanceDataset.getCandidateInstances(label, mp.targetURI);
					allCandidates.addAll(targetCandidates);
				}
				mapping = alignInstanceCandidates(currentInstance, allCandidates);
			}
			else{
				List<Instance> targetCandidates = targetInstanceDataset.getCandidateInstances(label, null);
				allCandidates.addAll(targetCandidates);
				mapping = alignInstanceCandidates(currentInstance, allCandidates);
			}

			if(mapping != null) mappings.add(mapping);

			//			stepDone();
			//			updateProgress();
		}
		return mappings;
	}


	//It may be overridden by instance matcher, but also used outside
	public MatchingPair alignInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		List<ScoredInstance> scoredCandidates = rankInstanceCandidates(sourceInstance, targetCandidates);

		//System.out.println(scoredCandidates);

		ScoredInstance scoredInstance = selectInstanceFromCandidates(scoredCandidates);
		if(scoredInstance == null) return null;
		return new MatchingPair(sourceInstance.getUri(), scoredInstance.getInstance().getUri(), scoredInstance.getScore(), MappingRelation.EQUIVALENCE);	
	}

	/**
	 * Basic implementation of the label processing before candidates retrieval. The type is not actually used
	 * but may be used by overriding matchers 
	 */
	public String processLabelBeforeCandidatesGeneration(String label, EntityType type) {
		//Remove parenthesis and text inside 
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
		//swaps text before and after comma. This is not safe! 
		if(label.contains(",")){
			String[] split = label.split(",");
			label = split[1].trim() + " " + split[0].trim();
		}

		String[] split = label.split(" ");

		label = "";
		for (int i = 0; i < split.length; i++) {
			if(split[i].length() == 1) continue;
			label += split[i] + " ";
		}
		label = label.trim();
		return label; 
	}

	//It may be overridden by instance matcher, but also used outside
	public List<ScoredInstance> rankInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		double similarity;
		List<ScoredInstance> scoredCandidates = new ArrayList<ScoredInstance>();
		for (Instance candidate: targetCandidates) {
			similarity = instanceSimilarity(sourceInstance, candidate);
			//System.out.println("sim: " + similarity);
			scoredCandidates.add(new ScoredInstance(candidate, similarity));
		}

		Collections.sort(scoredCandidates, new ScoredInstanceComparator());	

		return scoredCandidates;
	}

	public List<ScoredInstance> filterInstanceCandidates(List<ScoredInstance> candidates){
		return ScoredInstance.filter(candidates, 0.01);
	}

	/**
	 * Currently returns the top instance from the scored candidates.
	 */
	public ScoredInstance selectInstanceFromCandidates(List<ScoredInstance> scoredCandidates) {
		if (scoredCandidates.size() > 0
				&& scoredCandidates.get(0).getScore() > param.threshold)
			return scoredCandidates.get(0);
		return null;

		//		scoredCandidates = filterInstanceCandidates(scoredCandidates);
		//		
		//		if(scoredCandidates.size() == 1){
		//			double candidateScore = scoredCandidates.get(0).getScore();
		//			System.out.println(param.threshold);
		//			if (candidateScore < param.threshold) return null;
		//			return scoredCandidates.get(0);
		//		}			
		//		return null;
	}

	/**
	 * Compute the similarity between to instances.
	 * @return A value between 0.0 (not matching) and 1.0 (exact match).
	 * @throws Exception
	 */
	public abstract double instanceSimilarity(Instance source, Instance target) throws Exception;
}
