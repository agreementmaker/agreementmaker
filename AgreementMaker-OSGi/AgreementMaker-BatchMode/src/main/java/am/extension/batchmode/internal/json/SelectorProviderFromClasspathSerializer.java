package am.extension.batchmode.internal.json;

import am.extension.batchmode.internal.providers.SelectorProviderFromClasspath;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SelectorProviderFromClasspathSerializer extends StdSerializer<SelectorProviderFromClasspath> {

    public SelectorProviderFromClasspathSerializer() {
        super(SelectorProviderFromClasspath.class);
    }

    @Override
    public void serialize(SelectorProviderFromClasspath providerFromClasspath, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("class", providerFromClasspath.getCanonicalClassName());
        jsonGenerator.writeEndObject();
    }
}
