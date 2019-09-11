package am.extension.multiUserFeedback.selection;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.logic.PersistentSequentialControlLogic;
import am.extension.userfeedback.rankingStrategies.DisagreementRanking;
import am.extension.userfeedback.rankingStrategies.IntrinsicQualityRanking;
import am.extension.userfeedback.rankingStrategies.MultiSelectedRanking;
import am.extension.userfeedback.rankingStrategies.RevalidationRanking;
import am.extension.userfeedback.rankingStrategies.StrategyInterface;

/**
 * NOTE: This class is designed to work with {@link PersistentSequentialControlLogic}
 */
public class MultiStrategyCandidateSelection extends MUCandidateSelection<MUExperiment> {

	private ParametricCandidateSelection pcs;
	
	@Override
	public void rank(MUExperiment experiment) {
		if( pcs == null ) initialize(experiment);
		pcs.rank(experiment);
		done();
	}

	private void initialize(MUExperiment experiment) {
		boolean staticCS = experiment.setup.parameters.getBooleanParameter(Parameter.STATIC_CANDIDATE_SELECTION);
		
		pcs = new ParametricCandidateSelection();
		
		StrategyInterface[] strategies;
		
		String[] metric=experiment.setup.parameters.getArrayParameter(Parameter.CS_METRICS_LIST);
		if (metric!=null)
		{
			double revalidationRate= experiment.setup.parameters.getDoubleParameter(Parameter.REVALIDATION_RATE);
			if( (staticCS) || (revalidationRate==0.0) )
				strategies = new StrategyInterface[1];
			else
			{
				RevalidationRanking rr = new RevalidationRanking(experiment);
				rr.setPriority(1);		
				strategies=new StrategyInterface[2];
				double revRate = experiment.setup.parameters.getDoubleParameter(Parameter.REVALIDATION_RATE);
				rr.setPercentage(revRate);
				strategies[1] = rr;
			}
			String combinationMethod=experiment.setup.parameters.getParameter(Parameter.CS_COMBINATION_METHOD);
			MultiSelectedRanking msr=new MultiSelectedRanking(experiment, metric, combinationMethod, null);
			msr.setPriority(0);		
			strategies[0] = msr;

		}
		else
		{
			if( staticCS )
				strategies = new StrategyInterface[2];
			else
				strategies = new StrategyInterface[3];
			
			DisagreementRanking dr = new DisagreementRanking(experiment);
			IntrinsicQualityRanking mqr = new IntrinsicQualityRanking(experiment);
			RevalidationRanking rr = new RevalidationRanking(experiment);
			
			 dr.setPriority(0);
			mqr.setPriority(1);
			 rr.setPriority(2);
			
			strategies[0] = dr;
			strategies[1] = mqr;
			if( !staticCS ) 
			{
				double revRate = experiment.setup.parameters.getDoubleParameter(Parameter.REVALIDATION_RATE);
				rr.setPercentage(revRate);
				strategies[2] = rr;
			}
		
		}
		pcs.setStrategies(strategies);
	}

	@Override
	public Mapping getSelectedMapping() {
		return pcs.getSelectedMapping();
	}
	
	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking, String id) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<Mapping> getRankedMappings(String id) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public void rank(MUExperiment exp, String id) {
		throw new RuntimeException("Not implemented.");
	}
}