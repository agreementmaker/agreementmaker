package am.matcher.lod.instanceMatchers;

import java.util.List;

import org.apache.log4j.Logger;

import am.app.mappingEngine.instance.AbstractInstanceMatcher;
import am.app.mappingEngine.instance.DefaultInstanceMatcherParameters;
import am.app.mappingEngine.instance.EntityTypeMapper.EntityType;
import am.app.mappingEngine.utility.MatchingPair;
import am.app.ontology.instance.Instance;
import am.matcher.lod.instanceMatcher.LabelUtils;

/**
 * Implements some basic functions for instance matching.
 * 
 * @author Federico Caimi
 * 
 *         TODO: Decide if this class is actually needed or if it should be
 *         merged into AbstractInstanceMatcher. -- Cosmin.
 */
public abstract class BaseInstanceMatcher extends AbstractInstanceMatcher {

	private static final long serialVersionUID = 4301685403439511365L;
	
	private static Logger log = Logger.getLogger(BaseInstanceMatcher.class);
	
	public BaseInstanceMatcher() {
		super();
	}
	
	public BaseInstanceMatcher(DefaultInstanceMatcherParameters param) {
		super(param);
	}
	
	@Override
	public MatchingPair alignInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		log.debug("SOURCE");
		log.debug(sourceInstance.getSingleValuedProperty("label") + "\t" + sourceInstance.getUri());
		
		log.debug("CANDIDATES (" + targetCandidates.size() + ")");
		
		String solutionURI = null;
		
		boolean foundSolution = false;
		
	         	Instance candidate;
		for (int i = 0; i < targetCandidates.size(); i++) {
			candidate = targetCandidates.get(i);
			
			if(solutionURI != null && solutionURI.equals(candidate.getUri())){
				log.debug("X " + candidate.getSingleValuedProperty("label") + "\t" + candidate.getUri());
				foundSolution = true;
			}
			else log.debug(candidate.getSingleValuedProperty("label") + "\t" + candidate.getUri());
		}
				
		if(!foundSolution) log.info("NON SOLVABLE:\t" + sourceInstance.getSingleValuedProperty("label") + 
				"\t" + processLabelBeforeCandidatesGeneration(sourceInstance.getSingleValuedProperty("label"), sourceInstance.getType()) + "\t" + sourceInstance.getUri());
		
		return null;
	}
	
	@Override
	public String processLabelBeforeCandidatesGeneration(String label, EntityType type) {
		log.debug(label + "\t" + type);
		
		if(type == null) return super.processLabelBeforeCandidatesGeneration(label, type);
		
		if(type == EntityType.ORGANIZATION)
			return LabelUtils.processOrganizationLabel(label);
		
		else if(type == EntityType.PERSON)
			return LabelUtils.processPersonLabel(label);
		
		else if(type == EntityType.LOCATION)
			return LabelUtils.processLocationLabel(label);
		
		return super.processLabelBeforeCandidatesGeneration(label, type);
	}
}
