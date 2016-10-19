package am.api.matching;

import am.api.ontology.Ontology;

import java.util.Properties;

/**
 * <p>
 * A matching algorithm takes as input two {@link Ontology ontologies} and
 * produces a {@link MatcherResult matching result}.
 * </p>
 */
public interface Matcher {
    /**
     * Configure the matcher with a set of properties.
     */
	void configure(Properties properties);

    /**
     * {@link MatcherProperties} defines property keys used by AgreementMaker.
     * @return The properties for this matcher.
     */
    Properties getProperties();

	/**
	 * The work of the matching algorithm is expected to be done here.
	 */
	MatcherResult match(Ontology source, Ontology target);
}