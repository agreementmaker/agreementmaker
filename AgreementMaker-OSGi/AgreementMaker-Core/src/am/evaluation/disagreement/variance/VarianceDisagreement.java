package am.evaluation.disagreement.variance;

import java.util.List;
import java.util.Vector;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.evaluation.disagreement.DisagreementCalculationMethod;
import am.evaluation.disagreement.DisagreementParameters;
import am.evaluation.disagreement.DisagreementParametersPanel;

public class VarianceDisagreement extends DisagreementCalculationMethod {

	private VarianceDisagreementParameters params;
	private Alignment<Mapping> finalMappings;
	private final double weight_value=0.2;
	@Override
	public DisagreementParametersPanel getParametersPanel() { return new VarianceDisagreementPanel(); }

	@Override public DisagreementParameters getParameters() {	return params; }
	@Override public void setParameters(DisagreementParameters params) { 
		this.params = (VarianceDisagreementParameters) params; 
	}
	
	public void setParameters(DisagreementParameters params, Alignment<Mapping> finalMappings) { 
		this.params = (VarianceDisagreementParameters) params; 
		this.finalMappings=finalMappings;
	}
	
	@Override
	public SimilarityMatrix getDisagreementMatrix(alignType t) {
		List<AbstractMatcher> matchersToConsider = params.getMatchers();
		double weight=0.0;
		if( matchersToConsider.size() == 0 ) return null;
		
		if( t == alignType.aligningClasses ) {
		
			int rows = matchersToConsider.get(0).getClassesMatrix().getRows();
			int cols = matchersToConsider.get(0).getClassesMatrix().getColumns();
			
			SimilarityMatrix disagreementMatrix = new ArraySimilarityMatrix(matchersToConsider.get(0).getSourceOntology(), 
					matchersToConsider.get(0).getTargetOntology(), alignType.aligningClasses );
			
			for( int i = 0; i < rows; i++ ) {
				for( int j = 0; j < cols; j++ ) {
					
					Vector<Double> similarityValues = new Vector<Double>();
					for( int k = 0; k < matchersToConsider.size(); k++ ) {
						similarityValues.add(matchersToConsider.get(k).getClassesMatrix().getSimilarity(i, j));
					}
					
					Mapping m = matchersToConsider.get(0).getClassesMatrix().get(i, j);
					if( m == null ) {
						disagreementMatrix.set(i, j, new Mapping(Core.getInstance().getSourceOntology().getClassesList().get(i), Core.getInstance().getTargetOntology().getClassesList().get(j), computeVariance(similarityValues)));
					} else {
						try {
							if (finalMappings.contains(m))
								weight=weight_value;
							else
								weight=0;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
						
						disagreementMatrix.set(i, j, new Mapping(m.getEntity1(), m.getEntity2(), computeVariance(similarityValues)+weight));
					}
				}
			}
			
			return disagreementMatrix;
		} else {
			int rows = matchersToConsider.get(0).getPropertiesMatrix().getRows();
			int cols = matchersToConsider.get(0).getPropertiesMatrix().getColumns();
			
			SimilarityMatrix disagreementMatrix = new ArraySimilarityMatrix(matchersToConsider.get(0).getSourceOntology(),
					matchersToConsider.get(0).getTargetOntology(), alignType.aligningProperties );
			
			for( int i = 0; i < rows; i++ ) {
				for( int j = 0; j < cols; j++ ) {
					Vector<Double> similarityValues = new Vector<Double>();
					for( int k = 0; k < matchersToConsider.size(); k++ ) {
						similarityValues.add(matchersToConsider.get(k).getPropertiesMatrix().getSimilarity(i, j));
					}
					
					Mapping m = matchersToConsider.get(0).getPropertiesMatrix().get(i, j);
					if( m == null ) {
						disagreementMatrix.set(i, j, new Mapping(Core.getInstance().getSourceOntology().getPropertiesList().get(i), Core.getInstance().getTargetOntology().getPropertiesList().get(j), computeVariance(similarityValues)));
					} else {
						disagreementMatrix.set(i, j, new Mapping(m.getEntity1(), m.getEntity2(), computeVariance(similarityValues)));
					}
				}
			}
			
			return disagreementMatrix;
		}
	}

	public static double computeVariance(Vector<Double> similarityValues) {

		// variance is the average of the squares of the deviation of each value
		// deviation is the distance of the value from the mean
		
		// Step 1. Compute the mean.
		int n = 0;
		Double sum = 0d;
		for( Double d: similarityValues ) {
			sum += d;
			n++;
		}
		
		Double mean = sum / n;
		
		// Step 2. Compute the deviation of each value;
		
		Vector<Double> deviationVector = new Vector<Double>();
		for( Double val: similarityValues ) {
			deviationVector.add( val - mean );
		}
		
		// Step 3. Square the deviation values in the deviation Vector.
		
		for( int i = 0; i < deviationVector.size(); i++ ) {
			deviationVector.set(i,  deviationVector.get(i) * deviationVector.get(i) );
		}
		
		// Step 4. Compute the variance, which is the average of the squared deviation.
		Double devSum = 0d;
		for( Double val : deviationVector ) {
			devSum += val;
		}
		
		Double variance = devSum / n;
		
		return variance;
	}

	

}
