package com.scnsoft.eldermark.service.document.signature.template.field.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateFieldProperties;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractRequesterFieldJsonSchemaService implements RequesterFieldJsonSchemaService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void populateSchema(DocumentSignatureTemplateField field, DocumentSignatureTemplateFieldProperties properties) {
        try {
            field.setJsonSchema(fillSchema(
                    field.getToolboxRequesterType().getJsonSchema(),
                    json -> fillJsonSchema(json, properties)
            ));
            field.setJsonUiSchema(fillSchema(
                    field.getToolboxRequesterType().getJsonUiSchema(),
                    json -> fillJsonUiSchema(json, properties)
            ));
        } catch (BusinessException e) {
            throw new BusinessException("Invalid field " + field.getName() + " : " + e.getMessage());
        }
    }

    @Override
    public DocumentSignatureTemplateFieldProperties extractFieldProperties(DocumentSignatureTemplateField field) {
        var properties = new DocumentSignatureTemplateFieldProperties();
        fillProperties(field.getJsonSchema(), json -> extractPropertiesFromJsonSchema(json, properties));
        fillProperties(field.getJsonUiSchema(), json -> extractPropertiesFromUiJsonSchema(json, properties));
        return properties;
    }

    private void fillProperties(String schema, Consumer<ObjectNode> filler) {
        try {
            var json = (ObjectNode) objectMapper.readTree(schema);
            filler.accept(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String fillSchema(String schema, Function<ObjectNode, ObjectNode> filler) {
        try {
            var json = (ObjectNode) objectMapper.readTree(schema);
            var resultJson = filler.apply(json);
            return objectMapper.writeValueAsString(resultJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected ObjectNode fillJsonSchema(ObjectNode objectNode, DocumentSignatureTemplateFieldProperties properties) {
        return objectNode;
    }

    protected ObjectNode fillJsonUiSchema(ObjectNode objectNode, DocumentSignatureTemplateFieldProperties properties) {
        return objectNode;
    }

    protected void extractPropertiesFromJsonSchema(ObjectNode objectNode, DocumentSignatureTemplateFieldProperties properties) {
    }

    protected void extractPropertiesFromUiJsonSchema(ObjectNode objectNode, DocumentSignatureTemplateFieldProperties properties) {
    }
}
