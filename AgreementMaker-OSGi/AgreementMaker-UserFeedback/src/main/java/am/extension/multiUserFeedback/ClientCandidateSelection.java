package am.extension.multiUserFeedback;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.CandidateSelection;

public class ClientCandidateSelection extends CandidateSelection<MUExperiment>{

	@Override
	public void rank(MUExperiment exp) {
		done();
		
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
		
		//ASK at the server
		
		return null;
	}

	@Override
	public Mapping getSelectedMapping() {
		// TODO Auto-generated method stub
		return null;
	}

}
