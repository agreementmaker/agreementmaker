package am.extension.batchmode.internal.json;

import am.extension.batchmode.api.BatchModeSpec;
import am.extension.batchmode.api.BatchModeTask;
import am.extension.batchmode.internal.BatchModeSpecImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Iterator;

public class BatchModeSpecDeserializer extends StdDeserializer<BatchModeSpec> {
    public BatchModeSpecDeserializer() {
        super(BatchModeSpec.class);
    }

    @Override
    public BatchModeSpec deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        BatchModeSpecImpl spec = new BatchModeSpecImpl();

        if(jsonParser.nextToken() != JsonToken.FIELD_NAME ) {
            throw new JsonParseException("Expecting field name.", jsonParser.getCurrentLocation());
        } else if(!("tasks".equals(jsonParser.getCurrentName()))) {
            throw new JsonParseException("Expecting tasks field.", jsonParser.getCurrentLocation());
        }

        if(jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new JsonParseException("Expecting an array of tasks.", jsonParser.getCurrentLocation());
        }

        Iterator<BatchModeTask> taskIterator = jsonParser.readValuesAs(BatchModeTask.class);
        while(taskIterator.hasNext()) {
            spec.addTask(taskIterator.next());
        }

        JsonToken token1 = jsonParser.nextToken();
        if(token1 != JsonToken.END_OBJECT) {
            throw new JsonParseException("The end of an object.", jsonParser.getCurrentLocation());
        }

        return spec;
    }
}
