package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;

public interface FieldDefaultValueBuilder {

    TemplateFieldDefaultValueType getTemplateFieldType();

    Object build(DocumentSignatureTemplateContext context);
}
