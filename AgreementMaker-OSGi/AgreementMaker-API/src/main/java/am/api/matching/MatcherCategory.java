package am.api.matching;

public interface MatcherCategory {
    String SYNTACTIC     = "syntactic";     // Syntactic Matchers.
    String STRUCTURAL    = "structural";    // Structural Matchers.
    String LEXICAL       = "lexical";       // Matchers that use a dictionary.
    String COMBINATION   = "combination";   // Matchers that produce a combination of other matchers.
    String HYBRID        = "hybrid";        // Matchers that consider many features together.
    String UTILITY       = "utility";       // Utility matcher,
    String INSTANCE      = "instance";      // Instance matcher
    String USER          = "user";          // User matchers
    String UNCATEGORIZED = "uncategorized";	// Matchers that have not been categorized.
}
