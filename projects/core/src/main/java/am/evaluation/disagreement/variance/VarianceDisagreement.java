package am.evaluation.disagreement.variance;

import static am.evaluation.disagreement.variance.VarianceComputation.computeVariance;

import java.util.List;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.evaluation.disagreement.DisagreementCalculationMethod;
import am.evaluation.disagreement.DisagreementParameters;
import am.evaluation.disagreement.DisagreementParametersPanel;

public class VarianceDisagreement extends DisagreementCalculationMethod {

	private VarianceDisagreementParameters params;

	@Override
	public DisagreementParametersPanel getParametersPanel() { return new VarianceDisagreementPanel(); }

	@Override public DisagreementParameters getParameters() {	return params; }
	@Override public void setParameters(DisagreementParameters params) { 
		this.params = (VarianceDisagreementParameters) params; 
	}
	
	@Override
	public SimilarityMatrix getDisagreementMatrix(alignType t) {
		List<MatchingTask> matchersToConsider = params.getMatchers();
		double weight=0.0;
		if( matchersToConsider.size() == 0 ) return null;
		
		if( t == alignType.aligningClasses ) {
		
			MatchingTask firstTask = matchersToConsider.get(0);
			
			int rows = firstTask.matcherResult.getClassesMatrix().getRows();
			int cols = firstTask.matcherResult.getClassesMatrix().getColumns();
			
			SimilarityMatrix disagreementMatrix = new ArraySimilarityMatrix(firstTask.matcherParameters.getSourceOntology(), 
					firstTask.matcherParameters.getTargetOntology(), alignType.aligningClasses );
			
			for( int i = 0; i < rows; i++ ) {
				for( int j = 0; j < cols; j++ ) {
					
					double[] signatureVector = new double[matchersToConsider.size()];
					for( int k = 0; k < matchersToConsider.size(); k++ ) {
						signatureVector[k] = matchersToConsider.get(k).matcherResult.getClassesMatrix().getSimilarity(i, j);
					}
					
					// save the new mapping in the disagreement matrix
					Mapping m = matchersToConsider.get(0).matcherResult.getClassesMatrix().get(i, j);
					if( m == null ) {
						disagreementMatrix.set(i, j, new Mapping(Core.getInstance().getSourceOntology().getClassesList().get(i), Core.getInstance().getTargetOntology().getClassesList().get(j), computeVariance(signatureVector)));
					} else {
						disagreementMatrix.set(i, j, new Mapping(m.getEntity1(), m.getEntity2(), computeVariance(signatureVector)+weight));
					}
				}
			}
			
			return disagreementMatrix;
		} else {
			int rows = matchersToConsider.get(0).matcherResult.getPropertiesMatrix().getRows();
			int cols = matchersToConsider.get(0).matcherResult.getPropertiesMatrix().getColumns();
			
			SimilarityMatrix disagreementMatrix = new ArraySimilarityMatrix(matchersToConsider.get(0).matcherParameters.getSourceOntology(),
					matchersToConsider.get(0).matcherParameters.getTargetOntology(), alignType.aligningProperties );
			
			for( int i = 0; i < rows; i++ ) {
				for( int j = 0; j < cols; j++ ) {
					double[] signatureVector = new double[matchersToConsider.size()];
					for( int k = 0; k < matchersToConsider.size(); k++ ) {
						signatureVector[k] = matchersToConsider.get(k).matcherResult.getPropertiesMatrix().getSimilarity(i, j);
					}
					
					// save the new mapping in the disagreement matrix
					Mapping m = matchersToConsider.get(0).matcherResult.getPropertiesMatrix().get(i, j);
					if( m == null ) {
						disagreementMatrix.set(i, j, new Mapping(Core.getInstance().getSourceOntology().getPropertiesList().get(i), Core.getInstance().getTargetOntology().getPropertiesList().get(j), computeVariance(signatureVector)));
					} else {
						disagreementMatrix.set(i, j, new Mapping(m.getEntity1(), m.getEntity2(), computeVariance(signatureVector)));
					}
				}
			}
			
			return disagreementMatrix;
		}
	}

}
