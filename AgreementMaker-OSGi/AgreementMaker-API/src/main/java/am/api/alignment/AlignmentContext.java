package am.api.alignment;

import am.api.selector.Selector;
import am.api.matcher.Matcher;
import am.api.ontology.Ontology;

import java.util.List;

/**
 * Keeps track of all the information required to produce
 * an {@link am.api.selector.Alignment}, in addition to the settings.
 */
public interface AlignmentContext {
    AlignmentSettings getSettings();
    List<AlignmentResult> getInputs();
    List<Ontology> getOntologies();

    Matcher getMatcher();
    Selector getSelector();

    default Ontology getSourceOntology() {
        return getOntologies().get(0);
    }

    default Ontology getTargetOntology() {
        return getOntologies().get(1);
    }
}
