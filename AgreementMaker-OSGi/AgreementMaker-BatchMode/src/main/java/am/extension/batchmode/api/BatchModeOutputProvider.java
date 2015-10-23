package am.extension.batchmode.api;

import am.app.mappingEngine.SelectionResult;
import am.extension.batchmode.internal.providers.WriteOAEIToFile;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = WriteOAEIToFile.class)
public interface BatchModeOutputProvider {
    public void save(SelectionResult result);
}
