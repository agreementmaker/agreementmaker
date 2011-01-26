/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.sfm;

import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.structuralMatchers.SimilarityFloodingParameters;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.FullGraphMatcher;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WrappingGraph;

/**
 * @author michele
 *
 */
public class IndipendentSimilarityFlooding extends FullGraphMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7551045342225723696L;

	/**
	 * 
	 */
	public IndipendentSimilarityFlooding() {
		super();
		minInputMatchers = 0;
		maxInputMatchers = 0;
	}

	/**
	 * @param params_new
	 */
	public IndipendentSimilarityFlooding(SimilarityFloodingParameters params_new) {
		super(params_new);
		minInputMatchers = 0;
		maxInputMatchers = 0;
	}
	
	/**
	 *
	 */
	@Override
	protected void loadSimilarityMatrices(WrappingGraph s, WrappingGraph t){
		// load classesMatrix
		classesMatrix = new ArraySimilarityMatrix(sourceOntology.getClassesList().size(),
				targetOntology.getClassesList().size(),
				alignType.aligningClasses);
		// load propertiesMatrix
		propertiesMatrix = new ArraySimilarityMatrix(sourceOntology.getPropertiesList().size(),
				targetOntology.getPropertiesList().size(),
				alignType.aligningProperties);
	}

}
