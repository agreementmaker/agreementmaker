package am.api.matching;

import am.api.task.MatchingTask;

/**
 * An ontology matching algorithm.
 */
public interface Matcher {
    /**
     * @return The read-only, inherent properties of this matcher.
     */
    MatcherProperties getProperties();

	/**
	 * The work of the matching algorithm is expected to be done here.
	 */
	MatcherResult match(MatchingTask task);
}