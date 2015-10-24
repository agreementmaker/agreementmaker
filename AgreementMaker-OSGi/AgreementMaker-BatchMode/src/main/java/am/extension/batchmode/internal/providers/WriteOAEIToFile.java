package am.extension.batchmode.internal.providers;

import am.app.mappingEngine.SelectionResult;
import am.extension.batchmode.api.BatchModeOutputProvider;
import am.parsing.AlignmentOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;

public class WriteOAEIToFile implements BatchModeOutputProvider {
    private final String filePath;

    public WriteOAEIToFile(@JsonProperty("filePath") String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void save(SelectionResult result) throws IOException {
        AlignmentOutput output = new AlignmentOutput(result.getAlignment(), filePath, true);
        String sourceUri = result.getMatchingTask().matcherParameters.getSourceOntology().getURI();
        String targetUri = result.getMatchingTask().matcherParameters.getTargetOntology().getURI();
        output.write(sourceUri, targetUri, sourceUri, targetUri, result.getMatchingTask().matchingAlgorithm.getName());
    }

    public String getFilePath() {
        return filePath;
    }
}
