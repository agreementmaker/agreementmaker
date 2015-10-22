package am.extension.batchmode.internal;

import am.extension.batchmode.api.BatchModeFileReader;
import am.extension.batchmode.api.BatchModeMatcherProvider;
import am.extension.batchmode.api.BatchModeOntologyProvider;
import am.extension.batchmode.api.BatchModeOutputProvider;
import am.extension.batchmode.api.BatchModeSelectorProvider;
import am.extension.batchmode.api.BatchModeSpec;
import am.extension.batchmode.api.BatchModeTask;
import am.extension.batchmode.internal.json.BatchModeMatcherProviderDeserializer;
import am.extension.batchmode.internal.json.BatchModeOntologyProviderDeserializer;
import am.extension.batchmode.internal.json.BatchModeOutputProviderDeserializer;
import am.extension.batchmode.internal.json.BatchModeSelectorProviderDeserializer;
import am.extension.batchmode.internal.json.BatchModeSpecDeserializer;
import am.extension.batchmode.internal.json.BatchModeTaskDeserializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.InputStream;

public class BatchModeFileReaderImpl implements BatchModeFileReader {
    @Override
    public BatchModeSpec read(InputStream inputStream) throws IOException {
        ObjectMapper om = new ObjectMapper();
        SimpleModule bm = new SimpleModule("BatchModeModule", new Version(0,0,1,null));
        bm.addDeserializer(BatchModeSpec.class, new BatchModeSpecDeserializer());
        bm.addDeserializer(BatchModeTask.class, new BatchModeTaskDeserializer());
        bm.addDeserializer(BatchModeOntologyProvider.class, new BatchModeOntologyProviderDeserializer());
        bm.addDeserializer(BatchModeMatcherProvider.class, new BatchModeMatcherProviderDeserializer());
        bm.addDeserializer(BatchModeSelectorProvider.class, new BatchModeSelectorProviderDeserializer());
        bm.addDeserializer(BatchModeOutputProvider.class, new BatchModeOutputProviderDeserializer());
        om.registerModule(bm);
        return om.readValue(inputStream, BatchModeSpec.class);
    }
}
