package am.extension.batchmode.internal.providers;

import am.app.mappingEngine.SelectionAlgorithm;
import am.extension.batchmode.api.BatchModeSelectorProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SelectorProviderFromClasspath extends ProviderFromClasspath implements BatchModeSelectorProvider {
    public SelectorProviderFromClasspath(@JsonProperty("canonicalClassName") String canonicalClassName) {
        super(canonicalClassName);
    }

    @Override
    @JsonIgnore
    public SelectionAlgorithm getSelector() {
        return (SelectionAlgorithm) super.getObject();
    }
}
