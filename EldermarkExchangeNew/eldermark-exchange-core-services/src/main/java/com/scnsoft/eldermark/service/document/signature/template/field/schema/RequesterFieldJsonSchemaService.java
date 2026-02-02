package com.scnsoft.eldermark.service.document.signature.template.field.schema;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateFieldProperties;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.ToolboxRequesterFieldTypeCode;

import java.util.Collection;

public interface RequesterFieldJsonSchemaService {

    void populateSchema(DocumentSignatureTemplateField field, DocumentSignatureTemplateFieldProperties properties);

    DocumentSignatureTemplateFieldProperties extractFieldProperties(DocumentSignatureTemplateField field);

    Collection<ToolboxRequesterFieldTypeCode> getSupportedFieldTypes();
}
