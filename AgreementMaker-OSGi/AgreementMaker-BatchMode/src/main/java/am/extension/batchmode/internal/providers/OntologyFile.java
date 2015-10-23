package am.extension.batchmode.internal.providers;

import am.extension.batchmode.api.BatchModeOntologyProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OntologyFile implements BatchModeOntologyProvider {
    private final String filePath;

    public OntologyFile(String filePath) {
        this.filePath = filePath;
    }

    @Override
    @JsonIgnore
    public InputStream getContent() throws IOException {
        return new FileInputStream(filePath);
    }

    public String getFilePath() {
        return filePath;
    }
}
