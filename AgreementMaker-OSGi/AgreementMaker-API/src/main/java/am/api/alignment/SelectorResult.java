package am.api.alignment;

import am.api.ontology.Class;
import am.api.ontology.Instance;
import am.api.ontology.Property;

public interface SelectorResult {
    Alignment<Class> getClassAlignment();
    Alignment<Property> getPropertyAlignment();
    Alignment<Instance> getInstanceAlignment();
}
