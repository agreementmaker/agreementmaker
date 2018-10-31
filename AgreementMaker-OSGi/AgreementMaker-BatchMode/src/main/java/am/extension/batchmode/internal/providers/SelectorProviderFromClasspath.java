package am.extension.batchmode.internal.providers;

import am.app.mappingEngine.SelectionAlgorithm;
import am.extension.batchmode.api.BatchModeSelectorProvider;

public class SelectorProviderFromClasspath extends ProviderFromClasspath implements BatchModeSelectorProvider {
    public SelectorProviderFromClasspath(String canonicalClassName) {
        super(canonicalClassName);
    }

    @Override
    public SelectionAlgorithm getSelector() {
        return (SelectionAlgorithm) super.getObject();
    }
}
