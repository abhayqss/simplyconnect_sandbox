package com.scnsoft.eldermark.service.document.signature.template.field.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateFieldProperties;
import com.scnsoft.eldermark.entity.signature.ToolboxRequesterFieldTypeCode;
import com.scnsoft.eldermark.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EnumListRequesterFieldJsonSchemaService extends AbstractRequesterFieldJsonSchemaService {

    @Override
    protected ObjectNode fillJsonSchema(ObjectNode json, DocumentSignatureTemplateFieldProperties properties) {
        json.put("title", properties.getLabel());
        if (CollectionUtils.isNotEmpty(properties.getValues())) {
            var enumList = json.putObject("items").putArray("enum");
            properties.getValues().forEach(enumList::add);
        } else {
            throw new BusinessException("values should not be empty");
        }
        return json;
    }

    @Override
    protected void extractPropertiesFromJsonSchema(ObjectNode json, DocumentSignatureTemplateFieldProperties properties) {
        properties.setLabel(json.get("title").textValue());
        properties.setValues(
                Optional.ofNullable(json.get("items"))
                        .map(it -> it.get("enum"))
                        .stream()
                        .flatMap(it -> StreamSupport.stream(it.spliterator(), false))
                        .map(JsonNode::textValue)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Collection<ToolboxRequesterFieldTypeCode> getSupportedFieldTypes() {
        return EnumSet.of(ToolboxRequesterFieldTypeCode.CHECKBOX);
    }
}
