package am.extension.batchmode.internal.json;

import am.extension.batchmode.api.BatchModeMatcherProvider;
import am.extension.batchmode.api.BatchModeOntologyProvider;
import am.extension.batchmode.api.BatchModeOutputProvider;
import am.extension.batchmode.api.BatchModeSelectorProvider;
import am.extension.batchmode.api.BatchModeTask;
import am.extension.batchmode.internal.BatchModeTaskImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class BatchModeTaskDeserializer extends StdDeserializer<BatchModeTask> {
    public BatchModeTaskDeserializer() {
        super(BatchModeTask.class);
    }

    @Override
    public BatchModeTask deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if(jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Expected start of object.", jsonParser.getCurrentLocation());
        }

        BatchModeTaskImpl task = new BatchModeTaskImpl();
        {
            JsonToken currentToken = jsonParser.nextToken();
            if(currentToken != JsonToken.FIELD_NAME) {
                throw new JsonParseException("Expecting a field name.", jsonParser.getCurrentLocation());
            }

            String currentName = jsonParser.getCurrentName();

            BatchModeOntologyProvider ontProvider = jsonParser.readValueAs(BatchModeOntologyProvider.class);
            if("sourceOntology".equals(currentName)) {
                task.setSourceOntology(ontProvider);
            } else if("targetOntology".equals(currentName)) {
                task.setTargetOntology(ontProvider);
            }
        }{
            JsonToken currentToken = jsonParser.nextToken();
            if(currentToken != JsonToken.FIELD_NAME) {
                throw new JsonParseException("Expecting a field name.", jsonParser.getCurrentLocation());
            }

            String currentName = jsonParser.getCurrentName();

            BatchModeOntologyProvider ontProvider = jsonParser.readValueAs(BatchModeOntologyProvider.class);
            task.setTargetOntology(ontProvider);

            if("sourceOntology".equals(currentName)) {
                task.setSourceOntology(ontProvider);
            } else if("targetOntology".equals(currentName)) {
                task.setTargetOntology(ontProvider);
            }
        }
        {
            JsonToken currentToken = jsonParser.nextToken();
            if(currentToken != JsonToken.FIELD_NAME) {
                throw new JsonParseException("Expecting a field name.", jsonParser.getCurrentLocation());
            }

            String currentName = jsonParser.getCurrentName();

            if(!"matcher".equals(currentName)) {
                throw new JsonParseException("Expecting a matcher.", jsonParser.getCurrentLocation());
            }

            BatchModeMatcherProvider matcherProvider = jsonParser.readValueAs(BatchModeMatcherProvider.class);
            task.setMatcher(matcherProvider);
        }
        {
            JsonToken currentToken = jsonParser.nextToken();
            if(currentToken != JsonToken.FIELD_NAME) {
                throw new JsonParseException("Expecting a field name.", jsonParser.getCurrentLocation());
            }

            String currentName = jsonParser.getCurrentName();

            if(!"selector".equals(currentName)) {
                throw new JsonParseException("Expecting a matcher.", jsonParser.getCurrentLocation());
            }

            BatchModeSelectorProvider selectorProvider = jsonParser.readValueAs(BatchModeSelectorProvider.class);
            task.setSelector(selectorProvider);
        }
        BatchModeOutputProvider outputProvider = jsonParser.readValueAs(BatchModeOutputProvider.class);
        task.setOutput(outputProvider);

        if(jsonParser.nextToken() != JsonToken.END_OBJECT) {
            throw new JsonParseException("Expected start of object.", jsonParser.getCurrentLocation());
        }

        return task;
    }
}
