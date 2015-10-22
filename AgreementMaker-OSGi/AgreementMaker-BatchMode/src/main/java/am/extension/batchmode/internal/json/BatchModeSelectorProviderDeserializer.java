package am.extension.batchmode.internal.json;

import am.extension.batchmode.api.BatchModeSelectorProvider;
import am.extension.batchmode.internal.providers.SelectorProviderFromClasspath;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class BatchModeSelectorProviderDeserializer extends StdDeserializer<BatchModeSelectorProvider> {

    public BatchModeSelectorProviderDeserializer() {
        super(BatchModeSelectorProvider.class);
    }

    @Override
    public BatchModeSelectorProvider deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        BatchModeSelectorProvider provider;

        JsonToken currentToken2 = jsonParser.nextToken();
        if(currentToken2 != JsonToken.START_OBJECT) {
            throw new JsonParseException("Expecting a start object.", jsonParser.getCurrentLocation());
        }

        JsonToken token = jsonParser.nextToken();
        if(token != JsonToken.FIELD_NAME) {
            throw new JsonParseException("Expecting a field.", jsonParser.getCurrentLocation());
        }

        switch(jsonParser.getCurrentName()) {
            case "class": {
                JsonToken nextToken = jsonParser.nextToken();
                if(nextToken != JsonToken.VALUE_STRING) {
                    throw new JsonParseException("Expecting a string value.", jsonParser.getCurrentLocation());
                }
                provider = new SelectorProviderFromClasspath(jsonParser.getText());
                break;
            }
            default: {
                throw new JsonParseException("Unknown matcher type.", jsonParser.getCurrentLocation());
            }
        }

        if(jsonParser.nextToken() != JsonToken.END_OBJECT) {
            throw new JsonParseException("Expecting an object to end.", jsonParser.getCurrentLocation());
        }

        return provider;
    }
}
