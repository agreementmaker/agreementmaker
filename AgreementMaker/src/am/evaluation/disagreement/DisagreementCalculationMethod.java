package am.evaluation.disagreement;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.SimilarityMatrix;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;

public abstract class DisagreementCalculationMethod {

	private List<AbstractMatcher> availableMatchers;
	
	public void setAvailableMatchers(List<AbstractMatcher> availableMatchers) {	this.availableMatchers = availableMatchers; }
	public List<AbstractMatcher> getAvailableMatchers() { return availableMatchers; }
	
	public abstract SimilarityMatrix getDisagreementMatrix( VisualizationType t );
	public abstract DisagreementParameters getParameters();
	public abstract DisagreementParametersPanel getParametersPanel();
	public abstract void setParameters( DisagreementParameters params );
		
}
