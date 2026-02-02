package com.scnsoft.eldermark.service.document.signature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.RawValue;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateFieldProperties;
import com.scnsoft.eldermark.entity.signature.*;
import com.scnsoft.eldermark.service.document.signature.template.field.schema.RequesterFieldJsonSchemaService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DocumentSignatureTemplateJsonSchemaServiceImpl implements DocumentSignatureTemplateJsonSchemaService {

    private static final Comparator<DocumentSignatureTemplateField> FIELD_COMPARATOR_BY_PAGE_NO =
            Comparator.comparing(it -> Stream.ofNullable(it.getLocations())
                    .flatMap(Collection::stream)
                    .findFirst()
                    .map(BaseDocumentSignatureField::getPageNo)
                    .orElse(null));

    private static final Comparator<DocumentSignatureTemplateField> FIELD_COMPARATOR_BY_TOP_LEFT_Y =
            Comparator.comparing(it -> Stream.ofNullable(it.getLocations())
                    .flatMap(Collection::stream)
                    .findFirst()
                    .map(BaseDocumentSignatureField::getTopLeftY)
                    .orElse(null));

    private static final Comparator<DocumentSignatureTemplateField> FIELD_COMPARATOR_BY_TOP_LEFT_X =
            Comparator.comparing(it -> Stream.ofNullable(it.getLocations())
                    .flatMap(Collection::stream)
                    .findFirst()
                    .map(BaseDocumentSignatureField::getTopLeftX)
                    .orElse(null));

    private static final Comparator<DocumentSignatureTemplateField> FIELD_COMPARATOR =
            FIELD_COMPARATOR_BY_PAGE_NO
                    .thenComparing(FIELD_COMPARATOR_BY_TOP_LEFT_Y)
                    .thenComparing(FIELD_COMPARATOR_BY_TOP_LEFT_X);


    private final ObjectMapper objectMapper;

    private final Map<ToolboxRequesterFieldTypeCode, RequesterFieldJsonSchemaService> requesterFieldServices;

    @Autowired
    public DocumentSignatureTemplateJsonSchemaServiceImpl(
            ObjectMapper objectMapper,
            List<RequesterFieldJsonSchemaService> requesterFieldServices
    ) {
        this.objectMapper = objectMapper;
        this.requesterFieldServices = requesterFieldServices.stream()
                .flatMap(service -> service.getSupportedFieldTypes().stream()
                        .map(type -> Pair.of(type, service)))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    @Override
    public void fillJsonSchemasGeneratedByTemplateFields(DocumentSignatureTemplate template) {

        var fieldToSchemasList = getFieldNameToFieldSchemasList(template);

        template.setFormSchema(generateJsonSchema(fieldToSchemasList));
        template.setFormUiSchema(generateJsonUiSchema(fieldToSchemasList));
    }

    @Override
    public void fillJsonSchemasForToolboxRequesterField(
            DocumentSignatureTemplateField field,
            DocumentSignatureTemplateFieldProperties properties
    ) {
        Optional.ofNullable(field.getToolboxRequesterType())
                .map(DocumentSignatureTemplateToolboxRequesterFieldType::getCode)
                .map(requesterFieldServices::get)
                .ifPresent(service -> service.populateSchema(field, properties));
    }

    @Override
    public DocumentSignatureTemplateFieldProperties extractPropertiesFromJsonSchemas(DocumentSignatureTemplateField field) {
        return Optional.ofNullable(field.getToolboxRequesterType())
                .map(DocumentSignatureTemplateToolboxRequesterFieldType::getCode)
                .map(requesterFieldServices::get)
                .map(service -> service.extractFieldProperties(field))
                .orElse(null);
    }

    @Override
    public String generateJsonSchema(
            DocumentSignatureTemplate template,
            Predicate<DocumentSignatureTemplateField> fieldFilter
    ) {
        return generateJsonSchema(getFieldNameToFieldSchemasList(template, fieldFilter));
    }

    @Override
    public String generateJsonUiSchema(
            DocumentSignatureTemplate template,
            Predicate<DocumentSignatureTemplateField> fieldFilter
    ) {
        return generateJsonUiSchema(getFieldNameToFieldSchemasList(template, fieldFilter));
    }

    private List<Pair<String, Pair<String, String>>> getFieldNameToFieldSchemasList(
            DocumentSignatureTemplate template,
            Predicate<DocumentSignatureTemplateField> fieldPredicate
    ) {
        return template.getFields().stream()
                .filter(fieldPredicate)
                .filter(it -> it.getJsonSchema() != null && it.getJsonUiSchema() != null)
                .sorted(FIELD_COMPARATOR)
                .map(field -> Pair.of(
                        field.getName(),
                        Pair.of(
                                field.getJsonSchema(),
                                field.getJsonUiSchema()
                        )
                ))
                .collect(Collectors.toList());
    }

    private List<Pair<String, Pair<String, String>>> getFieldNameToFieldSchemasList(DocumentSignatureTemplate template) {
        return getFieldNameToFieldSchemasList(template, f -> true);
    }

    private String generateJsonSchema(List<Pair<String, Pair<String, String>>> fieldNameToFieldSchemasList) {

        if (!fieldNameToFieldSchemasList.isEmpty()) {

            var fields = fieldNameToFieldSchemasList.stream()
                    .map(it -> Pair.of(it.getFirst(), it.getSecond().getFirst()))
                    .collect(Collectors.toList());

            var jsonSchema = objectMapper.createObjectNode();
            jsonSchema.put("type", "object");
            var properties = jsonSchema.putObject("properties");

            fields.forEach(it -> properties.putRawValue(it.getFirst(), new RawValue(it.getSecond())));
            try {
                return objectMapper.writeValueAsString(jsonSchema);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    private String generateJsonUiSchema(List<Pair<String, Pair<String, String>>> fieldNameToFieldSchemasList) {
        if (!fieldNameToFieldSchemasList.isEmpty()) {
            var jsonUiSchema = objectMapper.createObjectNode();

            var fields = fieldNameToFieldSchemasList.stream()
                    .map(it -> Pair.of(it.getFirst(), it.getSecond().getSecond()))
                    .collect(Collectors.toList());

            fields.forEach(it -> jsonUiSchema.putRawValue(it.getFirst(), new RawValue(it.getSecond())));

            var fieldNames = fields.stream()
                    .map(Pair::getFirst)
                    .collect(Collectors.toList());

            jsonUiSchema.set("ui:grid", generateUiGrid(fieldNames));
            try {
                return objectMapper.writeValueAsString(jsonUiSchema);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    private JsonNode generateUiGrid(List<String> fieldNames) {
        var uiGrid = objectMapper.createArrayNode();

        for (int i = 0; i < (fieldNames.size() + 1) / 2; i++) {
            var uiGridLine = uiGrid.addObject();
            uiGridLine.putObject(fieldNames.get(i * 2))
                    .put("md", 6);
            if (i * 2 + 1 < fieldNames.size()) {
                uiGridLine.putObject(fieldNames.get(i * 2 + 1))
                        .put("md", 6);
            }
        }

        return uiGrid;
    }
}
