/**
 * 
 */
package am.app.mappingEngine;

/**
 * This enumeration defines categories for matchers.
 * Used for presentation purposes.
 * @author Cosmin Stroe
 * @date Monday, December 13th, 2010.
 */
public enum MatcherCategory {

	SYNTACTIC,		// Syntactic Matchers.
	STRUCTURAL,		// Structural Matchers.
	LEXICAL,		// Matchers that use a dictionary.
	COMBINATION,    // Matchers that produce a combination of other matchers. 
	HYBRID,			// Matchers that consider many features together.
	UTILITY, 		// Utility matcher,
	USER,			// User matchers
	UNCATEGORIZED;	// Matchers that have not been categorized.
	
}
