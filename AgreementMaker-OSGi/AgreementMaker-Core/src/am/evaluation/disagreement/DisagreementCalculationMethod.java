package am.evaluation.disagreement;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public abstract class DisagreementCalculationMethod {

	private List<MatchingTask> availableMatchers;
	
	public void setAvailableMatchers(List<MatchingTask> availableMatchers) {	this.availableMatchers = availableMatchers; }
	public List<MatchingTask> getAvailableMatchers() { return availableMatchers; }
	
	public abstract SimilarityMatrix getDisagreementMatrix( alignType t );
	public abstract DisagreementParameters getParameters();
	public abstract DisagreementParametersPanel getParametersPanel();
	public abstract void setParameters( DisagreementParameters params );
		
}
