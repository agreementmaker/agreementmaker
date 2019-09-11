package am.extension.batchmode.internal;

import am.extension.batchmode.api.BatchModeFileReader;
import am.extension.batchmode.api.BatchModeSpec;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class BatchModeFileReaderImpl implements BatchModeFileReader {
    @Override
    public BatchModeSpec read(InputStream inputStream) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(inputStream, BatchModeSpec.class);
    }
}
