package am.extension.batchmode.api;

import am.extension.batchmode.internal.providers.OntologyFile;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.io.InputStream;

@JsonDeserialize(as = OntologyFile.class)
public interface BatchModeOntologyProvider {
    public InputStream getContent() throws IOException;
}
