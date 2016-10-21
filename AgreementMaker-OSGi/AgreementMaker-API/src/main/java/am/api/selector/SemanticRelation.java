package am.api.selector;

/**
 * The kind of relation a correspondence is describing.
 */
public enum SemanticRelation {

	/**
	 * The concepts have exactly the same semantics.
	 */
	EQUIVALENT,
	
	/**
	 * The source concept is a subclass of the target concept.
	 */
	SUBCLASSOF
}