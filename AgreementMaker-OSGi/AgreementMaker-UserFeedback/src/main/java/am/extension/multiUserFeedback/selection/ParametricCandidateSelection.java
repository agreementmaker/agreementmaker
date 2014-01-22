package am.extension.multiUserFeedback.selection;

import java.util.Arrays;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.rankingStrategies.StrategyInterface;

public class ParametricCandidateSelection extends MUCandidateSelection<MUExperiment> {
	
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
		done();
	}

	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<Mapping> getRankedMappings() {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public Mapping getCandidateMapping() {
		
		total = 0;
		for( StrategyInterface currentStrategy : strategies ) {
			total += currentStrategy.getCount();
		}
		
		boolean staticCS = experiment.setup.parameters.getBooleanParameter(Parameter.STATIC_CANDIDATE_SELECTION);
		
		for( StrategyInterface currentStrategy : strategies ) {
			double currentRate = currentStrategy.getCount() / (double)total;
			if( currentRate <= currentStrategy.getPercentage() ) {
				currentStrategy.incrementCount();
				if( !staticCS ) currentStrategy.rank();
				experiment.selectedMapping = getCandidateMappingFromList(currentStrategy.getRankedList());
				experiment.data.mappingSource = currentStrategy;
				return experiment.selectedMapping;
			}
		}
		
		return null;
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
	public void rank(MUExperiment exp, String id) {
		throw new RuntimeException("Not implemented.");
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
	public Mapping getCandidateMapping(String id) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public Mapping getSelectedMapping() {
		return experiment.selectedMapping;
	}

}
