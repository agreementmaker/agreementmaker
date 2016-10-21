package am.api.matcher;

public enum MatcherCategory {
    SYNTACTIC,     // Syntactic Matchers
    STRUCTURAL,    // Structural Matchers
    LEXICAL,       // Matchers that use a dictionary
    COMBINATION,   // Matchers that produce a combination of other matchers
    HYBRID,        // Matchers that consider many features together
    UTILITY,       // Utility matcher
    INSTANCE,      // Instance matcher
    USER,          // User matchers
    UNCATEGORIZED, // Matchers that have not been categorized
}
