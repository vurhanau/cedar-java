package com.cedarpolicy.serializer;

import com.cedarpolicy.model.exception.InvalidValueDeserializationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class JsonEUIDDeserializer extends JsonDeserializer<JsonEUID> {
    private static final String FieldExpr = "__expr";
    private static final String FieldType = "type";
    private static final String FieldId = "id";

    @Override
    public JsonEUID deserialize(JsonParser jsonParser,
                                DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String euid;
        if (node.has(FieldExpr)) {
            euid = node.get(FieldExpr).textValue();
        } else if (HasTypeAndId(node)) {
            euid = GetEUID(node);
        } else {
            throw new InvalidValueDeserializationException(
                String.format("Use %s or {%s, %s}: %s", FieldExpr, FieldType, FieldId, node)
            );
        }

        return new JsonEUID(euid);
    }

    static boolean HasTypeAndId(JsonNode node) {
        return node.has(FieldType) && node.has(FieldId);
    }

    static String GetEUID(JsonNode node) {
        String type = node.get(FieldType).textValue();
        String id = node.get(FieldId).textValue();

        return String.format("%s::\"%s\"", type, id);
    }
}
