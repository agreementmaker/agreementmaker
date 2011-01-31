package am.app.userfeedbackloop;

import java.util.List;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;

public interface CandidateSelection {

	public void rank( ExecutionSemantics ex );
	
	public List<Mapping> getRankedMappings(alignType typeOfRanking);
	
}
