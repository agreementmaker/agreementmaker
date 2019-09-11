package am.extension.multiUserFeedback.selection;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.selection.CandidateSelection;

public abstract class MUCandidateSelection<T extends MUExperiment> extends CandidateSelection<T> {
		
	public abstract void rank( T exp, String id );
		
	public abstract List<Mapping> getRankedMappings(alignType typeOfRanking, String id);
	public abstract List<Mapping> getRankedMappings(String id);
	
	@Override
	public void rank( T exp ) {
		throw new RuntimeException("Use rank(experiment, id).");
	}
	
	@Override
	public List<Mapping> getRankedMappings() {
		throw new RuntimeException("Use getRankedMappings(String id)");
	}
	
	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking) {
		throw new RuntimeException("Use getRankedMappings(alignType typeOfRanking, String id)");
	}
}
