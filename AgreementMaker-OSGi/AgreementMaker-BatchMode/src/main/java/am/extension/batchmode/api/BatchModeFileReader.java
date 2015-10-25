package am.extension.batchmode.api;

import java.io.IOException;
import java.io.InputStream;

public interface BatchModeFileReader {
    public BatchModeSpec read(InputStream inputStream) throws IOException;
}
