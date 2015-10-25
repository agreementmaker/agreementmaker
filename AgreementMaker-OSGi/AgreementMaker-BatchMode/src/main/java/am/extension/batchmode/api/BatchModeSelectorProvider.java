package am.extension.batchmode.api;

import am.app.mappingEngine.SelectionAlgorithm;
import am.extension.batchmode.internal.providers.SelectorProviderFromClasspath;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = SelectorProviderFromClasspath.class)
public interface BatchModeSelectorProvider {
    public SelectionAlgorithm getSelector();
}
