package am.extension.multiUserFeedback.selection;

import static am.extension.userfeedback.utility.UFLutility.extractList;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.rankingStrategies.DisagreementRanking;
import am.extension.userfeedback.rankingStrategies.IntrinsicQualityRanking;
import am.extension.userfeedback.rankingStrategies.RevalidationRanking;
import am.extension.userfeedback.rankingStrategies.StrategyInterface;

public class MultiStrategyCandidateSelection extends MUCandidateSelection<MUExperiment> {

	private ParametricCandidateSelection pcs;
	
	@Override
	public void rank(MUExperiment experiment, String id) {
		List<Mapping> toRank = new ArrayList<Mapping>();
		if (experiment.correctMappings!=null)
			toRank.addAll(experiment.correctMappings);
		if (experiment.incorrectMappings!=null)
			toRank.addAll(experiment.incorrectMappings);
		
		DisagreementRanking dr = new DisagreementRanking(
				extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningClasses),
				extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningProperties), 
				experiment.getFeedbackMatrix(alignType.aligningClasses, Validation.CORRECT),
				experiment.getFeedbackMatrix(alignType.aligningClasses, Validation.INCORRECT),
				experiment.getFeedbackMatrix(alignType.aligningProperties, Validation.CORRECT),
				experiment.getFeedbackMatrix(alignType.aligningProperties, Validation.INCORRECT),
				experiment.getComputedUFLMatrix(alignType.aligningClasses), 
				experiment.getComputedUFLMatrix(alignType.aligningProperties));
		
		IntrinsicQualityRanking mqr=new IntrinsicQualityRanking(
				experiment.getComputedUFLMatrix(alignType.aligningClasses),
				experiment.getComputedUFLMatrix(alignType.aligningProperties),
				experiment.getForbiddenPositions(alignType.aligningClasses),
				experiment.getForbiddenPositions(alignType.aligningProperties));
		
		RevalidationRanking rr = new RevalidationRanking(
				extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningClasses),
				extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningProperties),
				experiment.getFeedbackMatrix(alignType.aligningClasses, Validation.CORRECT),
				experiment.getFeedbackMatrix(alignType.aligningClasses, Validation.INCORRECT),
				experiment.getFeedbackMatrix(alignType.aligningProperties, Validation.CORRECT),
				experiment.getFeedbackMatrix(alignType.aligningProperties, Validation.INCORRECT),
				experiment.getComputedUFLMatrix(alignType.aligningClasses), 
				experiment.getComputedUFLMatrix(alignType.aligningProperties),
				toRank,
				experiment.getForbiddenPositions(alignType.aligningClasses),
				experiment.getForbiddenPositions(alignType.aligningProperties));
		
		 dr.setPriority(0);
		mqr.setPriority(1);
		 rr.setPriority(2);
		
		StrategyInterface[] strategies = new StrategyInterface[3];
		strategies[0] = dr;
		strategies[1] = mqr;
		strategies[2] = rr;
		
		pcs = new ParametricCandidateSelection();
		pcs.setStrategies(strategies);
	}

	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> getRankedMappings(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping getCandidateMapping(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rank(MUExperiment exp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mapping getSelectedMapping() {
		// TODO Auto-generated method stub
		return null;
	}

}