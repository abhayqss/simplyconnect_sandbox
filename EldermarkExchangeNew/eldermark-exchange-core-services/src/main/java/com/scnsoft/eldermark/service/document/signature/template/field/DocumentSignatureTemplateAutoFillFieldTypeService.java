package com.scnsoft.eldermark.service.document.signature.template.field;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateAutoFillFieldType;

import java.util.List;
import java.util.Map;

public interface DocumentSignatureTemplateAutoFillFieldTypeService {

    Map<Long, DocumentSignatureTemplateAutoFillFieldType> getTypesMapById();

    List<DocumentSignatureTemplateAutoFillFieldType> findAll();
}
