package am.extension.multiUserFeedback.selection.strategy;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.selection.CandidateSelection;

/**
 * Quality-based Ranking
 * 
 * 
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 * @param <T> A UFLExperiment subclass.
 */
public class QualityBasedRanking<T extends UFLExperiment> extends CandidateSelection<T> {

	@Override
	public void rank(T exp) {
		
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

	@Override
	public Mapping getCandidateMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping getSelectedMapping() {
		// TODO Auto-generated method stub
		return null;
	}

}
