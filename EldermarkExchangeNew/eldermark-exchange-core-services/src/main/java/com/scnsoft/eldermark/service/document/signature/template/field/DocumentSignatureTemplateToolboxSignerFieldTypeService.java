package com.scnsoft.eldermark.service.document.signature.template.field;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateToolboxSignerFieldType;

import java.util.List;
import java.util.Map;

public interface DocumentSignatureTemplateToolboxSignerFieldTypeService {
    List<DocumentSignatureTemplateToolboxSignerFieldType> findAll();

    Map<Long, DocumentSignatureTemplateToolboxSignerFieldType> getTypesMapById();
}
