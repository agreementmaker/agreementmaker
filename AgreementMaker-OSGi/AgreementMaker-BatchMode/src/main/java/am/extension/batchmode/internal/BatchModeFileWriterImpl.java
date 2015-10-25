package am.extension.batchmode.internal;

import am.extension.batchmode.api.BatchModeFileWriter;
import am.extension.batchmode.api.BatchModeSpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.OutputStream;

public class BatchModeFileWriterImpl implements BatchModeFileWriter {
    @Override
    public void write(OutputStream output, BatchModeSpec batchModeSpec) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationFeature.INDENT_OUTPUT, true);
        om.writeValue(output, batchModeSpec);
    }
}
