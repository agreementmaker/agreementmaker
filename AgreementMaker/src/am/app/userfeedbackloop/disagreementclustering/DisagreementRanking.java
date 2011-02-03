package am.app.userfeedbackloop.disagreementclustering;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.userfeedbackloop.CandidateSelection;
import am.app.userfeedbackloop.ExecutionSemantics;
import am.evaluation.disagreement.variance.VarianceDisagreement;
import am.evaluation.disagreement.variance.VarianceDisagreementParameters;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;


public class DisagreementRanking extends CandidateSelection {

	private List<Mapping> rankedClassMappings;
	private List<Mapping> rankedPropertyMappings;
	
	
	@Override public List<Mapping> getRankedMappings(alignType t) { 
		if( t == alignType.aligningClasses ) { return rankedClassMappings; }
		if( t == alignType.aligningProperties ) { return rankedPropertyMappings; }

		return null;
	}

	@Override
	public void rank(ExecutionSemantics ex) {

		// get the matchers from the execution semantics
		List<AbstractMatcher> matchers = ex.getComponentMatchers();
		
		// setup the variance disagreement calculation
		VarianceDisagreementParameters disagreementParams = new VarianceDisagreementParameters();
		disagreementParams.setMatchers(matchers);
		
		VarianceDisagreement disagreementMetric = new VarianceDisagreement();
		disagreementMetric.setParameters(disagreementParams);
		
		// run the disagreement calculations
		SimilarityMatrix classDisagreement = disagreementMetric.getDisagreementMatrix(VisualizationType.CLASS_MATRIX);
		rankedClassMappings = classDisagreement.getOrderedMappingsAboveThreshold(0.0d);
		classDisagreement = null;  // release the memory used by this
		
		SimilarityMatrix propertyDisagreement = disagreementMetric.getDisagreementMatrix(VisualizationType.PROPERTIES_MATRIX);
		rankedPropertyMappings = propertyDisagreement.getOrderedMappingsAboveThreshold(0.0d);
		propertyDisagreement = null;
		
		done();
	}


}
