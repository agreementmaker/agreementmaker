package am.extension.batchmode.internal.providers;

import am.app.mappingEngine.SelectionResult;
import am.extension.batchmode.api.BatchModeOutputProvider;

public class WriteOAEIToFile implements BatchModeOutputProvider {
    private final String filePath;

    public WriteOAEIToFile(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void save(SelectionResult result) {
    }

    public String getFilePath() {
        return filePath;
    }
}
