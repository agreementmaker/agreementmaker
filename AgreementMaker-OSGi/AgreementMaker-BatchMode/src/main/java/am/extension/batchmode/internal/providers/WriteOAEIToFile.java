package am.extension.batchmode.internal.providers;

import am.app.mappingEngine.SelectionResult;
import am.extension.batchmode.api.BatchModeOutputProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WriteOAEIToFile implements BatchModeOutputProvider {
    private final String filePath;

    public WriteOAEIToFile(@JsonProperty("filePath") String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void save(SelectionResult result) {
    }

    public String getFilePath() {
        return filePath;
    }
}
