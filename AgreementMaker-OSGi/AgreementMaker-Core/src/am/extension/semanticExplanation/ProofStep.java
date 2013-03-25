package am.extension.semanticExplanation;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * Every ProofStep will have 
 * 1. list of inferenceSteps- which supports the ProofStep's conclusion. 
 * 2. list of assumptions discharged during the ProofStep.
 * 3. type of ProofType 
 * @author jjosep37
 *
 */

public class ProofStep {
	List<AxiomRelation> inferenceSteps;
	List<AxiomRelation> assumptionsDischarged;
	AxiomRelation conclusion;
	ProofType type;
	
	
	public List<AxiomRelation> getInferenceSteps() {
		return inferenceSteps;
	}
	public void setInferenceSteps(List<AxiomRelation> inferenceSteps) {
		this.inferenceSteps = inferenceSteps;
	}
	public List<AxiomRelation> getAssumptionsDischarged() {
		return assumptionsDischarged;
	}
	public void setAssumptionsDischarged(List<AxiomRelation> assumptionsDischarged) {
		this.assumptionsDischarged = assumptionsDischarged;
	}
	public AxiomRelation getConclusion() {
		return conclusion;
	}
	public void setConclusion(AxiomRelation conclusion) {
		this.conclusion = conclusion;
	}
	public ProofType getType() {
		return type;
	}
	public void setType(ProofType type) {
		this.type = type;
	}
	
	public ProofStep(List<AxiomRelation> inferenceSteps,
			List<AxiomRelation> assumptionsDischarged,
			AxiomRelation conclusion, ProofType type) {
		super();
		this.inferenceSteps = inferenceSteps;
		this.assumptionsDischarged = assumptionsDischarged;
		this.conclusion = conclusion;
		this.type = type;
	}
	
	public ProofStep() {
		this.inferenceSteps = new ArrayList<AxiomRelation>();
		this.assumptionsDischarged = new ArrayList<AxiomRelation>();
		this.conclusion = new AxiomRelation();
		this.type = ProofType.NOTDEFINED;
	}
	
}


/**
 * A Proof can be of type: Unconditional, Conditional, or by default- NotDefined.
 * @author jjosep37
 *
 */
enum ProofType{
	UNCONDITIONAL, CONDITIONAL, NOTDEFINED;
}