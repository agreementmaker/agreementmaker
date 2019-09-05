package am.app.mappingEngine.instanceMatchers;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.instanceMatcher.LabelUtils;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.instance.Instance;
import am.utility.referenceAlignment.AlignmentUtilities;


public class BaseInstanceMatcher extends AbstractMatcher{

	private static final long serialVersionUID = 4301685403439511365L;
	
	Logger log;
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		log = Logger.getLogger(BaseInstanceMatcher.class);
		log.setLevel(Level.INFO);
	}
	
	@Override
	protected MatchingPair alignInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		log.debug("SOURCE");
		log.debug(sourceInstance.getSingleValuedProperty("label") + "\t" + sourceInstance.getUri());
		
		log.debug("CANDIDATES (" + targetCandidates.size() + ")");
		
		String solutionURI = null;
		
		if(referenceAlignment != null)
			solutionURI = AlignmentUtilities.candidatesContainSolution(referenceAlignment, 
					sourceInstance.getUri(), targetCandidates);
		
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
	protected void afterAlignOperations() {
		super.afterAlignOperations();
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
