package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateFieldProperties;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;

import java.util.function.Predicate;

public interface DocumentSignatureTemplateJsonSchemaService {

    void fillJsonSchemasGeneratedByTemplateFields(DocumentSignatureTemplate template);

    void fillJsonSchemasForToolboxRequesterField(
            DocumentSignatureTemplateField field,
            DocumentSignatureTemplateFieldProperties properties
    );

    DocumentSignatureTemplateFieldProperties extractPropertiesFromJsonSchemas(DocumentSignatureTemplateField field);

    String generateJsonSchema(DocumentSignatureTemplate template, Predicate<DocumentSignatureTemplateField> fieldFilter);

    String generateJsonUiSchema(DocumentSignatureTemplate template, Predicate<DocumentSignatureTemplateField> fieldFilter);
}
