package am.extension.batchmode.internal.json;

import am.extension.batchmode.internal.providers.MatcherProviderFromClasspath;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class MatcherProviderFromClasspathSerializer extends StdSerializer<MatcherProviderFromClasspath> {

    public MatcherProviderFromClasspathSerializer() {
        super(MatcherProviderFromClasspath.class);
    }

    @Override
    public void serialize(MatcherProviderFromClasspath providerFromClasspath, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("class", providerFromClasspath.getCanonicalClassName());
        jsonGenerator.writeEndObject();
    }
}
