package am.extension.batchmode.api;

import am.app.mappingEngine.SelectionResult;

public interface BatchModeOutputProvider {
    public void save(SelectionResult result);
}
