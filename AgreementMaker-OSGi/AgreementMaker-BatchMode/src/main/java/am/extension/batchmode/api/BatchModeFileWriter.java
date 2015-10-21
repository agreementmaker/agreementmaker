package am.extension.batchmode.api;

import java.io.IOException;
import java.io.OutputStream;

public interface BatchModeFileWriter {
    public void write(OutputStream output, BatchModeSpec batchModeSpec) throws IOException;
}
