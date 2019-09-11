/**
 * 
 */
package am.matcher.structuralMatchers.similarityFlooding.anchorFlood;

import am.matcher.structuralMatchers.SimilarityFloodingParameters;
import am.matcher.structuralMatchers.similarityFlooding.FullGraphMatcher;
import am.matcher.structuralMatchers.similarityFlooding.utils.WrappingGraph;

/**
 * @author michele
 *
 */
public class AnchorFloodMatcher extends FullGraphMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6736429570974261886L;

	/**
	 * 
	 */
	public AnchorFloodMatcher() {
		super();
		minInputMatchers = 1;
		maxInputMatchers = ANY_INT;
	}

	/**
	 * @param params_new
	 */
	public AnchorFloodMatcher(SimilarityFloodingParameters params_new) {
		super(params_new);
		minInputMatchers = 1;
		maxInputMatchers = ANY_INT;
	}

	/**
	 *
	 */
	@Override
	protected void loadSimilarityMatrices(WrappingGraph s, WrappingGraph t) {
		classesMatrix = inputMatchers.get(0).getClassesMatrix();
		propertiesMatrix = inputMatchers.get(0).getPropertiesMatrix();
	}

}
