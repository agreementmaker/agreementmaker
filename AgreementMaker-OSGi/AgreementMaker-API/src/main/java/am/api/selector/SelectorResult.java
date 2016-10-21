package am.api.selector;

import am.api.ontology.Class;
import am.api.ontology.Instance;
import am.api.ontology.Property;

import java.util.Optional;

public interface SelectorResult {
    Optional<Alignment<Class>> getClassAlignment();
    Optional<Alignment<Property>> getPropertyAlignment();
    Optional<Alignment<Instance>> getInstanceAlignment();
}
