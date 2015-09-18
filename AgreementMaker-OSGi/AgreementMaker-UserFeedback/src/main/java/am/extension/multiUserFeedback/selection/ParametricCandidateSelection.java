package am.extension.multiUserFeedback.selection;

import java.util.Arrays;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.rankingStrategies.RevalidationRanking;
import am.extension.userfeedback.rankingStrategies.StrategyInterface;
import am.extension.userfeedback.selection.CandidateSelection;

public class ParametricCandidateSelection extends CandidateSelection<MUExperiment> {
	
	MUExperiment experiment;
	int[] count={0,0,0};
	int total=0;
	
	protected StrategyInterface[] strategies;
	
	public void setStrategies(StrategyInterface ... strategies) {
		this.strategies = strategies;
		
		// compute all selection percentages
		double specifiedPercentage = 0;
		int notSpecifiedCount = 0;
		for(StrategyInterface st : strategies) {
			if( st.getPercentage() > 0d ) { 
				specifiedPercentage += st.getPercentage();
			}
			else {
				notSpecifiedCount++;
			}
		}
		
		double leftOverPercentage = 1.0 - specifiedPercentage;
		if( leftOverPercentage > 0d ) {
			double balancedPercentage = leftOverPercentage / notSpecifiedCount;
			for(StrategyInterface st : strategies) {
				if( st.getPercentage() == 0d )
					st.setPercentage(balancedPercentage);
			}
		}

		Arrays.sort(strategies);
	}
	
	@Override
	public void rank(MUExperiment exp) {
		this.experiment=exp;
		
		total = 0;
		for( StrategyInterface currentStrategy : strategies ) {
			total += currentStrategy.getCount();
		}
		
		boolean staticCS = experiment.setup.parameters.getBooleanParameter(Parameter.STATIC_CANDIDATE_SELECTION);
		
		for( StrategyInterface currentStrategy : strategies ) {
			double currentRate = total == 0 ? 0d : currentStrategy.getCount() / (double)total;
			if( currentRate <= currentStrategy.getPercentage() ) {
				currentStrategy.incrementCount();
				if( !staticCS || (staticCS && experiment.getIterationNumber() == 1)) 
					currentStrategy.rank();
				List<Mapping> rankedList = currentStrategy.getRankedList();
				if( currentStrategy instanceof RevalidationRanking )
					experiment.selectedMapping = rankedList.get(0);
				else
					experiment.selectedMapping = getCandidateMappingFromList(rankedList);
					
				experiment.data.mappingSource = currentStrategy;
				break;
			}
		}
		done();
	}
		
	public Mapping getCandidateMappingFromList(List<Mapping> lst) 
	{

		for( int i = 0; i < lst.size(); i++ ){
			Mapping m = lst.get(i);
			
			if( experiment.correctMappings == null && experiment.incorrectMappings == null ) {
				//lst.remove(i); // optimization
				return m;
			}
						
			if( experiment.correctMappings != null && (experiment.correctMappings.contains(m.getEntity1(),Ontology.SOURCE) != null ||
				experiment.correctMappings.contains(m.getEntity2(),Ontology.TARGET) != null) ) 
			{
				// assume 1-1 mapping, skip already validated mappings.
				continue;
			}
			if( experiment.incorrectMappings != null && experiment.incorrectMappings.contains(m) ) 
				continue; // we've validated this mapping already.
			
			//lst.remove(i); // optimization.
			return m;
		}
		
		return null;
	}

	@Override
	public Mapping getSelectedMapping() {
		return experiment.selectedMapping;
	}

	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> getRankedMappings() {
		// TODO Auto-generated method stub
		return null;
	}

}
