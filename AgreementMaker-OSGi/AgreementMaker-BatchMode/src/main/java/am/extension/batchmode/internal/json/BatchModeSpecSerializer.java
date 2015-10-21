package am.extension.batchmode.internal.json;

import am.extension.batchmode.api.BatchModeSpec;
import am.extension.batchmode.api.BatchModeTask;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class BatchModeSpecSerializer extends StdSerializer<BatchModeSpec> {

    public BatchModeSpecSerializer() {
        super(BatchModeSpec.class);
    }

    @Override
    public void serialize(BatchModeSpec batchModeSpec, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeArrayFieldStart("tasks");
        for(BatchModeTask task : batchModeSpec.getTasks()) {
            jsonGenerator.writeObject(task);
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
