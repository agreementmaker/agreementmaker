package am.api.matching;

import am.api.ontology.Ontology;

import java.util.concurrent.Future;

/**
 * <p>
 * A matching algorithm takes as input two {@link Ontology ontologies} and
 * produces a {@link MatcherResult matching result}.
 * </p>
 */
public interface Matcher {
	/**
	 * The main work of the matching algorithm is expected to be done here. It
	 * is a distinct call because it is expected to take a considerable amount
	 * of time, therefore should be run in a separate thread, and monitored.
	 */
	Future<MatcherResult> match(Ontology source, Ontology target);
}