package am.extension.batchmode.api;

import am.app.mappingEngine.SelectionAlgorithm;

public interface BatchModeSelectorProvider {
    public SelectionAlgorithm getSelector();
}
