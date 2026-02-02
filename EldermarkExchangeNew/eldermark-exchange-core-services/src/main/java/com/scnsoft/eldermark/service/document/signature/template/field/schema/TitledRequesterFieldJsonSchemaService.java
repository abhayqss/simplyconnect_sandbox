package com.scnsoft.eldermark.service.document.signature.template.field.schema;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateFieldProperties;
import com.scnsoft.eldermark.entity.signature.ToolboxRequesterFieldTypeCode;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumSet;

@Service
public class TitledRequesterFieldJsonSchemaService extends AbstractRequesterFieldJsonSchemaService {

    @Override
    protected ObjectNode fillJsonSchema(ObjectNode json, DocumentSignatureTemplateFieldProperties properties) {
        return json.put("title", properties.getLabel());
    }

    @Override
    protected void extractPropertiesFromJsonSchema(ObjectNode json, DocumentSignatureTemplateFieldProperties properties) {
        properties.setLabel(json.get("title").textValue());
    }

    @Override
    public Collection<ToolboxRequesterFieldTypeCode> getSupportedFieldTypes() {
        return EnumSet.of(
                ToolboxRequesterFieldTypeCode.DATE_BOX,
                ToolboxRequesterFieldTypeCode.INPUT_BOX
        );
    }
}
