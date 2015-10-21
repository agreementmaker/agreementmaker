package am.extension.batchmode.api;

import java.io.IOException;
import java.io.InputStream;

public interface BatchModeOntologyProvider {
    public InputStream getContent() throws IOException;
}
