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
	HYBRID,			// Matchers that consider many features together.
	UNCATEGORIZED;	// Matchers that have not been categorized.
	
}
