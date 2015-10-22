package am.extension.batchmode.internal.json;

import am.extension.batchmode.internal.providers.WriteOAEIToFile;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class WriteOAEIToFileSerializer extends StdSerializer<WriteOAEIToFile> {
    public WriteOAEIToFileSerializer() {
        super(WriteOAEIToFile.class);
    }

    @Override
    public void serialize(WriteOAEIToFile writeOAEIToFile, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("oaei", writeOAEIToFile.getFilePath());
        jsonGenerator.writeEndObject();
    }
}
