package am.extension.batchmode.internal;

import am.extension.batchmode.api.BatchModeFileWriter;
import am.extension.batchmode.api.BatchModeSpec;
import am.extension.batchmode.internal.json.BatchModeSpecSerializer;
import am.extension.batchmode.internal.json.MatcherProviderFromClasspathSerializer;
import am.extension.batchmode.internal.json.OntologyFileSerializer;
import am.extension.batchmode.internal.json.SelectorProviderFromClasspathSerializer;
import am.extension.batchmode.internal.json.WriteOAEIToFileSerializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.OutputStream;

public class BatchModeFileWriterImpl implements BatchModeFileWriter {
    @Override
    public void write(OutputStream output, BatchModeSpec batchModeSpec) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationFeature.INDENT_OUTPUT, true);
        SimpleModule bmModule = new SimpleModule("BatchModeSpec", new Version(1,0,0,null));
        bmModule.addSerializer(new BatchModeSpecSerializer());
        bmModule.addSerializer(new OntologyFileSerializer());
        bmModule.addSerializer(new MatcherProviderFromClasspathSerializer());
        bmModule.addSerializer(new SelectorProviderFromClasspathSerializer());
        bmModule.addSerializer(new WriteOAEIToFileSerializer());
        om.registerModule(bmModule);
        om.writeValue(output, batchModeSpec);
    }
}
