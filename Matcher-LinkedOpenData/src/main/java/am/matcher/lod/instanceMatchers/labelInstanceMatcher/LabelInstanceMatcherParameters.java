package am.matcher.lod.instanceMatchers.labelInstanceMatcher;

import am.app.mappingEngine.instance.DefaultInstanceMatcherParameters;
import am.app.similarity.StringMetrics;

/**
 * Parameters for the {@link LabelInstanceMatcher}.
 * 
 * @author Federico Caimi
 * @author Cosmin Stroe
 */
public class LabelInstanceMatcherParameters extends DefaultInstanceMatcherParameters {
	
	private static final long serialVersionUID = -2178498388995325596L;
	
	/**
	 * The similarity metric which the matcher will use when comparing strings.
	 */
	public StringMetrics metric;
	
	/**
	 * <p>
	 * Instead of using the generic string similarity when comparing labels, use
	 * a custom string similarity for each type of instances. For example, the
	 * labels of people may be compared differently than the labels of
	 * organizations. If this parameter is false, then we will use a generic way
	 * of computing string similarity, ignoring entity types.
	 * </p>
	 * 
	 * <p>
	 * NOTE: This will only work if the source and target instance types match.
	 * If they don't match, the generic string similarity will be used. Also,
	 * the typed similarity will be used only if the algorithm has a custom
	 * similarity for that type, otherwise it will again default to the generic
	 * string similarity.
	 * </p>
	 */
	public boolean computeTypedSimilarity = false;
}
