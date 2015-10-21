package am.extension.batchmode.internal.json;

import am.extension.batchmode.internal.providers.OntologyFile;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class OntologyFileSerializer extends StdSerializer<OntologyFile> {

    public OntologyFileSerializer() {
        super(OntologyFile.class);
    }

    @Override
    public void serialize(OntologyFile ontologyFile, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("file", ontologyFile.getFilePath());
        jsonGenerator.writeEndObject();
    }
}
