package am.app.mappingEngine.abstractMatcherNew;

public class AbstractSelectionParameters {
	
	/**
	 * This is the threshold used during the selection phase
	 */
	protected double threshold;
	
	/**
	 * This is the maximum cardinality from the source
	 * TODO: rename it in "maxSourceCardinality
	 */
	protected int maxSourceAlign;
	/**
	 * This is the maximum cardinality from the target
	 * TODO: rename it in "maxTargetCardinality
	 */
	protected int maxTargetAlign;
	/**
	 * ANY means any number of relations for source or target (used for cardinalities)
	 */
	public final static int ANY_INT = Integer.MAX_VALUE;

	/**
	 * True means that AM should show its alignments
	 */
	protected boolean isShown;
}
