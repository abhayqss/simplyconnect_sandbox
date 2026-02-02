package com.scnsoft.eldermark.consana.sync.server.model.fhir;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Optional;

public class XMedicationActionPlanDeserializer extends JsonDeserializer<XMedicationActionPlan> {

    @Override
    public XMedicationActionPlan deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        var resource = Optional.ofNullable(node).map(n -> n.get("resource"));
        var id = resource.map(n -> n.get("id").textValue()).orElse(null);
        var status = resource.map(n -> n.get("status").textValue()).orElse(null);
        if (id == null || status == null) {
            return null;
        }
        return new XMedicationActionPlan("XMedicationActionPlan/" + id, status);
    }
}
